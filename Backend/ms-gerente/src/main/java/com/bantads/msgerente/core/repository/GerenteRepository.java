package com.bantads.msgerente.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.msgerente.core.model.Gerente;

import java.util.Optional;

public interface GerenteRepository extends JpaRepository<Gerente, Long>{

    Optional<Gerente> findByCpf(String cpf);
}
