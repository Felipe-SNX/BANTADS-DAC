package com.bantads.mscliente.core.repository;

import com.bantads.mscliente.core.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    List<Cliente> findAllByAprovado(boolean aprovado);

    @Query(
            "SELECT c.id, c.email, c.cpf, c.idEndereco, c.telefone, c.salario," +
            "c.aprovado, c.cpfGerente, c.motivoRejeição, c.nome " +
            "FROM Cliente c"
    )
    List<Cliente> findThreeBestClientes();
}
