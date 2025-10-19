package com.bantads.msconta.core.controller;

import com.bantads.msconta.core.dto.SaldoResponse;
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

}
