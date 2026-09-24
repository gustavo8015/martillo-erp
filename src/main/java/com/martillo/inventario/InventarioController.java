package com.martillo.inventario;

import com.martillo.inventario.dto.ItemResponse;
import com.martillo.inventario.dto.MovimientoRequest;
import com.martillo.seguridad.AutorizacionService;
import com.martillo.seguridad.Modulo;
import com.martillo.seguridad.Permiso;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Inventario de insumos y alerta de stock critico (HU-08). */
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService servicio;
    private final AutorizacionService autorizacion;

    public InventarioController(InventarioService servicio, AutorizacionService autorizacion) {
        this.servicio = servicio;
        this.autorizacion = autorizacion;
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemResponse>> listar(
            @RequestHeader(name = "X-Rol", required = false) String rol) {
        autorizacion.exigir(rol, Modulo.INVENTARIO, Permiso.LECTURA);
        return ResponseEntity.ok(servicio.listar().stream().map(ItemResponse::new).toList());
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaStock>> alertas(
            @RequestHeader(name = "X-Rol", required = false) String rol) {
        autorizacion.exigir(rol, Modulo.INVENTARIO, Permiso.LECTURA);
        return ResponseEntity.ok(servicio.alertasPendientes());
    }

    @PostMapping("/salidas")
    public ResponseEntity<ItemResponse> registrarSalida(
            @RequestHeader(name = "X-Rol", required = false) String rol,
            @Valid @RequestBody MovimientoRequest peticion) {
        autorizacion.exigir(rol, Modulo.INVENTARIO, Permiso.TOTAL);
        ItemInventario item = servicio.registrarSalida(peticion.getCodigo(), peticion.getCantidad());
        return ResponseEntity.ok(new ItemResponse(item));
    }

    @PostMapping("/entradas")
    public ResponseEntity<ItemResponse> registrarEntrada(
            @RequestHeader(name = "X-Rol", required = false) String rol,
            @Valid @RequestBody MovimientoRequest peticion) {
        autorizacion.exigir(rol, Modulo.INVENTARIO, Permiso.TOTAL);
        ItemInventario item = servicio.registrarEntrada(peticion.getCodigo(), peticion.getCantidad());
        return ResponseEntity.ok(new ItemResponse(item));
    }
}
