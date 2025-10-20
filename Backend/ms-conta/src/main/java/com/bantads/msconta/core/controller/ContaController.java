package com.bantads.msconta.core.controller;

import com.bantads.msconta.core.dto.*;
import com.bantads.msconta.core.service.ContaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/contas")
public class ContaController {

    private final ContaService contaService;

    @GetMapping("/{numero}/saldo")
    public ResponseEntity<SaldoResponse> saldo(@PathVariable String numero) {
        SaldoResponse saldoResponse = contaService.consultarSaldo(numero);
        return ResponseEntity.ok(saldoResponse);
    }

    @PostMapping("/{numero}/depositar")
    public ResponseEntity<OperacaoResponse> depositar(@RequestBody OperacaoRequest operacao, @PathVariable String numero) {
        OperacaoResponse operacaoResponse = contaService.depositar(operacao, numero);
        return ResponseEntity.ok(operacaoResponse);
    }

    @PostMapping("/{numero}/sacar")
    public ResponseEntity<OperacaoResponse> sacar(@RequestBody OperacaoRequest operacao, @PathVariable String numero) {
        OperacaoResponse operacaoResponse = contaService.sacar(operacao, numero);
        return ResponseEntity.ok(operacaoResponse);
    }

    @PostMapping("/{numero}/transferir")
    public ResponseEntity<TransferenciaResponse> transferir(@RequestBody TransferenciaRequest transferencia, @PathVariable String numero) {
        TransferenciaResponse transferenciaResponse = contaService.transferir(transferencia, numero);
        return ResponseEntity.ok(transferenciaResponse);
    }

    @GetMapping("/{numero}/extrato")
    public ResponseEntity<ExtratoResponse> extrato(@PathVariable String numero) {
        ExtratoResponse extratoResponse = contaService.extrato(numero);
        return ResponseEntity.ok(extratoResponse);
    }

}
