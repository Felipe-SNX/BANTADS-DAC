package com.bantads.msgerente.core.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.msgerente.core.dto.DadoGerente;
import com.bantads.msgerente.core.dto.DadoGerenteInsercao;
import com.bantads.msgerente.core.dto.GerentesResponse;
import com.bantads.msgerente.core.service.GerenteService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    
}
