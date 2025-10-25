package com.bantads.mscliente.core.repository;

import com.bantads.mscliente.core.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}
