package com.martillo.seguridad;

import org.springframework.stereotype.Service;

import java.util.List;

/** Registra los eventos de seguridad exigidos por HU-09 y HU-11. */
@Service
public class AuditoriaService {

    private final RegistroAuditoriaRepository repositorio;

    public AuditoriaService(RegistroAuditoriaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void registrar(String usuario, String evento, String detalle, boolean permitido) {
        repositorio.save(new RegistroAuditoria(usuario, evento, detalle, permitido));
    }

    public List<RegistroAuditoria> ultimos() {
        return repositorio.findTop50ByOrderByMomentoDesc();
    }
}
