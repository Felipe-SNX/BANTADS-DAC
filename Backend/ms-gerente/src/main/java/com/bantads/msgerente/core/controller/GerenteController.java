package com.bantads.msgerente.core.controller;

import java.util.List;
import java.util.Optional;

import com.bantads.msgerente.core.dto.DadoGerenteAtualizacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bantads.msgerente.core.dto.DadoGerente;
import com.bantads.msgerente.core.dto.DadoGerenteInsercao;
import com.bantads.msgerente.core.dto.GerentesResponse;
import com.bantads.msgerente.core.model.Gerente;
import com.bantads.msgerente.core.service.DataService;
import com.bantads.msgerente.core.service.GerenteService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/gerentes")
public class GerenteController {

    private final GerenteService gerenteService;
    private final DataService dataService;

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {
        dataService.popularBanco();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/checkCpf/{cpf}")
    public ResponseEntity<String> checkCpf(@PathVariable String cpf) {
        Optional<Gerente> gerente = gerenteService.checkCpf(cpf);

        if(gerente.isPresent()){
            log.info("Retornando 409");
            return ResponseEntity.status(409).body("Gerente já cadastrado ou aguardando aprovação, CPF duplicado");
        }

        return ResponseEntity.status(200).body("Cpf não cadastrado");
    }

    @GetMapping
    public ResponseEntity<List<DadoGerente>> listar() {
        List<DadoGerente> dadoGerente = gerenteService.listarGerentes();
        return ResponseEntity.ok(dadoGerente);
    }

    @PostMapping
    public ResponseEntity<DadoGerente> inserirGerente(@RequestBody DadoGerenteInsercao dadoGerenteInsercao) {
        DadoGerente dadoGerente = gerenteService.inserirGerente(dadoGerenteInsercao);
        return ResponseEntity.status(201).body(dadoGerente);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<DadoGerente> listarPorCpf(@PathVariable String cpf) {
        DadoGerente dadoGerente = gerenteService.listarGerentePorCpf(cpf);
        return ResponseEntity.ok(dadoGerente);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<GerentesResponse> deletarGerenteCpf(@PathVariable String cpf) {
        GerentesResponse gerenteResponse = gerenteService.deletarGerentePorCpf(cpf);
        return ResponseEntity.ok(gerenteResponse);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<GerentesResponse> atualizarGerenteCpf(@RequestBody DadoGerenteAtualizacao dadoGerente,
                                                              @PathVariable String cpf) {
        GerentesResponse gerenteResponse = gerenteService.atualizarGerentePorCpf(dadoGerente, cpf);
        return ResponseEntity.ok(gerenteResponse);
    }
}
