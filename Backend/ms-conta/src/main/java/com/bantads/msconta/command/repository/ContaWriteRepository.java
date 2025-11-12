package com.bantads.msconta.command.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.msconta.command.model.Conta;
import com.bantads.msconta.common.conta.dto.GerentesNumeroContasDto;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContaWriteRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByConta(String conta);

    @Query("SELECT c.gerente FROM Conta c " +
            "WHERE c.gerente IS NOT NULL " +
            "GROUP BY c.gerente " +
            "ORDER BY COUNT(c.gerente) DESC, MAX(c.dataCriacao) DESC")
    Page<String> findGerentesOrdenadosPorContasEData(Pageable pageable);

    @Query("SELECT c.gerente FROM Conta c GROUP BY c.gerente ORDER BY COUNT(c.gerente) ASC LIMIT 1")
    String findGerenteComMenosContas();

    Optional<Conta> findFirstByGerenteOrderByDataCriacaoAsc(String cpfGerente);

    Optional<Conta> findByCliente(String cpf);

    List<Conta> findAllByGerente(String cpf);

    @Query("SELECT c.gerente FROM Conta c WHERE c.gerente != :cpf GROUP BY c.gerente ORDER BY COUNT(c.gerente) ASC LIMIT 1")
    String findGerenteComMenosContasRemanejar(String cpf);

    @Query("SELECT new com.bantads.msconta.common.conta.dto.GerentesNumeroContasDto(c.gerente, COUNT(c.gerente)) " +
            "FROM Conta c " +
            "WHERE c.gerente IS NOT NULL " +
            "GROUP BY c.gerente")
    List<GerentesNumeroContasDto> countContasByGerente();

    void deleteByCliente(String cpf);

    List<Conta> findAllByClienteIn(List<String> cpfsClientesAfetados);
}
