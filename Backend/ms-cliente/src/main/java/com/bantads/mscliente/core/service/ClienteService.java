package com.bantads.mscliente.core.service;

import com.bantads.mscliente.core.dto.AutoCadastroInfo;
import com.bantads.mscliente.core.dto.ClienteParaAprovarResponse;
import com.bantads.mscliente.core.dto.mapper.ClienteMapper;
import com.bantads.mscliente.core.exception.EnderecoNaoEncontradoException;
import com.bantads.mscliente.core.model.Cliente;
import com.bantads.mscliente.core.model.Endereco;
import com.bantads.mscliente.core.repository.ClienteRepository;
import com.bantads.mscliente.core.repository.EnderecoRepository;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;

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

    public ClienteParaAprovarResponse cadastrarCliente(AutoCadastroInfo autoCadastroInfo){

    }

}
