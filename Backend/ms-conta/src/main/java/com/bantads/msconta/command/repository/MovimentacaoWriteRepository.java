package com.bantads.msconta.command.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.msconta.command.model.Movimentacao;

public interface MovimentacaoWriteRepository  extends JpaRepository<Movimentacao, Long> {
}
