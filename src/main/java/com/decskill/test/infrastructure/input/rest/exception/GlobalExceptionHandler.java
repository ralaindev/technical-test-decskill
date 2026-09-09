package com.decskill.test.infrastructure.input.rest.exception;

import com.decskill.test.application.exception.PriceNotFoundException;
import com.decskill.test.infrastructure.input.rest.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String INVALID_PARAMETERS_MESSAGE = "Parámetros de entrada no válidos.";

    @ExceptionHandler({
            MissingServletRequestParameterException.class, // Falta un parámetro @RequestParam obligatorio.
            MethodArgumentTypeMismatchException.class, // El texto recibido no se puede convertir a OffsetDateTime o Long.
            ConstraintViolationException.class // @Min o @NotNull rechazan el valor antes de ejecutar el controlador.
    })
    public ResponseEntity<ErrorResponse> handleInvalidParameters() {
        ErrorResponse error = new ErrorResponse()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(INVALID_PARAMETERS_MESSAGE);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePriceNotFound(PriceNotFoundException exception) {
        ErrorResponse error = new ErrorResponse()
                .status(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}
