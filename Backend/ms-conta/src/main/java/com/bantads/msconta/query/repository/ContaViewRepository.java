package com.bantads.msconta.query.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.msconta.query.model.ContaView;

public interface ContaViewRepository extends JpaRepository<ContaView, Long> {

    Optional<ContaView> findByConta(String conta);

    Optional<ContaView> findByCliente(String cpf);
}

