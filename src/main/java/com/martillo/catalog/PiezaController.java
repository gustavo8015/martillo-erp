package com.martillo.catalog;

import com.martillo.catalog.dto.PiezaRequest;
import com.martillo.catalog.dto.PiezaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * HU-01: Como administrador, quiero registrar una pieza en el catalogo
 * para poder gestionarla en futuras subastas.
 */
@RestController
@RequestMapping("/api/piezas")
public class PiezaController {

    private final PiezaService piezaService;

    public PiezaController(PiezaService piezaService) {
        this.piezaService = piezaService;
    }

    @PostMapping
    public ResponseEntity<PiezaResponse> registrar(@Valid @RequestBody PiezaRequest request) {
        PiezaResponse creada = piezaService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<PiezaResponse>> listarTodas() {
        return ResponseEntity.ok(piezaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PiezaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(piezaService.buscarPorId(id));
    }
}
