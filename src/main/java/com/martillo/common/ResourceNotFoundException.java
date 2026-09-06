package com.martillo.common;

/**
 * Excepcion generica para cuando un recurso solicitado no existe.
 * Vive en "common" porque la usan varios modulos (catalog, consignment, etc.),
 * tal como se documento en el diagrama de paquetes del proyecto.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
