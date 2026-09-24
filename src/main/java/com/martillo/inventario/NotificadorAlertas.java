package com.martillo.inventario;

/** Puerto de notificacion de la alerta de stock critico (correo, panel, etc.). */
public interface NotificadorAlertas {

    void notificar(AlertaStock alerta);
}
