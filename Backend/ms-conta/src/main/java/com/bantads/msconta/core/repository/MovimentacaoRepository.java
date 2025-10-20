package com.bantads.msconta.core.repository;

import com.bantads.msconta.core.dto.ItemExtratoResponse;
import com.bantads.msconta.core.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository  extends JpaRepository<Movimentacao, Long> {
    List<ItemExtratoResponse> findAllByCpfClienteOrigem(String cpfConta);
}
