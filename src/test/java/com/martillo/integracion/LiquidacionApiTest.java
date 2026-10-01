package com.martillo.integracion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integracion de HU-07 sobre la API real, con el contexto completo
 * de Spring y la base H2 del perfil por defecto (Guia 8, plan de pruebas).
 *
 * Caracteristicas ISO/IEC 25010 cubiertas: adecuacion funcional (correccion)
 * y seguridad (confidencialidad de la nomina).
 */
@SpringBootTest
@AutoConfigureMockMvc
class LiquidacionApiTest {

    private static final String URL = "/api/nomina/liquidaciones";

    private static final String CASO_REFERENCIA = """
            {"tipoContrato":"INDEFINIDO","fechaIngreso":"2026-01-01","fechaRetiro":"2026-09-30",
             "salarioMensual":2500000,"motivo":"SIN_JUSTA_CAUSA"}""";

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("CP-07.1 El responsable de nomina obtiene el total de referencia de 6.372.159")
    void responsableDeNominaLiquida() throws Exception {
        mvc.perform(post(URL).header("X-Rol", "RESPONSABLE_NOMINA")
                        .contentType(MediaType.APPLICATION_JSON).content(CASO_REFERENCIA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diasTrabajados").value(270))
                .andExpect(jsonPath("$.cesantias").value(2061821))
                .andExpect(jsonPath("$.indemnizacion").value(2500000))
                .andExpect(jsonPath("$.total").value(6372159));
    }

    @Test
    @DisplayName("CP-07.2 Un contrato a termino fijo sin vencimiento pactado se rechaza con 400")
    void contratoFijoSinVencimiento() throws Exception {
        String peticion = CASO_REFERENCIA.replace("INDEFINIDO", "FIJO");
        mvc.perform(post(URL).header("X-Rol", "RESPONSABLE_NOMINA")
                        .contentType(MediaType.APPLICATION_JSON).content(peticion))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("CP-07.3 Un salario negativo se rechaza con el mensaje del campo")
    void salarioNegativo() throws Exception {
        String peticion = CASO_REFERENCIA.replace("2500000", "-1");
        mvc.perform(post(URL).header("X-Rol", "RESPONSABLE_NOMINA")
                        .contentType(MediaType.APPLICATION_JSON).content(peticion))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.salarioMensual").value("El salario mensual debe ser mayor a cero"));
    }

    @Test
    @DisplayName("CP-07.4 Una fecha de ingreso vacia responde 400 con el detalle por campo")
    void fechaIngresoVacia() throws Exception {
        String peticion = CASO_REFERENCIA.replace("\"2026-01-01\"", "\"\"");
        mvc.perform(post(URL).header("X-Rol", "RESPONSABLE_NOMINA")
                        .contentType(MediaType.APPLICATION_JSON).content(peticion))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error de validacion"))
                .andExpect(jsonPath("$.errores.fechaIngreso").value("La fecha de ingreso es obligatoria"));
    }

    @Test
    @DisplayName("CP-09.1 El administrador no alcanza la nomina aunque conozca la URL")
    void administradorRecibe403() throws Exception {
        mvc.perform(post(URL).header("X-Rol", "ADMINISTRADOR")
                        .contentType(MediaType.APPLICATION_JSON).content(CASO_REFERENCIA))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CP-09.2 Una peticion sin cabecera de rol se rechaza con 403")
    void sinRolRecibe403() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(CASO_REFERENCIA))
                .andExpect(status().isForbidden());
    }
}
