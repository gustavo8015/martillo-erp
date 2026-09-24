package com.martillo.seguridad;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Directorio de demostracion del Sprint Review: cuatro usuarios, uno por rol.
 *
 * Las contrasenas de este directorio son de demostracion y el sistema no debe
 * llevarse a produccion con el: ahi se sustituye por el directorio
 * institucional a traves de la interfaz DirectorioUsuarios.
 */
@Component
public class DirectorioUsuariosDemo implements DirectorioUsuarios {

    private static final Map<String, Rol> USUARIOS = Map.of(
            "admin", Rol.ADMINISTRADOR,
            "nomina", Rol.RESPONSABLE_NOMINA,
            "catalogo", Rol.CATALOGADOR,
            "auditor", Rol.AUDITOR
    );

    private static final String CONTRASENA_DEMO = "martillo2026";

    @Override
    public Rol autenticar(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) return null;
        if (!CONTRASENA_DEMO.equals(contrasena)) return null;
        return USUARIOS.get(usuario.trim().toLowerCase());
    }
}
