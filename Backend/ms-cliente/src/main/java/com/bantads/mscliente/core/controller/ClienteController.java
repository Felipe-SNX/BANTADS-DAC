package com.bantads.mscliente.core.controller;

import com.bantads.mscliente.core.dto.*;
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
    public ResponseEntity<List<ClienteParaAprovarResponse>> listar(@RequestParam("filtro") String filtro) {
        List<ClienteParaAprovarResponse> clienteParaAprovarResponse = clienteService.listarClientes();
        return ResponseEntity.ok(clienteParaAprovarResponse);
    }

    @PostMapping
    public ResponseEntity<ClienteParaAprovarResponse> cadastrarCliente(@RequestBody AutoCadastroInfo autoCadastroInfo) {
        ClienteParaAprovarResponse clienteParaAprovarResponse = clienteService.cadastrarCliente(autoCadastroInfo);
        return ResponseEntity.status(201).body(clienteParaAprovarResponse);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<RelatorioClientesResponse> getCliente(@PathVariable String cpf) {
        RelatorioClientesResponse relatorioClientesResponse = clienteService.getClientePorCpf(cpf);
        return ResponseEntity.ok(relatorioClientesResponse);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<ClienteParaAprovarResponse> atualizaCliente(@RequestBody PerfilInfo perfilInfo, @PathVariable String cpf) {
        clienteService.atualizaCliente(perfilInfo, cpf);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{cpf}/aprovar")
    public ResponseEntity<Void> aprovarCliente(@PathVariable String cpf) {
        clienteService.aprovarCliente(cpf);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{cpf}/rejeitar")
    public ResponseEntity<Void> rejeitarCliente(@RequestBody ClienteRejeitadoDto clienteRejeitadoDto, @PathVariable String cpf) {
        clienteService.rejeitarCliente(clienteRejeitadoDto, cpf);
        return ResponseEntity.ok().build();
    }
}
