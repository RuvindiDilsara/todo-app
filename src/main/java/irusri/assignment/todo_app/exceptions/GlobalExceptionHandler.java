package irusri.assignment.todo_app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


//Global exception handler that catches and handles exceptions
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle Todo not found exception
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTodoNotFoundException(TodoNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }



    // Handle illegal actions exception
    @ExceptionHandler(IllegalActionException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalActionException(IllegalActionException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle Todo service-related exceptions
    @ExceptionHandler(TodoServiceException.class)
    public ResponseEntity<Map<String, Object>> handleTodoServiceException(TodoServiceException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle resource not found exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle illegal state exceptions
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle resource already exist exception
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Handle validation exceptions for @Valid annotation violations
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validation failed");

        List<String> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        response.put("errors", validationErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle JWT and security-related exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(Exception ex) {
        Map<String, Object> response = new HashMap<>();

        if (ex instanceof BadCredentialsException) {
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "Invalid credentials. The email or password is incorrect.");
        } else if (ex instanceof AccountStatusException) {
            response.put("status", HttpStatus.FORBIDDEN.value());
            response.put("message", "Your account is locked or disabled.");
        } else if (ex instanceof AccessDeniedException) {
            response.put("status", HttpStatus.FORBIDDEN.value());
            response.put("message", "Access is denied. You do not have permission to access this resource.");
        } else if (ex instanceof SignatureException) {
            response.put("status", HttpStatus.FORBIDDEN.value());
            response.put("message", "Invalid JWT signature.");
        } else if (ex instanceof ExpiredJwtException) {
            response.put("status", HttpStatus.FORBIDDEN.value());
            response.put("message", "The JWT token has expired.");
        } else {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "An unknown error occurred.");
        }

        ex.printStackTrace();

        return new ResponseEntity<>(response, HttpStatus.valueOf((int) response.get("status")));
    }
}
