package com.martillo.consignment;

import com.martillo.consignment.dto.ConsignanteRequest;
import com.martillo.consignment.dto.ConsignanteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsignanteServiceImplTest {

    @Mock
    private ConsignanteRepository consignanteRepository;

    @InjectMocks
    private ConsignanteServiceImpl consignanteService;

    private ConsignanteRequest request;

    @BeforeEach
    void setUp() {
        request = new ConsignanteRequest();
        request.setNombreCompleto("Laura Gomez");
        request.setTipoDocumento("CC");
        request.setNumeroDocumento("123456789");
        request.setTelefono("3001234567");
        request.setEmail("laura@example.com");
        request.setDireccion("Calle 10 # 20-30, Bogota");
    }

    @Test
    void registrar_deberiaGuardarConsignante_cuandoDocumentoNoExiste() {
        when(consignanteRepository.existsByNumeroDocumento("123456789")).thenReturn(false);
        when(consignanteRepository.save(any(Consignante.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ConsignanteResponse response = consignanteService.registrar(request);

        assertNotNull(response);
        assertEquals("Laura Gomez", response.getNombreCompleto());
        assertEquals("123456789", response.getNumeroDocumento());
        verify(consignanteRepository, times(1)).save(any(Consignante.class));
    }

    @Test
    void registrar_deberiaLanzarExcepcion_cuandoDocumentoYaExiste() {
        when(consignanteRepository.existsByNumeroDocumento("123456789")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> consignanteService.registrar(request));
        verify(consignanteRepository, never()).save(any(Consignante.class));
    }
}
