package irusri.assignment.todo_app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Todo {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private Integer priority;


    @Column(nullable = false)
    private boolean isCompleted;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
}

