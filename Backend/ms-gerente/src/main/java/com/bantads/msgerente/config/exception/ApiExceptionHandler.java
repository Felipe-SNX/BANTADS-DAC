package com.bantads.msgerente.config.exception;

import com.bantads.msgerente.core.exception.ErroExecucaoSaga;
import com.bantads.msgerente.core.exception.GerenteNaoEncontradoException;
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

    @ExceptionHandler(GerenteNaoEncontradoException.class)
    public ResponseEntity<ErrorMessage> contaNaoEncontradaException(GerenteNaoEncontradoException ex, HttpServletRequest request) {
        log.error("GerenteNaoEncontradoException capturada: {}", ex.getMessage());
        String message = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, message));
    }

    @ExceptionHandler(ErroExecucaoSaga.class)
    public ResponseEntity<ErrorMessage> erroExecucaoSaga(ErroExecucaoSaga ex, HttpServletRequest request) {
        log.error("ErroExecucaoSaga capturada: {}", ex.getMessage());
        String message = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.INTERNAL_SERVER_ERROR, message));
    }
}
