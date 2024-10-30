package irusri.assignment.todo_app.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "firstname is required")
    private String firstName;

    @NotBlank(message = "lastname is required")
    private String lastName;
}