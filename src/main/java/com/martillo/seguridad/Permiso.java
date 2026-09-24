package com.martillo.seguridad;

/** Nivel de acceso de un rol sobre un modulo. */
public enum Permiso {
    NINGUNO,
    LECTURA,
    TOTAL;

    /** Un permiso cubre a otro cuando es al menos tan amplio. */
    public boolean cubre(Permiso requerido) {
        return this.ordinal() >= requerido.ordinal();
    }
}
