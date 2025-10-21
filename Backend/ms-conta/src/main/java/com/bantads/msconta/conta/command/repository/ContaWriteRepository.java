package com.bantads.msconta.conta.command.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.msconta.conta.command.model.Conta;

import java.util.Optional;

public interface ContaWriteRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumConta(String numConta);
}
