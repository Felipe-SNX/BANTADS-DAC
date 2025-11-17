package com.bantads.mscliente.core.repository;

import com.bantads.mscliente.core.dto.GerenteNumeroContasDto;
import com.bantads.mscliente.core.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    List<Cliente> findAllByAprovado(boolean aprovado);

    Optional<Cliente> findByCpfAndAprovado(String cpf, boolean b);

    List<Cliente> findAllByAprovadoOrderByNomeAsc(boolean b);

    void deleteByCpf(String cpf);

    @Query("SELECT new com.bantads.mscliente.core.dto.GerenteNumeroContasDto(c.gerente, COUNT(c.gerente)) " +
            "FROM Cliente c " +
            "WHERE c.gerente IS NOT NULL " +
            "GROUP BY c.gerente")
    List<GerenteNumeroContasDto> countClientesByGerente();
}
