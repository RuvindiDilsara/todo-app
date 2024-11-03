package irusri.assignment.todo_app.services;

import irusri.assignment.todo_app.dtos.TodoRequest;
import irusri.assignment.todo_app.entity.Todo;
import irusri.assignment.todo_app.entity.User;
import irusri.assignment.todo_app.repositories.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TodoServiceTest {
    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private User user;
    private Todo todo;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        user = new User().setId(1).setEmail("user@test.com");
        todo = new Todo()
                .setId(12)
                .setTitle("Test title")
                .setDescription("Test description")
                .setDueDate(LocalDate.of(2023, 9, 18))
                .setPriority(70)
                .setCompleted(false)
                .setUser(user);
    }

    // Test for adding a new todo
    @Test
    void testCreateTodoForUser() {
        TodoRequest todoRequest = new TodoRequest(
                "Test title",
                "Test description",
                LocalDate.of(2023, 9, 18),
                7,
                false);
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        Todo createdTodo = todoService.createTodoForUser(todoRequest, user);

        assertEquals(todo.getTitle(), createdTodo.getTitle());
        assertEquals(todo.getDescription(), createdTodo.getDescription());
        assertEquals(todo.getDueDate(), createdTodo.getDueDate());
        assertEquals(todo.getPriority(), createdTodo.getPriority());
        assertEquals(todo.getUser(), createdTodo.getUser());

        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    // Test for reading all the todos of a user
    @Test
    void testGetTodosForUser() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Todo> todos = new PageImpl<>(Collections.singletonList(todo), pageable, 1);

        when(todoRepository.findByUserId(user.getId(), pageable)).thenReturn(todos);

        Page<Todo> result = todoService.getTodosForUser(user.getId(), 0, 5);

        assertEquals(1, result.getTotalElements());
        Todo todo1 = result.getContent().get(0);
        assertEquals(todo.getTitle(), todo1.getTitle());
        assertEquals(todo.getDescription(), todo1.getDescription());
        assertEquals(todo.getDueDate(), todo1.getDueDate());
        assertEquals(todo.getPriority(), todo1.getPriority());
        assertEquals(todo.getUser(), todo1.getUser());

        verify(todoRepository, times(1)).findByUserId(user.getId(), pageable);
    }

    // Test for searching todos by keyword
    @Test
    void testSearchTodosForUser() {
        Pageable pageable = PageRequest.of(0, 5);
        String keyword = "Test";
        Page<Todo> todos = new PageImpl<>(Collections.singletonList(todo), pageable, 1);

        when(todoRepository.searchByKeyword(user.getId(), keyword, pageable)).thenReturn(todos);

        Page<Todo> result = todoService.searchTodosForUser(user.getId(), keyword, 0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test title", result.getContent().get(0).getTitle());
        verify(todoRepository, times(1)).searchByKeyword(user.getId(), keyword, pageable);
    }

    // Test for sorting todos by specified attribute
    @Test
    void testGetTodosSortedForUser() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Todo> todos = new PageImpl<>(Collections.singletonList(todo), pageable, 1);

        when(todoRepository.findByUserIdOrderByDueDateAsc(user.getId(), pageable)).thenReturn(todos);

        Page<Todo> result = todoService.getTodosSortedForUser(user.getId(), "dueDate", 0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test title", result.getContent().get(0).getTitle());
        verify(todoRepository, times(1)).findByUserIdOrderByDueDateAsc(user.getId(), pageable);
    }

    // Test for retrieving todos by completion status
    @Test
    void testGetTodosByCompletionStatus() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Todo> todos = new PageImpl<>(Collections.singletonList(todo), pageable, 1);

        when(todoRepository.findByUserIdAndIsCompleted(user.getId(), false, pageable)).thenReturn(todos);

        Page<Todo> result = todoService.getTodosByCompletionStatus(user.getId(), false, 0, 5);

        assertEquals(1, result.getTotalElements());
        assertFalse(result.getContent().get(0).isCompleted());
        verify(todoRepository, times(1)).findByUserIdAndIsCompleted(user.getId(), false, pageable);
    }

    // Test for retrieving todos by due date
    @Test
    void testGetTodosByDueDate() {
        Pageable pageable = PageRequest.of(0, 5);
        LocalDate dueDate = LocalDate.of(2023, 9, 18);
        Page<Todo> todos = new PageImpl<>(Collections.singletonList(todo), pageable, 1);

        when(todoRepository.findByUserIdAndDueDate(user.getId(), dueDate, pageable)).thenReturn(todos);

        Page<Todo> result = todoService.getTodosByDueDate(user.getId(), dueDate, 0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals(dueDate, result.getContent().get(0).getDueDate());
        verify(todoRepository, times(1)).findByUserIdAndDueDate(user.getId(), dueDate, pageable);
    }

    // Test for deleting a todo
    @Test
    void testDeleteTodoForUser() {
        when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

        todoService.deleteTodoForUser(todo.getId(), user);

        verify(todoRepository, times(1)).delete(todo);
    }

    // Test for updating a todo
    @Test
    void testUpdateTodoForUser() {
        TodoRequest todoRequest = new TodoRequest(
                "Updated title",
                "Updated description",
                LocalDate.of(2023, 10, 1),
                5,
                true);

        when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        Todo updatedTodo = todoService.updateTodoForUser(todo.getId(), todoRequest, user);

        assertEquals("Updated title", updatedTodo.getTitle());
        assertEquals("Updated description", updatedTodo.getDescription());
        assertEquals(LocalDate.of(2023, 10, 1), updatedTodo.getDueDate());
        assertEquals(5, updatedTodo.getPriority());
        assertTrue(updatedTodo.isCompleted());

        verify(todoRepository, times(1)).save(any(Todo.class));
    }
}
