package com.bantads.msorquestrador.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bantads.msorquestrador.core.dto.AutoCadastroInfo;
import com.bantads.msorquestrador.core.dto.DadoGerenteInsercao;
import com.bantads.msorquestrador.core.dto.PerfilInfo;
import com.bantads.msorquestrador.core.service.SagaService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@AllArgsConstructor
@RequestMapping("/saga")
public class SagaController {

    private final SagaService sagaService;

    @PostMapping("/autocadastro")
    public void iniciarSagaAutocadastro(@RequestBody AutoCadastroInfo autoCadastroInfo) {
        sagaService.iniciarSagaAutocadastro(autoCadastroInfo);
    }

    @PutMapping("/alterar-perfil/{cpf}")
    public void iniciarSagaAlterarPerfil(@RequestBody PerfilInfo perfilInfo, @PathVariable String cpf) {
        sagaService.iniciarSagaAlterarPerfil(perfilInfo, cpf);
    }

    @PostMapping("/inserirGerente")
    public void inserirGerente(@RequestBody DadoGerenteInsercao dadoGerenteInsercao) {
       sagaService.iniciarSagaInserirGerente(dadoGerenteInsercao);
    }

    @DeleteMapping("/removerGerente/{cpf}")
    public void deletarGerenteCpf(@PathVariable String cpf) {
        sagaService.iniciarSagaRemoverGerente(cpf);
    }
    
}
