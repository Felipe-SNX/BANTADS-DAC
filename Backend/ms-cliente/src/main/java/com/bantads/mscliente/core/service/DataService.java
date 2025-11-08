package com.bantads.mscliente.core.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.mscliente.core.model.Cliente;
import com.bantads.mscliente.core.model.Endereco;
import com.bantads.mscliente.core.repository.ClienteRepository;
import com.bantads.mscliente.core.repository.EnderecoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;

    @Transactional 
    public void popularBanco() {
        log.info("Excluindo dados antigos");
        enderecoRepository.deleteAll();
        clienteRepository.deleteAll();

        Endereco endereco1 = new Endereco();
        endereco1.setLogradouro("Estrada 1");
        endereco1.setCep("10000000");
        endereco1.setCidade("Curitiba");
        endereco1.setEstado("Paraná");
        endereco1.setComplemento("Casa");
        endereco1.setNumero("1");

        Endereco endereco2 = new Endereco();
        endereco2.setLogradouro("Avenida 2");
        endereco2.setCep("30000000");
        endereco2.setCidade("Porto Alegre");
        endereco2.setEstado("Rio Grande do Sul");
        endereco2.setComplemento("Apê");
        endereco2.setNumero("3");

        Endereco endereco3 = new Endereco();
        endereco3.setLogradouro("Avenida 1");
        endereco3.setCep("20000000");
        endereco3.setCidade("Florianópolis");
        endereco3.setEstado("Santa Catarina");
        endereco3.setComplemento("Apartamento 2");
        endereco3.setNumero("2");

        List<Endereco> enderecos = Arrays.asList(endereco1, endereco2, endereco3);

        enderecoRepository.saveAll(enderecos);
        log.info("Foram inseridos {} enderecos no banco de dados.", enderecos.size());

        Cliente cliente1 = new Cliente();
        cliente1.setNome("Catharyna");
        cliente1.setEmail("cli1@bantads.com.br");
        cliente1.setCpf("12912861012");
        cliente1.setTelefone("4199999999");
        cliente1.setSalario(BigDecimal.valueOf(10000.00));
        cliente1.setIdEndereco(endereco1.getId());
        cliente1.setGerente("98574307084");
        cliente1.setAprovado(true);
        cliente1.setSaldo(BigDecimal.valueOf(800.00));

        Cliente cliente2 = new Cliente();
        cliente2.setNome("Cleuddônio");
        cliente2.setEmail("cli2@bantads.com.br");
        cliente2.setCpf("09506382000");
        cliente2.setTelefone("4498888888");
        cliente2.setSalario(BigDecimal.valueOf(20000.00));
        cliente2.setIdEndereco(endereco2.getId());
        cliente2.setGerente("64065268052");
        cliente2.setAprovado(true);
        cliente2.setSaldo(BigDecimal.valueOf(-10000.00));

        Cliente cliente3 = new Cliente();
        cliente3.setNome("Catianna");
        cliente3.setEmail("cli3@bantads.com.br");
        cliente3.setCpf("85733854057");
        cliente3.setTelefone("47977777777");
        cliente3.setSalario(BigDecimal.valueOf(3000.00));
        cliente3.setIdEndereco(endereco3.getId());
        cliente3.setGerente("23862179060");
        cliente3.setAprovado(true);
        cliente3.setSaldo(BigDecimal.valueOf(-1000.00));

        Cliente cliente4 = new Cliente();
        cliente4.setNome("Cutardo");
        cliente4.setEmail("cli4@bantads.com.br");
        cliente4.setCpf("58872160006");
        cliente4.setTelefone("4998000000");
        cliente4.setSalario(BigDecimal.valueOf(500.00));
        cliente4.setIdEndereco(endereco1.getId());
        cliente4.setGerente("98574307084");
        cliente4.setAprovado(true);
        cliente4.setSaldo(BigDecimal.valueOf(150000.00));

        Cliente cliente5 = new Cliente();
        cliente5.setNome("Coândrya");
        cliente5.setEmail("cli5@bantads.com.br");
        cliente5.setCpf("76179646090");
        cliente5.setTelefone("49989896976");
        cliente5.setSalario(BigDecimal.valueOf(1500.00));
        cliente5.setIdEndereco(endereco2.getId());
        cliente5.setGerente("64065268052");
        cliente5.setAprovado(true);
        cliente5.setSaldo(BigDecimal.valueOf(1500.00));

        List<Cliente> clientes = Arrays.asList(cliente1, cliente2, cliente3, cliente4, cliente5);

        clienteRepository.saveAll(clientes);
        log.info("Foram inseridos {} clientes no banco de dados.", clientes.size());
    }
}
