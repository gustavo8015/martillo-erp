package com.martillo.seguridad;

import com.martillo.seguridad.dto.LoginRequest;
import com.martillo.seguridad.dto.LoginResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Inicio de sesion con verificacion reCAPTCHA v3 en el servidor (HU-11).
 *
 * El intento se acepta solo si la verificacion tuvo exito, la accion declarada
 * es "login" y la puntuacion alcanza el umbral. Cualquier rechazo queda en la
 * pista de auditoria sin revelar el motivo al usuario.
 */
@Service
public class LoginService {

    public static final String ACCION_ESPERADA = "login";
    public static final double PUNTUACION_MINIMA = 0.5;

    private final VerificadorRecaptcha verificador;
    private final DirectorioUsuarios directorio;
    private final AuditoriaService auditoria;

    public LoginService(VerificadorRecaptcha verificador, DirectorioUsuarios directorio, AuditoriaService auditoria) {
        this.verificador = verificador;
        this.directorio = directorio;
        this.auditoria = auditoria;
    }

    public LoginResponse iniciarSesion(LoginRequest peticion, String ipRemota) {
        ResultadoRecaptcha resultado = verificador.verificar(peticion.getTokenRecaptcha(), ipRemota);

        boolean humanoVerificado = resultado.exito()
                && ACCION_ESPERADA.equals(resultado.accion())
                && resultado.puntuacion() >= PUNTUACION_MINIMA;

        if (!humanoVerificado) {
            auditoria.registrar(peticion.getUsuario(), "LOGIN_RECHAZADO",
                    "Verificacion reCAPTCHA con puntuacion " + resultado.puntuacion(), false);
            throw new CredencialesInvalidasException();
        }

        Rol rol = directorio.autenticar(peticion.getUsuario(), peticion.getContrasena());
        if (rol == null) {
            auditoria.registrar(peticion.getUsuario(), "LOGIN_RECHAZADO", "Credenciales invalidas", false);
            throw new CredencialesInvalidasException();
        }

        auditoria.registrar(peticion.getUsuario(), "LOGIN_ACEPTADO",
                "Puntuacion " + resultado.puntuacion(), true);

        Map<String, String> menu = MatrizAcceso.menuDe(rol).entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), e -> e.getValue().name()));

        return new LoginResponse(peticion.getUsuario(), rol.name(), menu);
    }
}
