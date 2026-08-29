package com.bancoxyz.bank_batch_system.tasklet;

import com.bancoxyz.bank_batch_system.model.CuentaAnual;
import com.bancoxyz.bank_batch_system.model.EstadoCuentaAnual;
import com.bancoxyz.bank_batch_system.repository.CuentaAnualRepository;
import com.bancoxyz.bank_batch_system.repository.EstadoCuentaAnualRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EstadoCuentaAnualTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(EstadoCuentaAnualTasklet.class);

    private final CuentaAnualRepository movimientosRepository;
    private final EstadoCuentaAnualRepository estadoRepository;
    private final String outputDir;

    public EstadoCuentaAnualTasklet(CuentaAnualRepository movimientosRepository,
            EstadoCuentaAnualRepository estadoRepository,
            @Value("${batch.output.dir}") String outputDir) {
        this.movimientosRepository = movimientosRepository;
        this.estadoRepository = estadoRepository;
        this.outputDir = outputDir;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        List<CuentaAnual> movimientos = movimientosRepository.findAll();

        Map<Long, List<CuentaAnual>> porCuenta = movimientos.stream()
                .collect(Collectors.groupingBy(CuentaAnual::getCuentaId));

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        Files.createDirectories(Path.of(outputDir));
        Path archivo = Path.of(outputDir, "informe_auditoria_anual_" + timestamp + ".csv");

        try (FileWriter informe = new FileWriter(archivo.toFile())) {
            informe.write("cuenta_id,cantidad_movimientos,total_depositos,total_retiros,total_otros,saldo_final\n");

            for (Map.Entry<Long, List<CuentaAnual>> entry : porCuenta.entrySet()) {
                Long cuentaId = entry.getKey();
                List<CuentaAnual> movsCuenta = entry.getValue();

                double totalDepositos = sumaPorTipo(movsCuenta, "DEPOSITO");
                double totalRetiros = sumaPorTipo(movsCuenta, "RETIRO");
                double totalOtros = movsCuenta.stream()
                        .filter(m -> !"DEPOSITO".equals(m.getTransaccion()) && !"RETIRO".equals(m.getTransaccion()))
                        .mapToDouble(CuentaAnual::getMonto)
                        .sum();
                double saldoFinal = movsCuenta.stream().mapToDouble(CuentaAnual::getMonto).sum();

                EstadoCuentaAnual estado = new EstadoCuentaAnual();
                estado.setCuentaId(cuentaId);
                estado.setCantidadMovimientos(movsCuenta.size());
                estado.setTotalDepositos(totalDepositos);
                estado.setTotalRetiros(totalRetiros);
                estado.setTotalOtros(totalOtros);
                estado.setSaldoFinal(saldoFinal);
                estado.setFechaGeneracion(timestamp);
                estadoRepository.save(estado);

                informe.write(String.format("%d,%d,%.2f,%.2f,%.2f,%.2f%n",
                        cuentaId, movsCuenta.size(), totalDepositos, totalRetiros, totalOtros, saldoFinal));

                log.info("Estado de cuenta anual compilado -> cuenta={} movimientos={} saldoFinal={}",
                        cuentaId, movsCuenta.size(), saldoFinal);
            }
            log.info("Informe de auditoría anual escrito en {}", archivo.toAbsolutePath());
        } catch (IOException e) {
            log.error("No se pudo escribir el informe de auditoría anual: {}", e.getMessage());
            throw e;
        }

        log.info("Estados de cuenta anuales generados para {} cuenta(s).", porCuenta.size());

        return RepeatStatus.FINISHED;
    }

    private double sumaPorTipo(List<CuentaAnual> movimientos, String tipo) {
        return movimientos.stream()
                .filter(m -> tipo.equals(m.getTransaccion()))
                .mapToDouble(CuentaAnual::getMonto)
                .sum();
    }
}
