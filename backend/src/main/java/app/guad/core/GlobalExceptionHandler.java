package app.guad.core;

import app.guad.security.InvalidAuthenticatedUserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = org.springframework.web.bind.annotation.RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(ApiError.of(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        var message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b).orElse("Validation failed");
        return ResponseEntity.badRequest().body(ApiError.of(400, "Bad Request", message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiError.of(400, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(InvalidAuthenticatedUserException.class)
    ResponseEntity<ApiError> handleInvalidAuthenticatedUser(InvalidAuthenticatedUserException ex) {
        return ResponseEntity.status(401).body(ApiError.of(401, "Unauthorized", ex.getMessage()));
    }
}
