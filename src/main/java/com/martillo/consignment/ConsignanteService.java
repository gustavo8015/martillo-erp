package com.martillo.consignment;

import com.martillo.consignment.dto.ConsignanteRequest;
import com.martillo.consignment.dto.ConsignanteResponse;

import java.util.List;

/**
 * Puerto/abstraccion del servicio de Consignantes.
 * El controller depende de esta interfaz, no de la implementacion concreta
 * (Principio de Inversion de Dependencias - DIP, pedido explicitamente en la guia).
 */
public interface ConsignanteService {

    ConsignanteResponse registrar(ConsignanteRequest request);

    List<ConsignanteResponse> listarTodos();

    ConsignanteResponse buscarPorId(Long id);
}
