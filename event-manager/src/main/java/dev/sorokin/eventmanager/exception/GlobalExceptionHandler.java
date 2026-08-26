package dev.sorokin.eventmanager.exception;

import dev.sorokin.eventmanager.model.ErrorMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleNotValidException(MethodArgumentNotValidException e) {
        String message = "Not Valid exception";
        String detailedMessage = e.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ":" + fieldError.getDefaultMessage())
                .collect(Collectors.joining(","));

        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(
                message,
                detailedMessage,
                LocalDateTime.now()
        );

        log.error("Validation exception " + e.getCause());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageResponse);
    }

    @ExceptionHandler(NoSuchFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleNoSuchFoundException(NoSuchFoundException e) {
        String message = "NoSuchFound exception";

        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(
                message,
                e.getMessage(),
                LocalDateTime.now()
        );


        log.error("NoSuchFound Exception " + e.getCause());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessageResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleGenericException(Exception e) {
        String message = "Server error exception";

        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse(
                message,
                e.getMessage(),
                LocalDateTime.now()
        );


        log.error("Generic exception " + e.getCause());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessageResponse);
    }
}
