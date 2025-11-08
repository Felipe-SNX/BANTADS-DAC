package com.bantads.msgerente.core.service;

import java.util.*;
import java.util.stream.Collectors;

import com.bantads.msgerente.core.dto.*;
import com.bantads.msgerente.core.enums.TipoGerente;
import com.bantads.msgerente.core.exception.GerenteNaoEncontradoException;
import org.springframework.stereotype.Service;

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

    public Optional<Gerente> checkCpf(String cpf){
        return repository.findByCpf(cpf);
    }

    public List<GerentesResponse> listarGerentes() {
        List<Gerente> gerentes = repository.findAllByTipo(TipoGerente.GERENTE);
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

    public GerenteNumeroContasDto selecionarGerente(List<GerenteNumeroContasDto> listaContasClientes) {
        List<Gerente> gerentesAtivos = repository.findAllByTipo(TipoGerente.GERENTE);

        if (gerentesAtivos.isEmpty()) {
            throw new RuntimeException("Não há gerentes ativos cadastrados para alocar o cliente.");
        }

        Set<String> cpfsGerentesAtivos = gerentesAtivos.stream()
                .map(Gerente::getCpf)
                .collect(Collectors.toSet());

        Map<String, Long> mapaContas = listaContasClientes.stream()
                .filter(dto -> cpfsGerentesAtivos.contains(dto.getCpfGerente()))
                .collect(Collectors.toMap(
                        GerenteNumeroContasDto::getCpfGerente,
                        GerenteNumeroContasDto::getQuantidade
                ));

        for (Gerente gerenteAtivo : gerentesAtivos) {
            if (!mapaContas.containsKey(gerenteAtivo.getCpf())) {
                return new GerenteNumeroContasDto(gerenteAtivo.getCpf(), 0L);
            }
        }

        return mapaContas.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(entry -> new GerenteNumeroContasDto(entry.getKey(), entry.getValue()))
                .orElseThrow(() -> new RuntimeException(
                        "Falha ao selecionar gerente. Não foi encontrado nenhum gerente válido com contas."
                ));
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
