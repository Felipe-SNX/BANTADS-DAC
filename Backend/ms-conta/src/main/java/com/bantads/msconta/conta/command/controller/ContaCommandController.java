package com.bantads.msconta.conta.command.controller;

import com.bantads.msconta.conta.command.service.ContaCommandService;
import com.bantads.msconta.conta.dto.OperacaoRequest;
import com.bantads.msconta.conta.dto.OperacaoResponse;
import com.bantads.msconta.conta.dto.TransferenciaRequest;
import com.bantads.msconta.conta.dto.TransferenciaResponse;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/contas")
public class ContaCommandController {

    private final ContaCommandService contaCommandService;

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
