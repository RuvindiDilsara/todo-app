package irusri.assignment.todo_app.services;

import irusri.assignment.todo_app.dtos.TodoRequest;
import irusri.assignment.todo_app.entity.Todo;
import irusri.assignment.todo_app.entity.User;
import irusri.assignment.todo_app.exceptions.TodoNotFoundException;
import irusri.assignment.todo_app.repositories.TodoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;

    @Transactional
    public Todo createTodoForUser(TodoRequest todoRequest, User user){
        Todo todo = new Todo();
        todo
                .setTitle(todoRequest.getTitle())
                .setDescription(todoRequest.getDescription())
                .setDueDate(todoRequest.getDueDate())
                .setPriority(todoRequest.getPriority())
                .setCompleted(false)
                .setUser(user);
        System.out.println(todo);
        try{
            todo = todoRepository.save(todo);
            return todo;
        }
        catch (Exception e){
            throw new RuntimeException("Error creating todo", e);
        }

    }

    public Page<Todo> getTodosForUser(Integer userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return todoRepository.findByUserId(userId, pageable);
    }

    // Search Todos by keyword for a user with pagination
    public Page<Todo> searchTodosForUser(Integer userId, String keyword, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return todoRepository.searchByKeyword(userId, keyword, pageable);
    }

    // Sort Todos by due date or priority
    public Page<Todo> getTodosSortedForUser(Integer userId, String sortBy, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        System.out.println(sortBy);
        if ("dueDate".equalsIgnoreCase(sortBy)) {
            return todoRepository.findByUserIdOrderByDueDateAsc(userId, pageable);
        } else if ("priority".equalsIgnoreCase(sortBy)) {
            System.out.println("inside priority");
            return todoRepository.findByUserIdOrderByPriorityAsc(userId, pageable);
        }
        return getTodosForUser(userId, pageNo, pageSize);
    }

    // Get todos based on completion status for a user
    public Page<Todo> getTodosByCompletionStatus(Integer userId, boolean completionStatus, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return todoRepository.findByUserIdAndIsCompleted(userId, completionStatus, pageable);
    }

    // Get todos based on due date for a user
    public Page<Todo> getTodosByDueDate(Integer userId, LocalDate dueDate, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return todoRepository.findByUserIdAndDueDate(userId, dueDate, pageable);
    }


    public void deleteTodoForUser(Integer todoId, User user) {
        Todo todo = todoRepository.findById(todoId)
                .filter(todo1 -> todo1.getUser().getId().equals(user.getId())) // Ensures user can only update their own Todo
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with ID: " + todoId + "for user with ID: " + user.getId()));
        try {
            todoRepository.delete(todo);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting todo", e);
        }
    }

    @Transactional
    public Todo updateTodoForUser(Integer todoId, TodoRequest todoRequest, User user) {
        Todo todo = todoRepository.findById(todoId)
                .filter(todo1 -> todo1.getUser().getId().equals(user.getId())) // Ensures user can only update their own Todo
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with ID: " + todoId + "for user with ID: " + user.getId()));
        System.out.println("before");
        System.out.println(todo);
        try {
            todo.setTitle(todoRequest.getTitle());
            todo.setDescription(todoRequest.getDescription());
            todo.setPriority(todoRequest.getPriority());
            todo.setCompleted(todoRequest.isCompleted());
            todo.setDueDate(todoRequest.getDueDate());
            System.out.println(todo);
            return todoRepository.save(todo);
        } catch (Exception e) {
            throw new RuntimeException("Error updating todo", e);
        }
    }

}
