package com.bantads.msconta.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bantads.msconta.core.dto.ItemExtratoResponse;
import com.bantads.msconta.core.model.MovimentacaoView;

public interface MovimentacaoViewRepository extends JpaRepository<MovimentacaoView, Long> {

    List<MovimentacaoView> findAllByCpfClienteOrigem(String cpfConta);
}
