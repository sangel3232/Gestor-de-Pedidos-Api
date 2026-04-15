package com.sergio.gestor_pedidos.dto;

import com.sergio.gestor_pedidos.model.EstadoPedido;

public class CambioEstadoDTO {

    private EstadoPedido estado;

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}