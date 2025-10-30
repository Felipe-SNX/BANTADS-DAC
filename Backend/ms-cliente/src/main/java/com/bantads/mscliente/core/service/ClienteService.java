package com.bantads.mscliente.core.service;

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
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ClienteEventProducer clienteEventProducer;

    public List<ClienteParaAprovarResponse> listarClientes(){
        List<Cliente> clientes = clienteRepository.findAll();
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

    public ClienteParaAprovarResponse cadastrarCliente(AutoCadastroInfo autoCadastroInfo) {
        Optional<Cliente> clienteExistente = clienteRepository.findByCpf(autoCadastroInfo.getCpf());

        if(clienteExistente.isPresent()){
            throw new CpfJaCadastradoException("Cliente", autoCadastroInfo.getCpf());
        }

        Cliente cliente = ClienteMapper.autoCadastroInfoToCliente(autoCadastroInfo);

        String[] enderecoCompleto = autoCadastroInfo.getEndereco().split(",");

        var endereco = Endereco
                .builder()
                .cep(autoCadastroInfo.getCep())
                .cidade(autoCadastroInfo.getCidade())
                .estado(autoCadastroInfo.getEstado())
                .logradouro(enderecoCompleto[0])
                .numero(enderecoCompleto[1])
                .build();

        Endereco enderecoSalvo = enderecoRepository.save(endereco);

        cliente.setIdEndereco(enderecoSalvo.getId());

        clienteRepository.save(cliente);

        ClienteParaAprovarResponse clienteParaAprovarResponse = ClienteMapper.toClienteParaAprovarResponse(cliente);
        clienteParaAprovarResponse.setEndereco(enderecoSalvo.getLogradouro().concat(", ").concat(enderecoSalvo.getNumero()));
        clienteParaAprovarResponse.setCidade(enderecoSalvo.getCidade());
        clienteParaAprovarResponse.setEstado(enderecoSalvo.getEstado());

        clienteEventProducer.publicarInicioSagaAutocadastro(ClienteMapper.toClienteDto(cliente));

        return clienteParaAprovarResponse;
    }

    public RelatorioClientesResponse getClientePorCpf(String cpf){
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente", cpf));

        Endereco endereco = enderecoRepository.findById(cliente.getIdEndereco())
                .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereco", cliente.getCpf()));

        RelatorioClientesResponse relatorioClientesResponse = ClienteMapper.clienteToRelatorioClientesResponse(cliente);
        relatorioClientesResponse.setCidade(endereco.getCidade());
        relatorioClientesResponse.setEstado(endereco.getEstado());
        relatorioClientesResponse.setEndereco(endereco.getLogradouro().concat(", ").concat(endereco.getNumero()));

        return relatorioClientesResponse;
    }

    public void atualizaCliente(PerfilInfo perfilInfo, String cpf){
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente", cpf));

        Endereco endereco = enderecoRepository.findById(cliente.getIdEndereco())
                .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereco", cliente.getCpf()));

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

        log.info("{}", !Objects.equals(cliente.getSalario(), perfilInfo.getSalario()));
        if(!Objects.equals(cliente.getSalario(), perfilInfo.getSalario())){
            clienteEventProducer.publicarInicioSagaAlteracaoPerfil(ClienteMapper.toClienteDto(cliente));
        }
    }

    public void aprovarCliente(String cpf){
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente", cpf));

        cliente.setAprovado(true);
        clienteRepository.save(cliente);
    }

    public void rejeitarCliente(ClienteRejeitadoDto clienteRejeitadoDto, String cpf){
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente", cpf));

        cliente.setAprovado(false);
        cliente.setMotivoRejeição(clienteRejeitadoDto.getMotivo());
        clienteRepository.save(cliente);
    }
}
