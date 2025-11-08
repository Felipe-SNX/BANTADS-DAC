package com.bantads.msauth.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String email;

    private final JavaMailSender mailSender;

    public void enviarEmailAprovado(String para, String assunto, String texto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(email); 
            message.setTo(para);
            message.setSubject(assunto);
            message.setText(texto);
            
            mailSender.send(message);
            log.info("E-mail enviado com sucesso!");

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}
