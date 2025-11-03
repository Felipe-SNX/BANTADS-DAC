package com.bantads.msconta.conta.command.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.msconta.conta.command.model.Conta;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ContaWriteRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumConta(String numConta);

    @Query("SELECT c.cpfGerente FROM Conta c GROUP BY c.cpfGerente ORDER BY COUNT(c.cpfGerente) DESC LIMIT 1")
    String findCpfGerenteComMaisContas();

    Optional<Conta> findFirstByCpfGerenteOrderByDataCriacaoAsc(String cpfGerente);
}
