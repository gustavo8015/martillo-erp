package com.martillo.consignment;

import com.martillo.consignment.dto.ConsignanteRequest;
import com.martillo.consignment.dto.ConsignanteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expone el modulo de Consignacion via HTTP.
 * HU-02: registrar un consignante.
 * HU-03: listar los consignantes registrados.
 */
@RestController
@RequestMapping("/api/consignantes")
public class ConsignanteController {

    private final ConsignanteService consignanteService;

    public ConsignanteController(ConsignanteService consignanteService) {
        this.consignanteService = consignanteService;
    }

    // HU-02: Como administrador, quiero registrar un consignante con su informacion
    // de contacto para poder asociarlo a piezas.
    @PostMapping
    public ResponseEntity<ConsignanteResponse> registrar(@Valid @RequestBody ConsignanteRequest request) {
        ConsignanteResponse creado = consignanteService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // HU-03: Como administrador, quiero ver una lista de todos los consignantes
    // registrados para seleccionarlos facilmente.
    @GetMapping
    public ResponseEntity<List<ConsignanteResponse>> listarTodos() {
        return ResponseEntity.ok(consignanteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsignanteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(consignanteService.buscarPorId(id));
    }
}
