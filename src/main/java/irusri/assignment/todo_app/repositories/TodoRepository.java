package irusri.assignment.todo_app.repositories;

import irusri.assignment.todo_app.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer> {


    Page<Todo> findByUserId(Integer userId, Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.user.id = :userId AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Todo> searchByKeyword(Integer userId, String keyword, Pageable pageable);

    // Sort and filter Todos by due date, priority, or other fields
    Page<Todo> findByUserIdOrderByDueDateAsc(Integer userId, Pageable pageable);

    Page<Todo> findByUserIdOrderByPriorityAsc(Integer userId, Pageable pageable);

    Page<Todo> findByUserIdAndIsCompleted(Integer userId, boolean isCompleted, Pageable pageable);

    // Retrieve Todo items for a user based on due date
    Page<Todo> findByUserIdAndDueDate(Integer userId, LocalDate dueDate, Pageable pageable);


}
