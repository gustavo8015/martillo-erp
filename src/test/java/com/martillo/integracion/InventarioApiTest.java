package com.martillo.integracion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integracion de HU-08 con los datos de arranque de la demostracion:
 * cajas de embalaje EMB-001 con 40 unidades y umbral critico de 25.
 *
 * Cada prueba parte de un contexto limpio y de una base H2 propia, para que
 * el stock no dependa del orden de ejecucion ni de otras clases de prueba que
 * mantienen abierta la base compartida del perfil por defecto.
 */
@SpringBootTest
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:inventario-${random.uuid}")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InventarioApiTest {

    @Autowired
    private MockMvc mvc;

    private String movimiento(String codigo, int cantidad) {
        return "{\"codigo\":\"" + codigo + "\",\"cantidad\":" + cantidad + "}";
    }

    @Test
    @DisplayName("CP-08.1 Una salida de 16 cajas deja el item en critico y genera una alerta")
    void salidaQueCruzaElUmbral() throws Exception {
        mvc.perform(post("/api/inventario/salidas").header("X-Rol", "CATALOGADOR")
                        .contentType(MediaType.APPLICATION_JSON).content(movimiento("EMB-001", 16)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(24))
                .andExpect(jsonPath("$.estado").value("CRITICO"));

        mvc.perform(get("/api/inventario/alertas").header("X-Rol", "CATALOGADOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigoItem").value("EMB-001"));
    }

    @Test
    @DisplayName("CP-08.2 La reposicion devuelve el item a normal y deja sin alertas pendientes")
    void reposicionCierraLaAlerta() throws Exception {
        mvc.perform(post("/api/inventario/salidas").header("X-Rol", "CATALOGADOR")
                .contentType(MediaType.APPLICATION_JSON).content(movimiento("EMB-001", 16)));

        mvc.perform(post("/api/inventario/entradas").header("X-Rol", "CATALOGADOR")
                        .contentType(MediaType.APPLICATION_JSON).content(movimiento("EMB-001", 10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(34))
                .andExpect(jsonPath("$.estado").value("NORMAL"));

        mvc.perform(get("/api/inventario/alertas").header("X-Rol", "CATALOGADOR"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("CP-08.3 Una salida mayor al stock disponible se rechaza con 400")
    void salidaMayorAlStock() throws Exception {
        mvc.perform(post("/api/inventario/salidas").header("X-Rol", "CATALOGADOR")
                        .contentType(MediaType.APPLICATION_JSON).content(movimiento("EMB-001", 41)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CP-08.4 Un codigo inexistente responde 404")
    void codigoInexistente() throws Exception {
        mvc.perform(post("/api/inventario/salidas").header("X-Rol", "CATALOGADOR")
                        .contentType(MediaType.APPLICATION_JSON).content(movimiento("NO-EXISTE", 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("CP-09.3 El auditor lee el inventario pero no puede registrar salidas")
    void auditorSoloLectura() throws Exception {
        mvc.perform(get("/api/inventario/items").header("X-Rol", "AUDITOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));

        mvc.perform(post("/api/inventario/salidas").header("X-Rol", "AUDITOR")
                        .contentType(MediaType.APPLICATION_JSON).content(movimiento("EMB-001", 1)))
                .andExpect(status().isForbidden());
    }
}
