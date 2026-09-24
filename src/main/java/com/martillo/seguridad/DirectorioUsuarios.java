package com.martillo.seguridad;

/**
 * Puerto de autenticacion. El Sprint 3 usa un directorio de demostracion; la
 * interfaz permite sustituirlo por el directorio institucional sin tocar el
 * servicio de inicio de sesion.
 */
public interface DirectorioUsuarios {

    /** Rol del usuario si las credenciales son correctas, o null si no lo son. */
    Rol autenticar(String usuario, String contrasena);
}
