package com.martillo.seguridad;

/**
 * Rechazo del intento de inicio de sesion. El mensaje es deliberadamente
 * generico: no se revela si fallo la contrasena o la verificacion reCAPTCHA.
 */
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("No fue posible iniciar sesion con los datos proporcionados");
    }
}
