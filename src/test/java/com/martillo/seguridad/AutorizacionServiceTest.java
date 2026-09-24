package com.martillo.seguridad;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/** La autorizacion se verifica en el servidor y todo intento queda auditado. */
@ExtendWith(MockitoExtension.class)
class AutorizacionServiceTest {

    @Mock
    private AuditoriaService auditoria;

    @InjectMocks
    private AutorizacionService autorizacion;

    @Test
    @DisplayName("El catalogador que llama a nomina recibe acceso denegado y queda registrado")
    void catalogadorNoAlcanzaNomina() {
        assertThrows(AccesoDenegadoException.class,
                () -> autorizacion.exigir("CATALOGADOR", Modulo.NOMINA, Permiso.TOTAL));

        verify(auditoria).registrar(eq("CATALOGADOR"), eq("ACCESO_DENEGADO"), eq("NOMINA"), eq(false));
    }

    @Test
    @DisplayName("El responsable de nomina pasa y el acceso queda registrado")
    void responsableDeNominaPasa() {
        assertDoesNotThrow(() -> autorizacion.exigir("responsable_nomina", Modulo.NOMINA, Permiso.TOTAL));

        verify(auditoria).registrar(eq("RESPONSABLE_NOMINA"), eq("ACCESO_PERMITIDO"), eq("NOMINA"), eq(true));
    }

    @Test
    @DisplayName("Una peticion sin rol se rechaza")
    void peticionSinRol() {
        assertThrows(AccesoDenegadoException.class,
                () -> autorizacion.exigir(null, Modulo.INVENTARIO, Permiso.LECTURA));
    }

    @Test
    @DisplayName("El auditor puede leer el inventario pero no modificarlo")
    void auditorLeePeroNoModifica() {
        assertDoesNotThrow(() -> autorizacion.exigir("AUDITOR", Modulo.INVENTARIO, Permiso.LECTURA));
        assertThrows(AccesoDenegadoException.class,
                () -> autorizacion.exigir("AUDITOR", Modulo.INVENTARIO, Permiso.TOTAL));
    }
}
