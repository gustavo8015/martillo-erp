package com.martillo.nomina;

/**
 * Motivo por el que termina el contrato. Solo la terminacion unilateral
 * del empleador sin justa causa genera indemnizacion (CST, art. 64).
 */
public enum MotivoTerminacion {
    SIN_JUSTA_CAUSA,
    RENUNCIA,
    JUSTA_CAUSA
}
