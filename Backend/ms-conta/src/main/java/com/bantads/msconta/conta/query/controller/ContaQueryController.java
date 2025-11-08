package com.bantads.msconta.conta.query.controller;

import com.bantads.msconta.conta.dto.DadoConta;
import com.bantads.msconta.conta.dto.ExtratoResponse;
import com.bantads.msconta.conta.dto.SaldoResponse;
import com.bantads.msconta.conta.query.service.ContaQueryService;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/contas")
public class ContaQueryController {

    private final ContaQueryService contaQueryService;

    @GetMapping("/{cpf}/dadosConta")
    public ResponseEntity<DadoConta> obterDadosConta(@PathVariable String cpf) {
        DadoConta dadoConta = contaQueryService.getContaByClienteCpf(cpf);
        return ResponseEntity.ok(dadoConta);
    }

    @GetMapping("/dadosConta")
    public ResponseEntity<List<DadoConta>> obterTodosDadosConta() {
        List<DadoConta> dadoContas = contaQueryService.getAllDadosConta();
        return ResponseEntity.ok(dadoContas);
    }

    @GetMapping("/{numero}/saldo")
    public ResponseEntity<SaldoResponse> saldo(@PathVariable String numero) {
        SaldoResponse saldoResponse = contaQueryService.consultarSaldo(numero);
        return ResponseEntity.ok(saldoResponse);
    }

    @GetMapping("/{numero}/extrato")
    public ResponseEntity<ExtratoResponse> extrato(@PathVariable String numero) {
        ExtratoResponse extratoResponse = contaQueryService.extrato(numero);
        return ResponseEntity.ok(extratoResponse);
    }

}

