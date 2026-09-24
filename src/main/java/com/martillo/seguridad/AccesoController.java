package com.martillo.seguridad;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Demostracion del control de acceso por roles (HU-09). */
@RestController
@RequestMapping("/api/seguridad")
public class AccesoController {

    private final AutorizacionService autorizacion;
    private final AuditoriaService auditoria;

    public AccesoController(AutorizacionService autorizacion, AuditoriaService auditoria) {
        this.autorizacion = autorizacion;
        this.auditoria = auditoria;
    }

    /** Modulos que el rol puede ver, con su nivel de acceso. */
    @GetMapping("/menu")
    public ResponseEntity<Map<String, String>> menu(@RequestHeader(name = "X-Rol", required = false) String rol) {
        Map<String, String> menu = autorizacion.menuDe(rol).entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), e -> e.getValue().name()));
        return ResponseEntity.ok(menu);
    }

    /** Ultimos eventos de la pista de auditoria; solo para el rol auditor. */
    @GetMapping("/auditoria")
    public ResponseEntity<List<RegistroAuditoria>> auditoria(
            @RequestHeader(name = "X-Rol", required = false) String rol) {
        autorizacion.exigir(rol, Modulo.AUDITORIA, Permiso.TOTAL);
        return ResponseEntity.ok(auditoria.ultimos());
    }
}
