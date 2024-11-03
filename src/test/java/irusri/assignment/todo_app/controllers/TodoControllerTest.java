package irusri.assignment.todo_app.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import irusri.assignment.todo_app.dtos.TodoRequest;
import irusri.assignment.todo_app.entity.Todo;
import irusri.assignment.todo_app.entity.User;
import irusri.assignment.todo_app.services.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
public class TodoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TodoService todoService;

    @InjectMocks
    private TodoController todoController;


    private User user;
    private Todo todo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build();
        user = new User().setId(1).setEmail("user@test.com");
        todo = new Todo()
                .setId(12)
                .setTitle("Test title")
                .setDescription("Test description")
                .setDueDate(LocalDate.of(2023, 9, 18))
                .setPriority(7)
                .setCompleted(false)
                .setUser(user);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // Test for creating a new todo
    @Test
    void testCreateTodo() throws Exception {
        TodoRequest todoRequest = new TodoRequest(
                "Test title",
                "Test description",
                LocalDate.of(2023, 9, 18),
                7,
                false);

        when(todoService.createTodoForUser(any(TodoRequest.class), any(User.class))).thenReturn(todo);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(todoRequest))
                        .principal(() -> "user@test.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.description").value("Test description"));
    }

    // Test for updating a new todo
    @Test
    void testUpdateTodo() throws Exception {
        TodoRequest updateTodoRequest = new TodoRequest(
                "Test title",
                "Test description",
                LocalDate.of(2023, 9, 18),
                7,
                true);
        Todo updatedTodo = todo.setCompleted(true);

        when(todoService.updateTodoForUser(any(Integer.class), any(TodoRequest.class), any(User.class))).thenReturn(updatedTodo);


        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mockMvc.perform(put("/api/todos/{todoId}", todo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateTodoRequest))
                        .principal(() -> "user@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value("true"))
        ;
    }

    // Test for deleting a new todo
    @Test
    void testDeleteTodo() throws Exception {
        doNothing().when(todoService).deleteTodoForUser(todo.getId(), user);

        mockMvc.perform(delete("/api/todos/{id}", todo.getId())
                        .principal(() -> "user@test.com"))
                .andExpect(status().isNoContent());

    }

}
