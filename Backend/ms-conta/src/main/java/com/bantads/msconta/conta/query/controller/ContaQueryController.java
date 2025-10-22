package com.bantads.msconta.conta.query.controller;

import com.bantads.msconta.conta.dto.ExtratoResponse;
import com.bantads.msconta.conta.dto.SaldoResponse;
import com.bantads.msconta.conta.query.service.ContaQueryService;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/contas")
public class ContaQueryController {

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

}

