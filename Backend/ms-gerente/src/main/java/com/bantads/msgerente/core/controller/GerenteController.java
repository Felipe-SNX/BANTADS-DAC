package com.bantads.msgerente.core.controller;

import java.util.List;

import com.bantads.msgerente.core.dto.DadoGerenteAtualizacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bantads.msgerente.core.dto.DadoGerente;
import com.bantads.msgerente.core.dto.DadoGerenteInsercao;
import com.bantads.msgerente.core.dto.GerentesResponse;
import com.bantads.msgerente.core.service.GerenteService;

import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
@RequestMapping("/gerentes")
public class GerenteController {

    private final GerenteService gerenteService;

    @GetMapping
    public ResponseEntity<List<GerentesResponse>> listar() {
        List<GerentesResponse> gerenteResponse = gerenteService.listarGerentes();
        return ResponseEntity.ok(gerenteResponse);
    }

    @PostMapping
    public ResponseEntity<DadoGerente> inserirGerente(@RequestBody DadoGerenteInsercao dadoGerenteInsercao) {
        DadoGerente dadoGerente = gerenteService.inserirGerente(dadoGerenteInsercao);
        return ResponseEntity.status(201).body(dadoGerente);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<GerentesResponse> listarPorCpf(@PathVariable String cpf) {
        GerentesResponse gerenteResponse = gerenteService.listarGerentePorCpf(cpf);
        return ResponseEntity.ok(gerenteResponse);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<GerentesResponse> deletarGerenteCpf(@PathVariable String cpf) {
        GerentesResponse gerenteResponse = gerenteService.deletarGerentePorCpf(cpf);
        return ResponseEntity.ok(gerenteResponse);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<GerentesResponse> deletarGerenteCpf(@RequestBody DadoGerenteAtualizacao dadoGerente,
                                                              @PathVariable String cpf) {
        GerentesResponse gerenteResponse = gerenteService.atualizarGerentePorCpf(dadoGerente, cpf);
        return ResponseEntity.ok(gerenteResponse);
    }
}
