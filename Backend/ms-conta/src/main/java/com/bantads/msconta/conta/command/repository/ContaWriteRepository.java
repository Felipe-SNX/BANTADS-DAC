package com.bantads.msconta.conta.command.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.msconta.conta.command.model.Conta;
import com.bantads.msconta.conta.dto.GerentesNumeroContasDto;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContaWriteRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByConta(String conta);

    @Query("SELECT c.cpfGerente FROM Conta c GROUP BY c.cpfGerente ORDER BY COUNT(c.cpfGerente) DESC LIMIT 1")
    String findCpfGerenteComMaisContas();

    @Query("SELECT c.cpfGerente FROM Conta c GROUP BY c.cpfGerente ORDER BY COUNT(c.cpfGerente) ASC LIMIT 1")
    String findCpfGerenteComMenosContas();

    Optional<Conta> findFirstByCpfGerenteOrderByDataCriacaoAsc(String cpfGerente);

    Optional<Conta> findByCliente(String cpf);

    List<Conta> findAllByCpfGerente(String cpf);

    @Query("SELECT c.cpfGerente FROM Conta c WHERE c.cpfGerente != :cpf GROUP BY c.cpfGerente ORDER BY COUNT(c.cpfGerente) ASC LIMIT 1")
    String findCpfGerenteComMenosContasRemanejar(String cpf);
    
    List<GerentesNumeroContasDto> buscarNumeroDeContasPorGerente();
}
