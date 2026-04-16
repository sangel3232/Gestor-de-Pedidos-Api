package com.sergio.gestor_pedidos.service;

import com.sergio.gestor_pedidos.dto.PagoRequestDTO;
import com.sergio.gestor_pedidos.dto.PagoResponseDTO;
import com.sergio.gestor_pedidos.dto.CambioEstadoDTO;
import com.sergio.gestor_pedidos.exception.RecursoNoEncontradoException;
import com.sergio.gestor_pedidos.exception.ReglaDeNegocioException;
import com.sergio.gestor_pedidos.mapper.PagoMapper;
import com.sergio.gestor_pedidos.model.*;
import com.sergio.gestor_pedidos.repository.PagoRepository;
import com.sergio.gestor_pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final PasarelaPagoService pasarelaPagoService;
    private final PedidoService pedidoService;
    private final PagoMapper pagoMapper;

    @Transactional
    public PagoResponseDTO procesarPago(PagoRequestDTO dto) {
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Pedido no encontrado con id: " + dto.getPedidoId()
                ));

        if (pedido.getEstado() == EstadoPedido.PAGADO) {
            throw new ReglaDeNegocioException("El pedido ya fue pagado");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new ReglaDeNegocioException("No se puede pagar un pedido cancelado");
        }
        if (pedido.getEstado() == EstadoPedido.CREADO) {
            throw new ReglaDeNegocioException("El pedido debe estar CONFIRMADO antes de pagar");
        }

        // Validar que no exista pago previo completado
        pagoRepository.findByPedidoId(dto.getPedidoId()).ifPresent(p -> {
            if (p.getEstado() == EstadoPago.COMPLETADO) {
                throw new ReglaDeNegocioException(
                        "Ya existe un pago completado para este pedido"
                );
            }
        });

        // Crear pago en estado PENDIENTE
        Pago pago = Pago.builder()
                .pedido(pedido)
                .monto(pedido.getTotal())
                .metodoPago(dto.getMetodoPago())
                .estado(EstadoPago.PENDIENTE)
                .build();

        pago = pagoRepository.save(pago);

        // Procesar pago en pasarela
        PasarelaPagoService.ResultadoPago resultado =
                pasarelaPagoService.procesarPago(dto, pedido.getTotal());

        pago.setProcesadoEn(LocalDateTime.now());
        pago.setMensajeRespuesta(resultado.mensaje());

        if (resultado.exitoso()) {

            pago.setEstado(EstadoPago.COMPLETADO);
            pago.setReferenciaExterna(resultado.referencia());
            pagoRepository.save(pago);

            log.info(
                    "Pago completado y pendiente de confirmación admin - pedido: {}, referencia: {}",
                    pedido.getId(),
                    resultado.referencia()
            );

        } else {

            pago.setEstado(EstadoPago.FALLIDO);
            pagoRepository.save(pago);

            log.warn(
                    "Pago fallido - pedido: {}, motivo: {}",
                    pedido.getId(),
                    resultado.mensaje()
            );
        }

        return pagoMapper.toResponse(pago);
    }

    public PagoResponseDTO obtenerPorId(Long id) {
        return pagoMapper.toResponse(
                pagoRepository.findById(id)
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "Pago no encontrado con id: " + id
                        ))
        );
    }

    @Transactional
    public PagoResponseDTO confirmarPago(Long pagoId) {
        Pago pago = buscarPago(pagoId);
        if (pago.getEstado() != EstadoPago.COMPLETADO) {
            throw new ReglaDeNegocioException("Solo se pueden confirmar pagos completados");
        }
        Pedido pedido = pago.getPedido();
        if (pedido.getEstado() != EstadoPedido.CONFIRMADO) {
            throw new ReglaDeNegocioException("El pedido debe estar CONFIRMADO para marcarlo como PAGADO");
        }
        CambioEstadoDTO cambio = new CambioEstadoDTO();
        cambio.setEstado(EstadoPedido.PAGADO);
        pedidoService.cambiarEstado(pedido.getId(), cambio);
        return pagoMapper.toResponse(pago);
    }

    @Transactional
    public PagoResponseDTO reembolsarPago(Long pagoId) {
        Pago pago = buscarPago(pagoId);
        if (pago.getEstado() != EstadoPago.COMPLETADO) {
            throw new ReglaDeNegocioException("Solo se pueden reembolsar pagos completados");
        }

        boolean exito = pasarelaPagoService.reembolsarPago(pago.getReferenciaExterna(), pago.getMonto());
        if (!exito) {
            throw new ReglaDeNegocioException("No se pudo procesar el reembolso");
        }

        pago.setEstado(EstadoPago.REEMBOLSADO);
        pago.setMensajeRespuesta("Pago reembolsado");
        pagoRepository.save(pago);

        CambioEstadoDTO cambio = new CambioEstadoDTO();
        cambio.setEstado(EstadoPedido.REEMBOLSADO);
        pedidoService.cambiarEstado(pago.getPedido().getId(), cambio);

        return pagoMapper.toResponse(pago);
    }

    private Pago buscarPago(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado con id: " + id));
    }

    public PagoResponseDTO obtenerPorPedido(Long pedidoId) {
        return pagoMapper.toResponse(
                pagoRepository.findByPedidoId(pedidoId)
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "No existe pago para el pedido: " + pedidoId
                        ))
        );
    }

    public List<PagoResponseDTO> listarTodos() {
        return pagoRepository.findAll()
                .stream()
                .map(pagoMapper::toResponse)
                .toList();
    }

    public List<PagoResponseDTO> listarPorCliente(Long clienteId) {
        return pagoRepository.findByClienteId(clienteId)
                .stream()
                .map(pagoMapper::toResponse)
                .toList();
    }
}
