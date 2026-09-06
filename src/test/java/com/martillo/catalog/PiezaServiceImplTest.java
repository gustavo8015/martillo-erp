package com.martillo.catalog;

import com.martillo.catalog.dto.PiezaRequest;
import com.martillo.catalog.dto.PiezaResponse;
import com.martillo.common.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PiezaServiceImplTest {

    @Mock
    private PiezaRepository piezaRepository;

    @InjectMocks
    private PiezaServiceImpl piezaService;

    @Test
    void registrar_deberiaGuardarPiezaConEstadoDisponiblePorDefecto() {
        PiezaRequest request = new PiezaRequest();
        request.setNombre("Jarron de porcelana");
        request.setDescripcion("Jarron chino del siglo XIX");
        request.setCategoria("Antiguedades");
        request.setPrecioReserva(new BigDecimal("1500000"));

        when(piezaRepository.save(any(Pieza.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PiezaResponse response = piezaService.registrar(request);

        assertNotNull(response);
        assertEquals("Jarron de porcelana", response.getNombre());
        assertEquals("DISPONIBLE", response.getEstado());
        verify(piezaRepository, times(1)).save(any(Pieza.class));
    }

    @Test
    void buscarPorId_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(piezaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> piezaService.buscarPorId(99L));
    }
}
