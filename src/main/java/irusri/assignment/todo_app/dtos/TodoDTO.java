package irusri.assignment.todo_app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoDTO {
    private Integer id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer priority;
    private boolean completed;
}
