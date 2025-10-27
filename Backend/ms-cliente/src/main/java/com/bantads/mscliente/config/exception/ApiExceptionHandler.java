package com.bantads.mscliente.config.exception;

import com.bantads.mscliente.core.exception.ClienteNaoEncontradoException;
import com.bantads.mscliente.core.exception.CpfJaCadastradoException;
import com.bantads.mscliente.core.exception.EnderecoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CpfJaCadastradoException.class)
    public ResponseEntity<ErrorMessage> cpfJaCadastradoException(CpfJaCadastradoException ex, HttpServletRequest request) {
        log.error("CpfJaCadastradoException capturada: {}", ex.getMessage());
        String message = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, message));
    }

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<ErrorMessage> clienteNaoEncontradoException(ClienteNaoEncontradoException ex, HttpServletRequest request) {
        log.error("ClienteNaoEncontradoException capturada: {}", ex.getMessage());
        String message = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, message));
    }

    @ExceptionHandler(EnderecoNaoEncontradoException.class)
    public ResponseEntity<ErrorMessage> enderecoNaoEncontradoException(EnderecoNaoEncontradoException ex, HttpServletRequest request) {
        log.error("EnderecoNaoEncontradoException capturada: {}", ex.getMessage());
        String message = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, message));
    }
}

