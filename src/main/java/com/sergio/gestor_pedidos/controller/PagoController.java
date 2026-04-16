package com.sergio.gestor_pedidos.controller;

import com.sergio.gestor_pedidos.dto.PagoRequestDTO;
import com.sergio.gestor_pedidos.dto.PagoResponseDTO;
import com.sergio.gestor_pedidos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping("/procesar")
    @ResponseStatus(HttpStatus.CREATED)
    public PagoResponseDTO procesar(@Valid @RequestBody PagoRequestDTO dto) {
        return pagoService.procesarPago(dto);
    }

    @GetMapping
    public List<PagoResponseDTO> listar() {
        return pagoService.listarTodos();
    }

    @GetMapping("/{id}")
    public PagoResponseDTO obtener(@PathVariable Long id) {
        return pagoService.obtenerPorId(id);
    }

    @GetMapping("/pedido/{pedidoId}")
    public PagoResponseDTO porPedido(@PathVariable Long pedidoId) {
        return pagoService.obtenerPorPedido(pedidoId);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<PagoResponseDTO> porCliente(@PathVariable Long clienteId) {
        return pagoService.listarPorCliente(clienteId);
    }

    @PatchMapping("/{id}/confirmar")
    public PagoResponseDTO confirmar(@PathVariable Long id) {
        return pagoService.confirmarPago(id);
    }

    @PatchMapping("/{id}/reembolsar")
    public PagoResponseDTO reembolsar(@PathVariable Long id) {
        return pagoService.reembolsarPago(id);
    }
}
