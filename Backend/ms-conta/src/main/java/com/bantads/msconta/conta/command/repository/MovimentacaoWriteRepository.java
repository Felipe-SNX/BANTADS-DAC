package com.bantads.msconta.conta.command.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.msconta.conta.command.model.Movimentacao;

public interface MovimentacaoWriteRepository  extends JpaRepository<Movimentacao, Long> {
}
