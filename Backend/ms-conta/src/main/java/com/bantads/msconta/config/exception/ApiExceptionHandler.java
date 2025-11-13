package com.bantads.msconta.config.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bantads.msconta.common.conta.exception.ContaNaoEncontradaException;
import com.bantads.msconta.common.conta.exception.ErroExecucaoSaga;
import com.bantads.msconta.common.conta.exception.TransferenciaInvalidaException;
import com.bantads.msconta.common.conta.exception.ValorInvalidoException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ContaNaoEncontradaException.class)
    public ResponseEntity<ErrorMessage> contaNaoEncontradaException(ContaNaoEncontradaException ex, HttpServletRequest request) {
        log.error("ContaNaoEncontradaException capturada: {}", ex.getMessage());
        String message = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, message));
    }

    @ExceptionHandler(ValorInvalidoException.class)
    public ResponseEntity<ErrorMessage> valorInvalidoException(ValorInvalidoException ex, HttpServletRequest request) {
        log.error("valorInvalidoException capturada: {}", ex.getMessage());
        String message = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(TransferenciaInvalidaException.class)
    public ResponseEntity<ErrorMessage> transferenciaInvalidaException(TransferenciaInvalidaException ex, HttpServletRequest request) {
        log.error("transferenciaInvalidaException capturada: {}", ex.getMessage());
        String message = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, message));
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
