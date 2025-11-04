package com.bantads.mscliente.core.service;

import com.bantads.mscliente.common.dto.Evento;
import com.bantads.mscliente.common.enums.EEventSource;
import com.bantads.mscliente.common.enums.ESaga;
import com.bantads.mscliente.common.enums.ESagaStatus;
import com.bantads.mscliente.common.enums.ETopics;
import com.bantads.mscliente.core.dto.*;
import com.bantads.mscliente.core.dto.mapper.ClienteMapper;
import com.bantads.mscliente.core.exception.ClienteNaoEncontradoException;
import com.bantads.mscliente.core.exception.CpfJaCadastradoException;
import com.bantads.mscliente.core.exception.EnderecoNaoEncontradoException;
import com.bantads.mscliente.core.model.Cliente;
import com.bantads.mscliente.core.model.Endereco;
import com.bantads.mscliente.core.producer.ClienteEventProducer;
import com.bantads.mscliente.core.repository.ClienteRepository;
import com.bantads.mscliente.core.repository.EnderecoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ClienteEventProducer clienteEventProducer;
    private final ObjectMapper objectMapper;

    public List<ClienteParaAprovarResponse> listarClientes(String filtro){
        List<Cliente> clientes = new ArrayList<>();
        log.info(filtro);
        if(filtro.equals("para_aprovar")){
            log.info("estou no para aprovar");
            clientes = clienteRepository.findAllByAprovado(false);
        }
        else if(filtro.equals("adm_relatorio_clientes")){
            log.info("estou no adm_relatorio_clientes");
            clientes = clienteRepository.findAll();
        }
        else if(filtro.equals("melhores_clientes")){
            log.info("estou no melhores_clientes");
            clientes = clienteRepository.findThreeBestClientes();
        }
        else{
            log.info("Filtro inválido");
        }

        List<ClienteParaAprovarResponse> clienteParaAprovarResponse = new ArrayList<>();

        for (Cliente cliente : clientes) {
            Endereco endereco = enderecoRepository.findById(cliente.getIdEndereco())
                    .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereco", cliente.getNome()));
            ClienteParaAprovarResponse response = ClienteMapper.toClienteParaAprovarResponse(cliente);
            response.setCidade(endereco.getCidade());
            response.setEstado(endereco.getEstado());
            response.setEndereco(endereco.getLogradouro().concat(", ").concat(endereco.getNumero()));
            clienteParaAprovarResponse.add(response);
        }

        return clienteParaAprovarResponse;
    }

    @Transactional
    public ClienteParaAprovarResponse cadastrarCliente(AutoCadastroInfo autoCadastroInfo) {
        Optional<Cliente> clienteExistente = clienteRepository.findByCpf(autoCadastroInfo.getCpf());

        if(clienteExistente.isPresent()){
            throw new CpfJaCadastradoException("Cliente", autoCadastroInfo.getCpf());
        }

        Cliente cliente = ClienteMapper.autoCadastroInfoToCliente(autoCadastroInfo);

        String[] enderecoCompleto = autoCadastroInfo.getEndereco().split(",");
        String logradouro = enderecoCompleto[0].trim();
        String numero = "";
        if (enderecoCompleto.length > 1) {
            numero = enderecoCompleto[1].trim();
        }

        var endereco = Endereco
                .builder()
                .cep(autoCadastroInfo.getCep())
                .cidade(autoCadastroInfo.getCidade())
                .estado(autoCadastroInfo.getEstado())
                .logradouro(logradouro)
                .numero(numero)
                .build();

        Endereco enderecoSalvo = enderecoRepository.save(endereco);

        cliente.setIdEndereco(enderecoSalvo.getId());

        clienteRepository.save(cliente);

        ClienteParaAprovarResponse clienteParaAprovarResponse = ClienteMapper.toClienteParaAprovarResponse(cliente);
        clienteParaAprovarResponse.setEndereco(enderecoSalvo.getLogradouro().concat(", ").concat(enderecoSalvo.getNumero()));
        clienteParaAprovarResponse.setCidade(enderecoSalvo.getCidade());
        clienteParaAprovarResponse.setEstado(enderecoSalvo.getEstado());

        Map<String, Object> autoCadastroInfoMap = new HashMap<>();
        autoCadastroInfoMap.put("autoCadastroInfo", autoCadastroInfo);

        Evento evento = null;
        try {
            evento = criarEvento(autoCadastroInfoMap, ESaga.AUTOCADASTRO_SAGA, EEventSource.CLIENTE_SERVICE);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        clienteEventProducer.sendEvent(ETopics.EVT_CLIENTE_SUCCESS, evento);

        return clienteParaAprovarResponse;
    }

    public RelatorioClientesResponse getClientePorCpf(String cpf){
        Cliente cliente = getCliente(cpf);

        Endereco endereco = getEndereco(cliente.getIdEndereco(), cpf);

        RelatorioClientesResponse relatorioClientesResponse = ClienteMapper.clienteToRelatorioClientesResponse(cliente);
        relatorioClientesResponse.setCidade(endereco.getCidade());
        relatorioClientesResponse.setEstado(endereco.getEstado());
        relatorioClientesResponse.setEndereco(endereco.getLogradouro().concat(", ").concat(endereco.getNumero()));

        return relatorioClientesResponse;
    }

    public void atualizaCliente(PerfilInfo perfilInfo, String cpf){
        Cliente cliente = getCliente(cpf);

        Endereco endereco = getEndereco(cliente.getIdEndereco(), cpf);

        String[] enderecoCompleto = perfilInfo.getEndereco().split(",");

        endereco.setEstado(perfilInfo.getEstado());
        endereco.setCep(perfilInfo.getCep());
        endereco.setCidade(perfilInfo.getCidade());
        endereco.setLogradouro(enderecoCompleto[0]);
        endereco.setNumero(enderecoCompleto[1]);
        enderecoRepository.save(endereco);

        cliente.setNome(perfilInfo.getNome());
        cliente.setEmail(perfilInfo.getEmail());
        cliente.setSalario(perfilInfo.getSalario());
        clienteRepository.save(cliente);        
    }

    public void aprovarCliente(String cpf){
        Cliente cliente = getCliente(cpf);

        cliente.setAprovado(true);
        clienteRepository.save(cliente);
    }

    public void rejeitarCliente(ClienteRejeitadoDto clienteRejeitadoDto, String cpf){
        Cliente cliente = getCliente(cpf);

        cliente.setAprovado(false);
        cliente.setMotivoRejeição(clienteRejeitadoDto.getMotivo());
        clienteRepository.save(cliente);
    }

    private Cliente getCliente(String cpf){
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente", cpf));
    }

    private Endereco getEndereco(long idEndereco, String cpfCliente){
        return enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereco", cpfCliente));
    }

    private String gerarId() {
        return UUID.randomUUID().toString();
    }

    private Evento criarEvento(Map<String, Object> payload, ESaga saga, EEventSource source) throws JsonProcessingException {
        String sagaId = gerarId();

        return Evento.builder()
                .id(sagaId)
                .payload(objectMapper.writeValueAsString(payload))
                .status(ESagaStatus.SAGA_STARTED)
                .saga(saga)
                .source(source)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
