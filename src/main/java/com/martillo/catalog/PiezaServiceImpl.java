package com.martillo.catalog;

import com.martillo.catalog.dto.PiezaRequest;
import com.martillo.catalog.dto.PiezaResponse;
import com.martillo.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PiezaServiceImpl implements PiezaService {

    private final PiezaRepository piezaRepository;

    public PiezaServiceImpl(PiezaRepository piezaRepository) {
        this.piezaRepository = piezaRepository;
    }

    @Override
    public PiezaResponse registrar(PiezaRequest request) {
        Pieza pieza = new Pieza(
            request.getNombre(),
            request.getDescripcion(),
            request.getCategoria(),
            request.getPrecioReserva()
        );
        Pieza guardada = piezaRepository.save(pieza);
        return new PiezaResponse(guardada);
    }

    @Override
    public List<PiezaResponse> listarTodas() {
        return piezaRepository.findAll()
            .stream()
            .map(PiezaResponse::new)
            .toList();
    }

    @Override
    public PiezaResponse buscarPorId(Long id) {
        Pieza pieza = piezaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No existe una pieza con id " + id));
        return new PiezaResponse(pieza);
    }
}
