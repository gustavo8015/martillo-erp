package com.martillo.integracion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integracion de HU-09 y HU-11 sobre los controladores reales.
 *
 * En el entorno de pruebas no existe la variable MARTILLO_RECAPTCHA_SECRETO,
 * de modo que estas pruebas comprueban el comportamiento de falla cerrada:
 * sin clave secreta el servidor no puede afirmar que quien entra es una
 * persona y rechaza el inicio de sesion aunque la contrasena sea correcta.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("CP-11.1 Sin clave secreta configurada el inicio de sesion falla cerrado con 401")
    void loginFallaCerradoSinSecreto() throws Exception {
        mvc.perform(post("/api/seguridad/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"nomina\",\"contrasena\":\"martillo2026\",\"tokenRecaptcha\":\"token\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("CP-11.2 Una peticion sin token de verificacion se rechaza con 400")
    void loginSinToken() throws Exception {
        mvc.perform(post("/api/seguridad/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"nomina\",\"contrasena\":\"martillo2026\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.tokenRecaptcha").value("Falta el token de verificacion"));
    }

    @Test
    @DisplayName("CP-09.4 El menu del catalogador incluye catalogo e inventario y excluye nomina")
    void menuDelCatalogador() throws Exception {
        mvc.perform(get("/api/seguridad/menu").header("X-Rol", "CATALOGADOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.CONSIGNANTES_Y_CATALOGO").value("TOTAL"))
                .andExpect(jsonPath("$.INVENTARIO").value("TOTAL"))
                .andExpect(jsonPath("$.NOMINA").doesNotExist());
    }

    @Test
    @DisplayName("CP-09.5 La pista de auditoria responde al auditor y rechaza al administrador")
    void auditoriaSoloParaAuditor() throws Exception {
        mvc.perform(get("/api/seguridad/auditoria").header("X-Rol", "AUDITOR"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/seguridad/auditoria").header("X-Rol", "ADMINISTRADOR"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CP-REL.1 La sonda de salud del servicio responde UP")
    void sondaDeSalud() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
