package com.bantads.mscliente.core.repository;

import com.bantads.mscliente.core.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    List<Cliente> findAllByAprovado(boolean aprovado);

    Optional<Cliente> findByCpfAndAprovado(String cpf, boolean b);

    List<Cliente> findAllByAprovadoOrderByNomeAsc(boolean b);

    @Query("SELECT c FROM Cliente c " +
            "WHERE c.aprovado = :aprovado " +
            "ORDER BY c.saldo DESC NULLS LAST")
    Page<Cliente> findMelhoresClientes(
            @Param("aprovado") Boolean aprovado,
            Pageable pageable
    );
}
