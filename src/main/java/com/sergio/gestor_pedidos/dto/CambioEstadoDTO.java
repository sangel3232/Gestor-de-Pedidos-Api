package com.sergio.gestor_pedidos.dto;

import com.sergio.gestor_pedidos.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambioEstadoDTO {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoPedido estado;
}
