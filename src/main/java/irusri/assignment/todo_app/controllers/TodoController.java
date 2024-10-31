package irusri.assignment.todo_app.controllers;

import irusri.assignment.todo_app.dtos.TodoRequest;
import irusri.assignment.todo_app.entity.Todo;
import irusri.assignment.todo_app.entity.User;
import irusri.assignment.todo_app.exceptions.TodoNotFoundException;
import irusri.assignment.todo_app.services.TodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody @Valid TodoRequest todoRequest, @AuthenticationPrincipal User user){
        Todo createdTodo = todoService.createTodoForUser(todoRequest, user);
        System.out.println(createdTodo);
        return new ResponseEntity<>(createdTodo, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Todo>> getTodosForUser(@RequestParam(defaultValue = "0") int pageNo,
                                                      @RequestParam(defaultValue = "10") int pageSize,
                                                      @RequestParam(required = false) String sortBy,
                                                      @AuthenticationPrincipal User user){
        Page<Todo> todos = (sortBy == null)
                ? todoService.getTodosForUser(user.getId(), pageNo, pageSize)
                : todoService.getTodosSortedForUser(user.getId(), sortBy, pageNo, pageSize);
        return ResponseEntity.ok(todos);

    }

    // Search Todos by keyword
    @GetMapping("/search")
    public ResponseEntity<Page<Todo>> searchTodos(@RequestParam String keyword,
                                                  @RequestParam(defaultValue = "0") int pageNo,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  @AuthenticationPrincipal User user) {
        System.out.println("controller inside");
        System.out.println(keyword);
        Page<Todo> todos = todoService.searchTodosForUser(user.getId(), keyword, pageNo, pageSize);
        return ResponseEntity.ok(todos);
    }

    // Endpoint for getting todos based on completion status
    @GetMapping("/status")
    public ResponseEntity<Page<Todo>> getTodosByCompletionStatus(@RequestParam("status") boolean completionStatus,
                                                                 @RequestParam(defaultValue = "0") int pageNo,
                                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                                 @AuthenticationPrincipal User user) {
        Page<Todo> todos = todoService.getTodosByCompletionStatus(user.getId(), completionStatus, pageNo, pageSize);
        return ResponseEntity.ok(todos);
    }

    // Endpoint for getting todos based on due date
    @GetMapping("/due-date")
    public ResponseEntity<Page<Todo>> getTodosByDueDate(@RequestParam("dueDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
                                                        @RequestParam(defaultValue = "0") int pageNo,
                                                        @RequestParam(defaultValue = "10") int pageSize,
                                                        @AuthenticationPrincipal User user) {
        Page<Todo> todos = todoService.getTodosByDueDate(user.getId(), dueDate, pageNo, pageSize);
        return ResponseEntity.ok(todos);
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Integer todoId,
                                           @RequestBody @Valid TodoRequest todoRequest,
                                           @AuthenticationPrincipal User user) {
        System.out.println("inside controller update");
        System.out.println(todoRequest);
        Todo updatedTodo = todoService.updateTodoForUser(todoId, todoRequest, user);
        if (updatedTodo == null) {
            throw new TodoNotFoundException("Cannot update. Todo not found with ID: " + todoId);
        }
        return ResponseEntity.ok(updatedTodo);
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Integer todoId, @AuthenticationPrincipal User user) {
        todoService.deleteTodoForUser(todoId, user);
        return ResponseEntity.noContent().build();
    }

}
