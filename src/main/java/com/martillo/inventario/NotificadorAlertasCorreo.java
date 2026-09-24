package com.martillo.inventario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Envio de la alerta por correo. En el Sprint 3 el envio se registra en la
 * bitacora del servicio: el proveedor de correo aparece como sistema externo
 * en el diagrama C4 de nivel 1 y se integra en un sprint posterior.
 */
@Component
public class NotificadorAlertasCorreo implements NotificadorAlertas {

    private static final Logger log = LoggerFactory.getLogger(NotificadorAlertasCorreo.class);

    @Override
    public void notificar(AlertaStock alerta) {
        log.warn("Alerta de stock critico enviada por correo: {} (stock {})",
                alerta.getMensaje(), alerta.getStockAlGenerar());
    }
}
