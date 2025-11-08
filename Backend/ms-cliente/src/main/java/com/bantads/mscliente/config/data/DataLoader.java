package com.bantads.mscliente.config.data;

import com.bantads.mscliente.core.service.DataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DataService dataService;

    @Override
    public void run(String... args) throws Exception {
       dataService.popularBanco();
    }
}
