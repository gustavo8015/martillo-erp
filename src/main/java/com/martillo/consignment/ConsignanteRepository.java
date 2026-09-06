package com.martillo.consignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsignanteRepository extends JpaRepository<Consignante, Long> {

    Optional<Consignante> findByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);
}
