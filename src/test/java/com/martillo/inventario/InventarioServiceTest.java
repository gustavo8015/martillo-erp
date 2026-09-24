package com.martillo.inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Verificacion de la alerta de stock critico (HU-08). */
@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private ItemInventarioRepository items;

    @Mock
    private AlertaStockRepository alertas;

    @Mock
    private NotificadorAlertas notificador;

    private InventarioService servicio;
    private ItemInventario cajas;
    private final List<AlertaStock> guardadas = new ArrayList<>();

    @BeforeEach
    void prepararEscenario() {
        servicio = new InventarioService(items, alertas, notificador);
        cajas = new ItemInventario("EMB-001", "Cajas de embalaje", 40, 25);

        when(items.findByCodigo("EMB-001")).thenReturn(Optional.of(cajas));
        when(items.save(any(ItemInventario.class))).thenAnswer(i -> i.getArgument(0));
        guardadas.clear();
    }

    private void conAlertasPersistentes() {
        when(alertas.save(any(AlertaStock.class))).thenAnswer(i -> {
            AlertaStock a = i.getArgument(0);
            guardadas.add(a);
            return a;
        });
    }

    @Test
    @DisplayName("Una salida que cruza el umbral deja el item en critico y genera una alerta")
    void salidaBajoUmbralGeneraAlerta() {
        conAlertasPersistentes();

        ItemInventario resultado = servicio.registrarSalida("EMB-001", 20);

        assertEquals(20, resultado.getStock());
        assertEquals(EstadoStock.CRITICO, resultado.getEstado());
        assertEquals(1, guardadas.size());
        assertEquals(20, guardadas.get(0).getStockAlGenerar());
        verify(notificador).notificar(any(AlertaStock.class));
    }

    @Test
    @DisplayName("Una salida que no cruza el umbral no genera alerta")
    void salidaSobreUmbralNoAlerta() {
        ItemInventario resultado = servicio.registrarSalida("EMB-001", 10);

        assertEquals(30, resultado.getStock());
        assertEquals(EstadoStock.NORMAL, resultado.getEstado());
        verify(alertas, never()).save(any(AlertaStock.class));
        verify(notificador, never()).notificar(any(AlertaStock.class));
    }

    @Test
    @DisplayName("Un item que ya esta en critico no vuelve a alertar")
    void noRepiteAlertaMientrasSigaCritico() {
        conAlertasPersistentes();

        servicio.registrarSalida("EMB-001", 20);
        servicio.registrarSalida("EMB-001", 5);

        assertEquals(15, cajas.getStock());
        assertEquals(1, guardadas.size());
        verify(notificador).notificar(any(AlertaStock.class));
    }

    @Test
    @DisplayName("La reposicion devuelve el item a normal y cierra la alerta pendiente")
    void reposicionDevuelveANormal() {
        conAlertasPersistentes();
        servicio.registrarSalida("EMB-001", 20);
        when(alertas.findByAtendidaFalseOrderByMomentoDesc()).thenReturn(List.copyOf(guardadas));

        ItemInventario resultado = servicio.registrarEntrada("EMB-001", 30);

        assertEquals(50, resultado.getStock());
        assertEquals(EstadoStock.NORMAL, resultado.getEstado());
        assertEquals(true, guardadas.get(0).isAtendida());
    }

    @Test
    @DisplayName("No se puede sacar mas stock del disponible")
    void salidaMayorAlStock() {
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarSalida("EMB-001", 100));
    }
}
