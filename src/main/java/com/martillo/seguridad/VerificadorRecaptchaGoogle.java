package com.martillo.seguridad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Verificacion en el servidor contra el punto de Google (ADR-003).
 *
 * La clave secreta llega por variable de entorno RECAPTCHA_SECRETO y nunca se
 * escribe en el repositorio, en linea con la revision automatica de
 * credenciales del flujo de integracion continua del Sprint 2.
 */
@Component
public class VerificadorRecaptchaGoogle implements VerificadorRecaptcha {

    private static final String PUNTO_VERIFICACION = "https://www.google.com/recaptcha/api/siteverify";

    private final RestClient cliente;
    private final String secreto;

    public VerificadorRecaptchaGoogle(@Value("${martillo.recaptcha.secreto:}") String secreto) {
        this.secreto = secreto;
        this.cliente = RestClient.create();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultadoRecaptcha verificar(String token, String ipRemota) {
        if (secreto == null || secreto.isBlank()) {
            // Sin clave configurada el sistema no puede afirmar que quien entra es una persona.
            return new ResultadoRecaptcha(false, null, 0.0);
        }

        MultiValueMap<String, String> cuerpo = new LinkedMultiValueMap<>();
        cuerpo.add("secret", secreto);
        cuerpo.add("response", token);
        if (ipRemota != null) cuerpo.add("remoteip", ipRemota);

        Map<String, Object> respuesta = cliente.post()
                .uri(PUNTO_VERIFICACION)
                .body(cuerpo)
                .retrieve()
                .body(Map.class);

        if (respuesta == null) return new ResultadoRecaptcha(false, null, 0.0);

        boolean exito = Boolean.TRUE.equals(respuesta.get("success"));
        String accion = (String) respuesta.get("action");
        double puntuacion = respuesta.get("score") instanceof Number n ? n.doubleValue() : 0.0;
        return new ResultadoRecaptcha(exito, accion, puntuacion);
    }
}
