package com.bantads.mscliente.core.controller;

import com.bantads.mscliente.core.dto.*;
import com.bantads.mscliente.core.service.ClienteService;
import com.bantads.mscliente.core.service.DataService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.bantads.mscliente.core.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final DataService dataService;

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {
        dataService.popularBanco();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/checkCpf/{cpf}")
    public ResponseEntity<String> checkCpf(@PathVariable String cpf) {
        Optional<Cliente> cliente = clienteService.checkCpf(cpf);

        if(cliente.isPresent()){
            log.info("Retornando 409");
            return ResponseEntity.status(409).body("Cliente já cadastrado ou aguardando aprovação, CPF duplicado");
        }

        return ResponseEntity.status(200).body("Cpf não cadastrado");
    }


    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) String filtro, @AuthenticationPrincipal UserDetails userDetails) {
        
        Set<String> rolesUsuario = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)         
                .map(role -> role.replace("ROLE_", "")) 
                .collect(Collectors.toSet());

        Map<String, List<String>> permissoesFiltro = new HashMap<>();
        permissoesFiltro.put("para_aprovar", List.of("GERENTE"));
        permissoesFiltro.put("adm_relatorio_clientes", List.of("ADMINISTRADOR"));
        permissoesFiltro.put("melhores_clientes", List.of("GERENTE"));

        if (filtro != null && !filtro.isEmpty()) {
            
            List<String> rolesPermitidas = permissoesFiltro.get(filtro);

            if (rolesPermitidas == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Filtro '" + filtro + "' é inválido.");
            }

            if (rolesPermitidas.stream().noneMatch(rolesUsuario::contains)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Usuário com role(s) " + rolesUsuario + " não tem permissão para o filtro '" + filtro + "'.");
            }
        }

        List<ClienteParaAprovarResponse> clienteResponse = clienteService.listarClientes(filtro);
        return ResponseEntity.ok(clienteResponse);
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
    public ResponseEntity<Void> aprovarCliente(@RequestBody ClienteParaAprovarRequest clienteParaAprovarRequest, @PathVariable String cpf) {
        clienteService.aprovarCliente(clienteParaAprovarRequest, cpf);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{cpf}/rejeitar")
    public ResponseEntity<Void> rejeitarCliente(@RequestBody ClienteRejeitadoDto clienteRejeitadoDto, @PathVariable String cpf) {
        clienteService.rejeitarCliente(clienteRejeitadoDto, cpf);
        return ResponseEntity.ok().build();
    }
}
