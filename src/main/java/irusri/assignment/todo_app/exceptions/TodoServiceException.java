package irusri.assignment.todo_app.exceptions;

public class TodoServiceException extends RuntimeException {
    public TodoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}