package com.bancoxyz.bank_batch_system.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

public final class FechaUtils {

    private static final List<DateTimeFormatter> FORMATOS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu/MM/dd").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)
    );

    private FechaUtils() {
    }

    public static boolean esFechaValida(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return false;
        }
        String valor = fecha.trim();
        for (DateTimeFormatter formato : FORMATOS) {
            try {
                LocalDate.parse(valor, formato);
                return true;
            } catch (DateTimeParseException ignored) {
                // se intenta con el siguiente formato
            }
        }
        return false;
    }
}
