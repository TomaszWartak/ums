package pl.dev4lazy.ums.adapters.inbound.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.dev4lazy.ums.domain.service.EmailAlreadyExistsException;
import pl.dev4lazy.ums.domain.service.UserNotFoundException;
import pl.dev4lazy.ums.utils.Messages;

import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ResponseEntity<Map<String, String>> handleEmailExists(EmailAlreadyExistsException ex) {
        Map<String, String> body = Map.of( "error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body( body );
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, String> body = Map.of( "error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //505
    public ResponseEntity<Map<String, String>> handleAllUncaught(Exception ex) {
        Map<String, String> body = Map.of(
                "status", String.valueOf( HttpStatus.INTERNAL_SERVER_ERROR.value() ),
                "error", String.format( Messages.UNEXPECTED_ERROR, ex.getMessage() )
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

