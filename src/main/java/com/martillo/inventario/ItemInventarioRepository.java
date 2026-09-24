package com.martillo.inventario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemInventarioRepository extends JpaRepository<ItemInventario, Long> {

    Optional<ItemInventario> findByCodigo(String codigo);

    List<ItemInventario> findByEstado(EstadoStock estado);
}
