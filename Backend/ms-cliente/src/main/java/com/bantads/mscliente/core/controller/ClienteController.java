package com.bantads.mscliente.core.controller;

import com.bantads.mscliente.core.dto.AutoCadastroInfo;
import com.bantads.mscliente.core.dto.ClienteParaAprovarResponse;
import com.bantads.mscliente.core.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteParaAprovarResponse>> listar() {
        List<ClienteParaAprovarResponse> clienteParaAprovarResponse = clienteService.listarClientes();
        return ResponseEntity.ok(clienteParaAprovarResponse);
    }

    @PostMapping
    public ResponseEntity<ClienteParaAprovarResponse> cadastrar(@RequestBody AutoCadastroInfo autoCadastroInfo) {
        ClienteParaAprovarResponse clienteParaAprovarResponse = clienteService.cadastrarCliente(autoCadastroInfo);
        return ResponseEntity.status(201).body(clienteParaAprovarResponse);
    }
}
