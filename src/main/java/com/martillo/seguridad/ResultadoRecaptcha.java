package com.martillo.seguridad;

/**
 * Respuesta del punto de verificacion de Google.
 *
 * @param exito      la verificacion se completo sin errores
 * @param accion     accion declarada por el cliente; debe ser la esperada
 * @param puntuacion 0,0 (probablemente un robot) a 1,0 (probablemente una persona)
 */
public record ResultadoRecaptcha(boolean exito, String accion, double puntuacion) {
}
