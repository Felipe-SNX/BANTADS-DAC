package com.bantads.msorquestrador.core.controller;

import com.bantads.msorquestrador.core.dto.*;
import com.bantads.msorquestrador.core.dto.mapper.PerfilInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bantads.msorquestrador.core.service.SagaService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/saga")
public class SagaController {

    private final SagaService sagaService;

    @PostMapping("/autocadastro")
    public ResponseEntity<AutoCadastroInfo> iniciarSagaAutocadastro(@RequestBody AutoCadastroInfo autoCadastroInfo) {
        log.info("Iniciando saga autocadastro");
        sagaService.iniciarSagaAutocadastro(autoCadastroInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(autoCadastroInfo);
    }

    @PostMapping("/{cpf}/aprovar")
    public ResponseEntity<Void> iniciarSagaAprovarCliente(@RequestBody ClienteParaAprovarRequest clienteParaAprovarRequest,
                                               @PathVariable String cpf) {
        sagaService.iniciarSagaAprovarCliente(clienteParaAprovarRequest, cpf);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/alterarPerfil/{cpf}")
    public ResponseEntity<PerfilInfoResponse> iniciarSagaAlterarPerfil(@RequestBody PerfilInfo perfilInfo, @PathVariable String cpf) {
        log.info("Iniciando saga alterar perfil");
        sagaService.iniciarSagaAlterarPerfil(perfilInfo, cpf);
        PerfilInfoResponse perfilInfoResponse = PerfilInfoMapper.toPerfilInfoResponse(perfilInfo);
        perfilInfoResponse.setCpf(cpf);
        return ResponseEntity.status(HttpStatus.OK).body(perfilInfoResponse);
    }

    @PostMapping("/inserirGerente")
    public ResponseEntity<DadoGerenteInsercao> inserirGerente(@RequestBody DadoGerenteInsercao dadoGerenteInsercao) {
        log.info("Iniciando saga inserir gerente");
        log.info("Saga inserir gerente: {}", dadoGerenteInsercao);
        sagaService.iniciarSagaInserirGerente(dadoGerenteInsercao);
        return ResponseEntity.status(HttpStatus.CREATED).body(dadoGerenteInsercao);
    }

    @DeleteMapping("/removerGerente/{cpf}")
    public void deletarGerenteCpf(@PathVariable String cpf) {
        log.info("Iniciando saga remover gerente");
        sagaService.iniciarSagaRemoverGerente(cpf);
    }
    
}
