package com.bantads.msgerente.core.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msgerente.core.enums.TipoGerente;
import com.bantads.msgerente.core.model.Gerente;
import com.bantads.msgerente.core.repository.GerenteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {

    private final GerenteRepository gerenteRepository;

    @Transactional 
    public void popularBanco() {
        log.info("Excluindo dados antigos");
        gerenteRepository.deleteAll();

        Gerente gerente1 = new Gerente();
        gerente1.setNome("Geniéve");
        gerente1.setEmail("ger1@bantads.com.br");
        gerente1.setCpf("98574307084");
        gerente1.setTelefone("4190909090");
        gerente1.setTipo(TipoGerente.GERENTE);

        Gerente gerente2 = new Gerente();
        gerente2.setNome("Godophredo");
        gerente2.setEmail("ger2@bantads.com.br");
        gerente2.setCpf("64065268052");
        gerente2.setTelefone("4180808080");
        gerente2.setTipo(TipoGerente.GERENTE);

        Gerente gerente3 = new Gerente();
        gerente3.setNome("Gyândula");
        gerente3.setEmail("ger3@bantads.com.br");
        gerente3.setCpf("23862179060");
        gerente3.setTelefone("4170707070");
        gerente3.setTipo(TipoGerente.GERENTE);

        Gerente admin = new Gerente();
        admin.setNome("Adamântio");
        admin.setEmail("adm1@bantads.com.br");
        admin.setCpf("40501740066");
        admin.setTelefone("4160606060");
        admin.setTipo(TipoGerente.ADMINISTRADOR);

        List<Gerente> gerentes = Arrays.asList(gerente1, gerente2, gerente3, admin);

        gerenteRepository.saveAll(gerentes);
        log.info("Foram inseridos {} gerentes no banco de dados.", gerentes.size());
    }
}
