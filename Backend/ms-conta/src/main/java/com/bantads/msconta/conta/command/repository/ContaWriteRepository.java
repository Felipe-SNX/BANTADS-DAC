package com.bantads.msconta.conta.command.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.msconta.conta.command.model.Conta;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContaWriteRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumConta(String numConta);

    @Query("SELECT c.cpfGerente FROM Conta c GROUP BY c.cpfGerente ORDER BY COUNT(c.cpfGerente) DESC LIMIT 1")
    String findCpfGerenteComMaisContas();

    @Query("SELECT c.cpfGerente FROM Conta c GROUP BY c.cpfGerente ORDER BY COUNT(c.cpfGerente) ASC LIMIT 1")
    String findCpfGerenteComMenosContas();

    Optional<Conta> findFirstByCpfGerenteOrderByDataCriacaoAsc(String cpfGerente);

    Optional<Conta> findByCpfCliente(String cpf);

    List<Conta> findAllByCpfGerente(String cpf);

    @Query("SELECT c.cpfGerente FROM Conta c WHERE c.cpfGerente != :cpf GROUP BY c.cpfGerente ORDER BY COUNT(c.cpfGerente) ASC LIMIT 1")
    String findCpfGerenteComMenosContasRemanejar(String cpf);
}
