package com.martillo.seguridad;

import java.util.EnumMap;
import java.util.Map;

/**
 * Matriz de acceso por rol y modulo (HU-09).
 *
 * Restriccion heredada de la seccion 10 de arc42: solo el responsable de
 * nomina alcanza los datos de empleados y salarios, ni siquiera el
 * administrador.
 */
public final class MatrizAcceso {

    private static final Map<Rol, Map<Modulo, Permiso>> MATRIZ = new EnumMap<>(Rol.class);

    static {
        MATRIZ.put(Rol.ADMINISTRADOR, permisos(
                Permiso.TOTAL,   // ADMINISTRACION
                Permiso.TOTAL,   // CONSIGNANTES_Y_CATALOGO
                Permiso.TOTAL,   // INVENTARIO
                Permiso.NINGUNO, // NOMINA
                Permiso.NINGUNO  // AUDITORIA
        ));
        MATRIZ.put(Rol.RESPONSABLE_NOMINA, permisos(
                Permiso.NINGUNO, Permiso.NINGUNO, Permiso.NINGUNO, Permiso.TOTAL, Permiso.NINGUNO));
        MATRIZ.put(Rol.CATALOGADOR, permisos(
                Permiso.NINGUNO, Permiso.TOTAL, Permiso.TOTAL, Permiso.NINGUNO, Permiso.NINGUNO));
        MATRIZ.put(Rol.AUDITOR, permisos(
                Permiso.NINGUNO, Permiso.LECTURA, Permiso.LECTURA, Permiso.NINGUNO, Permiso.TOTAL));
    }

    private MatrizAcceso() {
    }

    private static Map<Modulo, Permiso> permisos(Permiso administracion, Permiso catalogo,
                                                 Permiso inventario, Permiso nomina, Permiso auditoria) {
        Map<Modulo, Permiso> fila = new EnumMap<>(Modulo.class);
        fila.put(Modulo.ADMINISTRACION, administracion);
        fila.put(Modulo.CONSIGNANTES_Y_CATALOGO, catalogo);
        fila.put(Modulo.INVENTARIO, inventario);
        fila.put(Modulo.NOMINA, nomina);
        fila.put(Modulo.AUDITORIA, auditoria);
        return fila;
    }

    public static Permiso permisoDe(Rol rol, Modulo modulo) {
        if (rol == null) return Permiso.NINGUNO;
        return MATRIZ.get(rol).getOrDefault(modulo, Permiso.NINGUNO);
    }

    /** Modulos que el rol puede ver en el menu, con su nivel de acceso. */
    public static Map<Modulo, Permiso> menuDe(Rol rol) {
        Map<Modulo, Permiso> visibles = new EnumMap<>(Modulo.class);
        for (Modulo modulo : Modulo.values()) {
            Permiso permiso = permisoDe(rol, modulo);
            if (permiso != Permiso.NINGUNO) visibles.put(modulo, permiso);
        }
        return visibles;
    }
}
