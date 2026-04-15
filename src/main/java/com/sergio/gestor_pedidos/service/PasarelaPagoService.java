package com.sergio.gestor_pedidos.service;

import com.sergio.gestor_pedidos.dto.PagoRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Pasarela de pagos simulada.
 * En producción, aquí se integraría con Stripe, PayPal, MercadoPago, etc.
 */
@Service
@Slf4j
public class PasarelaPagoService {

    public record ResultadoPago(boolean exitoso, String referencia, String mensaje) {}

    public ResultadoPago procesarPago(PagoRequestDTO dto, BigDecimal monto) {
        log.info("Procesando pago simulado - método: {}, monto: {}", dto.getMetodoPago(), monto);

        // Simulación: tarjetas que terminan en 0000 son rechazadas
        if (dto.getNumeroTarjeta() != null && dto.getNumeroTarjeta().endsWith("0000")) {
            log.warn("Pago rechazado (simulación) para tarjeta terminada en 0000");
            return new ResultadoPago(false, null, "Pago rechazado: fondos insuficientes");
        }

        // Simular latencia de procesamiento
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        String referencia = "PAY-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        log.info("Pago aprobado - referencia: {}", referencia);
        return new ResultadoPago(true, referencia, "Pago aprobado exitosamente");
    }
}
