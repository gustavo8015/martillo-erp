package com.martillo.seguridad;

/**
 * Puerto de verificacion del token de reCAPTCHA (HU-11). Permite sustituir el
 * proveedor o usar un doble de prueba sin tocar la logica de inicio de sesion.
 */
public interface VerificadorRecaptcha {

    ResultadoRecaptcha verificar(String token, String ipRemota);
}
