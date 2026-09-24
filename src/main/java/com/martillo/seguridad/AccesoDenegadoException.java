package com.martillo.seguridad;

/** Se lanza cuando un rol intenta alcanzar un modulo que la matriz no le permite. */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
