package com.bantads.msgerente.core.service;

import java.util.ArrayList;
import java.util.List;

import com.bantads.msgerente.core.dto.DadoGerenteAtualizacao;
import com.bantads.msgerente.core.exception.GerenteNaoEncontradoException;
import org.springframework.stereotype.Service;

import com.bantads.msgerente.core.dto.DadoGerente;
import com.bantads.msgerente.core.dto.DadoGerenteInsercao;
import com.bantads.msgerente.core.dto.GerentesResponse;
import com.bantads.msgerente.core.dto.mapper.GerenteMapper;
import com.bantads.msgerente.core.model.Gerente;
import com.bantads.msgerente.core.repository.GerenteRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class  GerenteService {

    private final GerenteRepository repository;

    public List<GerentesResponse> listarGerentes() {
        List<Gerente> gerentes = repository.findAll();
        List<GerentesResponse> gerenteResponse = new ArrayList<>();

        for (Gerente gerente : gerentes) {
            gerenteResponse.add(
                GerenteMapper.toGerentesResponse(gerente)
            );
        }

        return gerenteResponse;
    }

    public DadoGerente inserirGerente(DadoGerenteInsercao dadoGerenteInsercao){
        Gerente gerente = GerenteMapper.dadoGerenteInsercaoToGerente(dadoGerenteInsercao);
        repository.save(gerente);
        return GerenteMapper.toDadoGerente(gerente);
    }

    public GerentesResponse listarGerentePorCpf(String cpf) {
        Gerente gerente = repository.findByCpf(cpf)
                .orElseThrow(() -> new GerenteNaoEncontradoException("Gerente", cpf));

        return GerenteMapper.toGerentesResponse(gerente);
    }

    public GerentesResponse deletarGerentePorCpf(String cpf) {
        Gerente gerente = repository.findByCpf(cpf)
                .orElseThrow(() -> new GerenteNaoEncontradoException("Gerente", cpf));
        repository.delete(gerente);
        return GerenteMapper.toGerentesResponse(gerente);
    }

    public GerentesResponse atualizarGerentePorCpf(DadoGerenteAtualizacao dadoGerenteAtualizacao, String cpf) {
        Gerente gerente = repository.findByCpf(cpf)
                .orElseThrow(() -> new GerenteNaoEncontradoException("Gerente", cpf));

        gerente.setNome(dadoGerenteAtualizacao.getNome());
        gerente.setEmail(dadoGerenteAtualizacao.getEmail());
        repository.save(gerente);
        //gerente.setSenha(dadoGerenteAtualizacao.getSenha());
        return GerenteMapper.toGerentesResponse(gerente);
    }
}
