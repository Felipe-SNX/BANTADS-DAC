package com.bantads.msconta.core.controller;

import com.bantads.msconta.core.dto.*;
import com.bantads.msconta.core.service.ContaCommandService;
import com.bantads.msconta.core.service.ContaQueryService;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/contas")
public class ContaController {

    private final ContaCommandService contaCommandService;
    private final ContaQueryService contaQueryService;

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

    @PostMapping("/{numero}/depositar")
    public ResponseEntity<OperacaoResponse> depositar(@RequestBody OperacaoRequest operacao, @PathVariable String numero) {
        OperacaoResponse operacaoResponse = contaCommandService.depositar(operacao, numero);
        return ResponseEntity.ok(operacaoResponse);
    }

    @PostMapping("/{numero}/sacar")
    public ResponseEntity<OperacaoResponse> sacar(@RequestBody OperacaoRequest operacao, @PathVariable String numero) {
        OperacaoResponse operacaoResponse = contaCommandService.sacar(operacao, numero);
        return ResponseEntity.ok(operacaoResponse);
    }

    @PostMapping("/{numero}/transferir")
    public ResponseEntity<TransferenciaResponse> transferir(@RequestBody TransferenciaRequest transferencia, @PathVariable String numero) {
        TransferenciaResponse transferenciaResponse = contaCommandService.transferir(transferencia, numero);
        return ResponseEntity.ok(transferenciaResponse);
    }

}
