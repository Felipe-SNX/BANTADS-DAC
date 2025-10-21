package com.bantads.msconta.conta.query.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bantads.msconta.conta.query.model.MovimentacaoView;

public interface MovimentacaoViewRepository extends JpaRepository<MovimentacaoView, Long> {

    @Query("SELECT m FROM MovimentacaoView m WHERE m.cpfClienteOrigem = :cpfConta OR m.cpfClienteDestino = :cpfConta ORDER BY m.data DESC")
    List<MovimentacaoView> findAllByCpfCliente(String cpfConta);
}
