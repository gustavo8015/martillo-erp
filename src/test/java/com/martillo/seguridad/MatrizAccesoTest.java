package com.martillo.seguridad;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verificacion de la matriz de acceso por rol y modulo (HU-09). */
class MatrizAccesoTest {

    @Test
    @DisplayName("Solo el responsable de nomina alcanza el modulo de nomina")
    void nominaSoloParaSuResponsable() {
        assertEquals(Permiso.TOTAL, MatrizAcceso.permisoDe(Rol.RESPONSABLE_NOMINA, Modulo.NOMINA));
        assertEquals(Permiso.NINGUNO, MatrizAcceso.permisoDe(Rol.ADMINISTRADOR, Modulo.NOMINA));
        assertEquals(Permiso.NINGUNO, MatrizAcceso.permisoDe(Rol.CATALOGADOR, Modulo.NOMINA));
        assertEquals(Permiso.NINGUNO, MatrizAcceso.permisoDe(Rol.AUDITOR, Modulo.NOMINA));
    }

    @Test
    @DisplayName("El auditor consulta pero no modifica el catalogo y el inventario")
    void auditorSoloLectura() {
        assertEquals(Permiso.LECTURA, MatrizAcceso.permisoDe(Rol.AUDITOR, Modulo.INVENTARIO));
        assertTrue(MatrizAcceso.permisoDe(Rol.AUDITOR, Modulo.INVENTARIO).cubre(Permiso.LECTURA));
        assertFalse(MatrizAcceso.permisoDe(Rol.AUDITOR, Modulo.INVENTARIO).cubre(Permiso.TOTAL));
    }

    @Test
    @DisplayName("El menu del catalogador no incluye nomina ni administracion")
    void menuDelCatalogador() {
        var menu = MatrizAcceso.menuDe(Rol.CATALOGADOR);
        assertEquals(2, menu.size());
        assertTrue(menu.containsKey(Modulo.CONSIGNANTES_Y_CATALOGO));
        assertTrue(menu.containsKey(Modulo.INVENTARIO));
        assertFalse(menu.containsKey(Modulo.NOMINA));
        assertFalse(menu.containsKey(Modulo.ADMINISTRACION));
    }

    @Test
    @DisplayName("Un rol desconocido no tiene permiso sobre ningun modulo")
    void rolNuloSinPermisos() {
        assertEquals(Permiso.NINGUNO, MatrizAcceso.permisoDe(null, Modulo.INVENTARIO));
    }
}
