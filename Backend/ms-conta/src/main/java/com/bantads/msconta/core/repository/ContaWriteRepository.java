package com.bantads.msconta.core.repository;

import com.bantads.msconta.core.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ContaWriteRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumConta(String numConta);
}
