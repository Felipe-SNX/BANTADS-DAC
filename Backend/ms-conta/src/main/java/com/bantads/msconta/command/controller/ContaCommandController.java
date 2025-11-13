package com.bantads.msconta.command.controller;

import com.bantads.msconta.command.service.ContaCommandService;
import com.bantads.msconta.common.conta.dto.OperacaoRequest;
import com.bantads.msconta.common.conta.dto.OperacaoResponse;
import com.bantads.msconta.common.conta.dto.TransferenciaRequest;
import com.bantads.msconta.common.conta.dto.TransferenciaResponse;
import com.bantads.msconta.common.conta.service.DataService;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/contas")
public class ContaCommandController {

    private final ContaCommandService contaCommandService;
    private final DataService dataService;

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {
        dataService.popularBanco();
        return ResponseEntity.ok().build();
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
