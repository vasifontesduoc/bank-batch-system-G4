# Bank Batch System — Banco XYZ

Proyecto Spring Boot del Banco XYZ: procesos batch (Semana 3) + patrón BFF (Semana 4).

## Semana 4 — Backend for Frontend (BFF)

Se implementaron 3 backends independientes, cada uno adaptado a su cliente, sobre la misma base de datos generada por los Jobs batch.

### Estrategia elegida

BFF por canal (Web/Móvil/Cajero), cada uno con sus propios endpoints, DTOs y reglas de autorización — pero dentro de la misma app Spring Boot (no microservicios separados), por tamaño y contexto del proyecto.

### Los 3 BFF

| Canal | Ruta base | Qué expone |
|---|---|---|
| **Web** | `/api/web/**` | Datos completos: estado de cuenta anual detallado, transacciones con anomalías, historial de intereses |
| **Móvil** | `/api/mobile/**` | Payloads livianos: solo saldo y últimas transacciones (fecha/monto/tipo) |
| **Cajero** | `/api/cajero/**` | Solo operaciones críticas: consultar saldo y retirar (con validación de fondos) |

### Seguridad por canal

Autenticación por API Key en el header `X-API-KEY`. Cada key mapea a un rol (`ROLE_WEB`/`ROLE_MOBILE`/`ROLE_CAJERO`) y Spring Security exige el rol correcto según la ruta:

| Canal | Header |
|---|---|
| Web | `X-API-KEY: web-2024-xyz-key` |
| Móvil | `X-API-KEY: mobile-2024-xyz-key` |
| Cajero | `X-API-KEY: cajero-2024-xyz-key` |

### Organización del código

Por capa técnica (`controller/`, `service/`, `dto/`), con el canal explícito en el nombre de cada clase (`WebBffController`, `MobileBffService`, etc.) y los DTOs además separados por subcarpeta (`dto/web`, `dto/mobile`, `dto/cajero`) — misma convención que ya usa el resto del proyecto batch.

### Cómo probar

```bash
mvn spring-boot:run
```

```bash
# Web
curl -H "X-API-KEY: web-2024-xyz-key" http://localhost:8080/api/web/cuentas/101/estado-anual

# Móvil
curl -H "X-API-KEY: mobile-2024-xyz-key" http://localhost:8080/api/mobile/cuentas/101/saldo

# Cajero - saldo
curl -H "X-API-KEY: cajero-2024-xyz-key" http://localhost:8080/api/cajero/cuentas/101/saldo

# Cajero - retiro
curl -X POST -H "X-API-KEY: cajero-2024-xyz-key" -H "Content-Type: application/json" \
  -d '{"monto": 500}' http://localhost:8080/api/cajero/cuentas/101/retiro

## Evidencia de ejecución

Capturas de cada endpoint, la prueba de seguridad entre canales (403), validación de fondos insuficientes (409) y la verificación en MySQL de que el retiro persiste en la base de datos, están en el documento de entrega adjunto.
