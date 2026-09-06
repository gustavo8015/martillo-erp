package com.martillo.consignment;

import com.martillo.common.ResourceNotFoundException;
import com.martillo.consignment.dto.ConsignanteRequest;
import com.martillo.consignment.dto.ConsignanteResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementacion del servicio de Consignantes.
 * Responsabilidad unica (SRP): reglas de negocio de consignantes, nada de HTTP ni de SQL directo.
 */
@Service
public class ConsignanteServiceImpl implements ConsignanteService {

    private final ConsignanteRepository consignanteRepository;

    // Inyeccion por constructor: Spring resuelve la dependencia via la abstraccion del repositorio.
    public ConsignanteServiceImpl(ConsignanteRepository consignanteRepository) {
        this.consignanteRepository = consignanteRepository;
    }

    @Override
    public ConsignanteResponse registrar(ConsignanteRequest request) {
        if (consignanteRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new IllegalArgumentException(
                "Ya existe un consignante registrado con el documento " + request.getNumeroDocumento());
        }

        Consignante consignante = new Consignante(
            request.getNombreCompleto(),
            request.getTipoDocumento(),
            request.getNumeroDocumento(),
            request.getTelefono(),
            request.getEmail(),
            request.getDireccion()
        );

        Consignante guardado = consignanteRepository.save(consignante);
        return new ConsignanteResponse(guardado);
    }

    @Override
    public List<ConsignanteResponse> listarTodos() {
        return consignanteRepository.findAll()
            .stream()
            .map(ConsignanteResponse::new)
            .toList();
    }

    @Override
    public ConsignanteResponse buscarPorId(Long id) {
        Consignante consignante = consignanteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No existe un consignante con id " + id));
        return new ConsignanteResponse(consignante);
    }
}
