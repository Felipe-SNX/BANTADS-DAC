package com.bantads.msconta.query.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bantads.msconta.query.model.MovimentacaoView;

public interface MovimentacaoViewRepository extends JpaRepository<MovimentacaoView, Long> {

    @Query("SELECT m FROM MovimentacaoView m WHERE m.cpfClienteOrigem = :cpfConta OR m.cpfClienteDestino = :cpfConta ORDER BY m.data ASC")
    List<MovimentacaoView> findAllByCpfCliente(String cpfConta);
}
