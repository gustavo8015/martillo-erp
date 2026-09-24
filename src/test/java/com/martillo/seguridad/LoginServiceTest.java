package com.martillo.seguridad;

import com.martillo.seguridad.dto.LoginRequest;
import com.martillo.seguridad.dto.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Verificacion reCAPTCHA v3 en el servidor (HU-11). El doble de prueba
 * sustituye al proveedor: la logica de aceptacion se prueba sin salir a la red.
 */
@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private AuditoriaService auditoria;

    private LoginService conPuntuacion(double puntuacion, String accion, boolean exito) {
        VerificadorRecaptcha doble = (token, ip) -> new ResultadoRecaptcha(exito, accion, puntuacion);
        return new LoginService(doble, new DirectorioUsuariosDemo(), auditoria);
    }

    private LoginRequest peticion(String usuario) {
        LoginRequest p = new LoginRequest();
        p.setUsuario(usuario);
        p.setContrasena("martillo2026");
        p.setTokenRecaptcha("token-de-prueba");
        return p;
    }

    @Test
    @DisplayName("Una puntuacion de 0,9 con accion login se acepta")
    void puntuacionAltaSeAcepta() {
        LoginResponse respuesta = conPuntuacion(0.9, "login", true)
                .iniciarSesion(peticion("nomina"), "127.0.0.1");

        assertEquals("RESPONSABLE_NOMINA", respuesta.getRol());
        assertEquals("TOTAL", respuesta.getMenu().get("NOMINA"));
        assertFalse(respuesta.getMenu().containsKey("ADMINISTRACION"));
        verify(auditoria).registrar(eq("nomina"), eq("LOGIN_ACEPTADO"), anyString(), eq(true));
    }

    @Test
    @DisplayName("Una puntuacion de 0,2 se rechaza y queda registrada")
    void puntuacionBajaSeRechaza() {
        LoginService servicio = conPuntuacion(0.2, "login", true);

        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.iniciarSesion(peticion("nomina"), "127.0.0.1"));

        verify(auditoria).registrar(eq("nomina"), eq("LOGIN_RECHAZADO"), anyString(), eq(false));
    }

    @Test
    @DisplayName("Un token emitido para otra accion se rechaza")
    void accionDistintaSeRechaza() {
        LoginService servicio = conPuntuacion(0.9, "registro", true);

        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.iniciarSesion(peticion("nomina"), "127.0.0.1"));
    }

    @Test
    @DisplayName("Sin exito en la verificacion no se evalua la contrasena")
    void verificacionFallidaSeRechaza() {
        LoginService servicio = conPuntuacion(0.9, "login", false);

        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.iniciarSesion(peticion("nomina"), "127.0.0.1"));
    }

    @Test
    @DisplayName("Un usuario que no existe se rechaza aunque la verificacion pase")
    void usuarioInexistenteSeRechaza() {
        LoginService servicio = conPuntuacion(0.9, "login", true);
        LoginRequest p = peticion("intruso");

        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.iniciarSesion(p, "127.0.0.1"));

        verify(auditoria).registrar(eq("intruso"), eq("LOGIN_RECHAZADO"), anyString(), anyBoolean());
    }
}
