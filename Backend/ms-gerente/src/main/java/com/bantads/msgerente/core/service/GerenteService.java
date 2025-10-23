package com.bantads.msgerente.core.service;

import java.util.ArrayList;
import java.util.List;

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
public class GerenteService {

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
}
