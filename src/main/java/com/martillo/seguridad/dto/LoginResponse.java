package com.martillo.seguridad.dto;

import java.util.Map;

/** Resultado del inicio de sesion: rol asignado y menu que le corresponde. */
public class LoginResponse {

    private final String usuario;
    private final String rol;
    private final Map<String, String> menu;

    public LoginResponse(String usuario, String rol, Map<String, String> menu) {
        this.usuario = usuario;
        this.rol = rol;
        this.menu = menu;
    }

    public String getUsuario() { return usuario; }
    public String getRol() { return rol; }
    public Map<String, String> getMenu() { return menu; }
}
