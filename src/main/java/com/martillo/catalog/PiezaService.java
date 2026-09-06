package com.martillo.catalog;

import com.martillo.catalog.dto.PiezaRequest;
import com.martillo.catalog.dto.PiezaResponse;

import java.util.List;

public interface PiezaService {

    PiezaResponse registrar(PiezaRequest request);

    List<PiezaResponse> listarTodas();

    PiezaResponse buscarPorId(Long id);
}
