package com.martillo.seguridad.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    @NotBlank(message = "La contrasena es obligatoria")
    private String contrasena;

    @NotBlank(message = "Falta el token de verificacion")
    private String tokenRecaptcha;

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getTokenRecaptcha() { return tokenRecaptcha; }
    public void setTokenRecaptcha(String tokenRecaptcha) { this.tokenRecaptcha = tokenRecaptcha; }
}
