package com.martillo.seguridad;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Verificacion de acceso en el servidor (HU-09).
 *
 * Un menu oculto no es un control de seguridad: cada peticion a un modulo
 * restringido pasa por aqui y todo rechazo queda en la pista de auditoria.
 */
@Service
public class AutorizacionService {

    private final AuditoriaService auditoria;

    public AutorizacionService(AuditoriaService auditoria) {
        this.auditoria = auditoria;
    }

    /** Lanza AccesoDenegadoException si el rol no alcanza el permiso requerido. */
    public void exigir(String rolRecibido, Modulo modulo, Permiso requerido) {
        Rol rol = interpretar(rolRecibido);
        Permiso permiso = MatrizAcceso.permisoDe(rol, modulo);

        if (!permiso.cubre(requerido)) {
            auditoria.registrar(rol == null ? "desconocido" : rol.name(),
                    "ACCESO_DENEGADO", modulo.name(), false);
            throw new AccesoDenegadoException("El rol no tiene acceso al modulo " + modulo);
        }
        auditoria.registrar(rol.name(), "ACCESO_PERMITIDO", modulo.name(), true);
    }

    public Map<Modulo, Permiso> menuDe(String rolRecibido) {
        return MatrizAcceso.menuDe(interpretar(rolRecibido));
    }

    private Rol interpretar(String rolRecibido) {
        if (rolRecibido == null || rolRecibido.isBlank()) return null;
        try {
            return Rol.valueOf(rolRecibido.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
