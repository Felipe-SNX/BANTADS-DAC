package com.bantads.msconta.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.msconta.core.model.Conta;
import com.bantads.msconta.core.model.ContaView;

public interface ContaViewRepository extends JpaRepository<ContaView, Long> {

    Optional<Conta> findByNumConta(String numConta);
}

