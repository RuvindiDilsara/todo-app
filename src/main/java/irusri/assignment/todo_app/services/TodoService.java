package irusri.assignment.todo_app.services;

import irusri.assignment.todo_app.dtos.TodoRequest;
import irusri.assignment.todo_app.entity.Todo;
import irusri.assignment.todo_app.entity.User;
import irusri.assignment.todo_app.exceptions.IllegalActionException;
import irusri.assignment.todo_app.exceptions.ResourceAlreadyExistsException;
import irusri.assignment.todo_app.exceptions.TodoNotFoundException;
import irusri.assignment.todo_app.exceptions.TodoServiceException;
import irusri.assignment.todo_app.repositories.TodoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TodoService {
    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);
    @Autowired
    private TodoRepository todoRepository;

    // create new todo
    @Transactional
    public Todo createTodoForUser(TodoRequest todoRequest, User user) {
        try {
            logger.info("Creating Todo for user: {}", user.getId());
            Todo todo = new Todo()
                    .setTitle(todoRequest.getTitle())
                    .setDescription(todoRequest.getDescription())
                    .setDueDate(todoRequest.getDueDate())
                    .setPriority(todoRequest.getPriority())
                    .setCompleted(false)
                    .setUser(user);

            return todoRepository.save(todo);
        } catch (DataIntegrityViolationException e) {
            logger.error("Failed to create Todo: A Todo with the same details already exists for user: {}", user.getId(), e);
            throw new ResourceAlreadyExistsException("A Todo with the same details already exists.");
        } catch (Exception e) {
            logger.error("Failed to create Todo for user: {}", user.getId(), e);
            throw new TodoServiceException("Failed to create Todo", e);
        }
    }

    // get todos for given user with pagination
    public Page<Todo> getTodosForUser(Integer userId, int pageNo, int pageSize) {
        try {
            logger.info("Retrieving Todos for user: {}", userId);
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            return todoRepository.findByUserId(userId, pageable);
        } catch (Exception e) {
            logger.error("Error retrieving todos for user with ID: {}", userId, e);
            throw new TodoServiceException("Error retrieving todos", e);
        }
    }

    // Search Todos by keyword for a user with pagination
    public Page<Todo> searchTodosForUser(Integer userId, String keyword, int pageNo, int pageSize) {
        try {
            logger.info("Searching Todos for user: {} with keyword: {}", userId, keyword);
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            return todoRepository.searchByKeyword(userId, keyword, pageable);
        } catch (DataAccessException e) {
            logger.error("Error searching todos for user with ID: {}", userId, e);
            throw new TodoServiceException("Error searching todos for user with ID: " + userId, e);
        }
    }

    // Sort Todos by due date or priority for a user with pagination
    public Page<Todo> getTodosSortedForUser(Integer userId, String sortBy, int pageNo, int pageSize) {
        try {
            logger.info("Sorting Todos for user: {} by {}", userId, sortBy);
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            if ("dueDate".equalsIgnoreCase(sortBy)) {
                return todoRepository.findByUserIdOrderByDueDateAsc(userId, pageable);
            } else if ("priority".equalsIgnoreCase(sortBy)) {
                return todoRepository.findByUserIdOrderByPriorityAsc(userId, pageable);
            }
            return getTodosForUser(userId, pageNo, pageSize);
        } catch (DataAccessException e) {
            logger.error("Error sorting todos for user with ID: {}", userId, e);
            throw new TodoServiceException("Error sorting todos for user with ID: " + userId, e);
        }
    }

    // Get todos based on completion status for a user with pagination
    public Page<Todo> getTodosByCompletionStatus(Integer userId, boolean completionStatus, int pageNo, int pageSize) {
        try {
            logger.info("Retrieving Todos for user: {} by completion status: {}", userId, completionStatus);
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            return todoRepository.findByUserIdAndIsCompleted(userId, completionStatus, pageable);
        } catch (DataAccessException e) {
            logger.error("Error retrieving todos by completion status for user with ID: {}", userId, e);
            throw new TodoServiceException("Error retrieving todos by completion status for user with ID: " + userId, e);
        }
    }

    // Get todos based on due date for a user with pagination
    public Page<Todo> getTodosByDueDate(Integer userId, LocalDate dueDate, int pageNo, int pageSize) {
        try {
            logger.info("Retrieving Todos for user: {} by due date: {}", userId, dueDate);
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            return todoRepository.findByUserIdAndDueDate(userId, dueDate, pageable);
        } catch (DataAccessException e) {
            logger.error("Error retrieving todos by due date for user with ID: {}", userId, e);
            throw new TodoServiceException("Error retrieving todos by due date for user with ID: " + userId, e);
        }
    }

    // delete Todo
    public void deleteTodoForUser(Integer todoId, User user) {
        Todo todo = todoRepository.findById(todoId)
                .filter(todo1 -> todo1.getUser().getId().equals(user.getId())) // Ensures user can only update their own Todo
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with ID: " + todoId + " for user with ID: " + user.getId()));
        try {
            logger.info("Deleting Todo with ID: {} for user: {}", todoId, user.getId());
            todoRepository.delete(todo);
        } catch (DataIntegrityViolationException e) {
            logger.error("Cannot delete Todo with ID: {} as it is referenced elsewhere.", todoId, e);
            throw new IllegalActionException("Cannot delete Todo as it is referenced elsewhere.");
        } catch (Exception e) {
            logger.error("Error deleting Todo with ID: {} for user: {}", todoId, user.getId(), e);
            throw new TodoServiceException("Error deleting Todo", e);
        }
    }

    // update todo
    @Transactional
    public Todo updateTodoForUser(Integer todoId, TodoRequest todoRequest, User user) {
        Todo todo = todoRepository.findById(todoId)
                .filter(todo1 -> todo1.getUser().getId().equals(user.getId())) // Ensures user can only update their own Todo
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with ID: " + todoId + " for user with ID: " + user.getId()));
        try {
            logger.info("Updating Todo with ID: {} for user: {}", todoId, user.getId());
            todo.setTitle(todoRequest.getTitle());
            todo.setDescription(todoRequest.getDescription());
            todo.setPriority(todoRequest.getPriority());
            todo.setCompleted(todoRequest.isCompleted());
            todo.setDueDate(todoRequest.getDueDate());
            System.out.println(todo);
            return todoRepository.save(todo);
        } catch (DataIntegrityViolationException e) {
            logger.error("Failed to update Todo: A Todo with similar details already exists for user: {}", user.getId(), e);
            throw new ResourceAlreadyExistsException("A Todo with similar details already exists.");
        } catch (Exception e) {
            logger.error("Error updating Todo with ID: {} for user: {}", todoId, user.getId(), e);
            throw new TodoServiceException("Error updating Todo", e);
        }
    }

}
