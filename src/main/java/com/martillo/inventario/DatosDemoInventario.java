package com.martillo.inventario;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datos de arranque para la demostracion del Sprint Review: las cajas de
 * embalaje quedan con 40 unidades y umbral 25, tal como pide el criterio de
 * aceptacion de HU-08.
 */
@Configuration
public class DatosDemoInventario {

    @Bean
    ApplicationRunner cargarInsumosDeDemostracion(ItemInventarioRepository items) {
        return argumentos -> {
            if (items.count() > 0) return;
            items.save(new ItemInventario("EMB-001", "Cajas de embalaje", 40, 25));
            items.save(new ItemInventario("EMB-002", "Protecciones de espuma", 120, 40));
            items.save(new ItemInventario("CAT-001", "Catalogos impresos", 300, 100));
            items.save(new ItemInventario("ETQ-001", "Etiquetas de lote", 60, 50));
        };
    }
}
