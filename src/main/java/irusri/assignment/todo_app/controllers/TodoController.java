package irusri.assignment.todo_app.controllers;

import irusri.assignment.todo_app.dtos.TodoRequest;
import irusri.assignment.todo_app.entity.Todo;
import irusri.assignment.todo_app.entity.User;
import irusri.assignment.todo_app.exceptions.TodoNotFoundException;
import irusri.assignment.todo_app.services.TodoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@Validated
public class TodoController {
    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    @Autowired
    private TodoService todoService;

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody @Valid TodoRequest todoRequest, @AuthenticationPrincipal User user){
        logger.info("Request to create Todo for user: {}", user.getId());
        Todo createdTodo = todoService.createTodoForUser(todoRequest, user);
        logger.info("Todo created with ID: {}", createdTodo.getId());
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Todo>> getTodosForUser(@RequestParam(defaultValue = "0") int pageNo,
                                                      @RequestParam(defaultValue = "10") int pageSize,
                                                      @RequestParam(required = false) String sortBy,
                                                      @AuthenticationPrincipal User user){
        logger.info("Request to get Todos for user: {} with pageNo: {}, pageSize: {}, sortBy: {}",
                user.getId(), pageNo, pageSize, sortBy);
        Page<Todo> todos = (sortBy == null)
                ? todoService.getTodosForUser(user.getId(), pageNo, pageSize)
                : todoService.getTodosSortedForUser(user.getId(), sortBy, pageNo, pageSize);
        logger.info("Retrieved {} Todos for user: {}", todos.getTotalElements(), user.getId());
        return ResponseEntity.ok(todos);

    }

    // Search Todos by keyword
    @GetMapping("/search")
    public ResponseEntity<Page<Todo>> searchTodos(@RequestParam String keyword,
                                                  @RequestParam(defaultValue = "0") int pageNo,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @AuthenticationPrincipal User user) {
        logger.info("Request to search Todos for user: {} with keyword: {}", user.getId(), keyword);
        Page<Todo> todos = todoService.searchTodosForUser(user.getId(), keyword, pageNo, pageSize);
        logger.info("Found {} Todos for keyword: {}", todos.getTotalElements(), keyword);
        return ResponseEntity.ok(todos);
    }

    // Endpoint for getting todos based on completion status
    @GetMapping("/status")
    public ResponseEntity<Page<Todo>> getTodosByCompletionStatus(@RequestParam("status") boolean completionStatus,
                                                                 @RequestParam(defaultValue = "0") int pageNo,
                                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                                 @AuthenticationPrincipal User user) {
        logger.info("Request to get Todos by completion status: {} for user: {}", completionStatus, user.getId());
        Page<Todo> todos = todoService.getTodosByCompletionStatus(user.getId(), completionStatus, pageNo, pageSize);
        logger.info("Retrieved {} Todos for completion status: {} for user: {}", todos.getTotalElements(), completionStatus, user.getId());
        return ResponseEntity.ok(todos);
    }

    // Endpoint for getting todos based on due date
    @GetMapping("/due-date")
    public ResponseEntity<Page<Todo>> getTodosByDueDate(@RequestParam("dueDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
                                                        @RequestParam(defaultValue = "0") int pageNo,
                                                        @RequestParam(defaultValue = "10") int pageSize,
                                                        @AuthenticationPrincipal User user) {
        logger.info("Request to get Todos by due date: {} for user: {}", dueDate, user.getId());
        Page<Todo> todos = todoService.getTodosByDueDate(user.getId(), dueDate, pageNo, pageSize);
        logger.info("Retrieved {} Todos due on: {} for user: {}", todos.getTotalElements(), dueDate, user.getId());
        return ResponseEntity.ok(todos);
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Integer todoId,
                                           @RequestBody @Valid TodoRequest todoRequest,
                                           @AuthenticationPrincipal User user) {
        logger.info("Request to update Todo with ID: {} for user: {}", todoId, user.getId());
        Todo updatedTodo = todoService.updateTodoForUser(todoId, todoRequest, user);
        if (updatedTodo == null) {
            logger.error("Cannot update. Todo not found with ID: {}", todoId);
            throw new TodoNotFoundException("Cannot update. Todo not found with ID: " + todoId);
        }
        logger.info("Todo with ID: {} updated successfully", todoId);
        return ResponseEntity.ok(updatedTodo);
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Integer todoId, @AuthenticationPrincipal User user) {
        logger.info("Request to delete Todo with ID: {} for user: {}", todoId, user.getId());
        todoService.deleteTodoForUser(todoId, user);
        logger.info("Todo with ID: {} deleted successfully", todoId);
        return ResponseEntity.noContent().build();
    }

}
