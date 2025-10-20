package com.bantads.msconta.core.repository;

import com.bantads.msconta.core.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoWriteRepository  extends JpaRepository<Movimentacao, Long> {
}
