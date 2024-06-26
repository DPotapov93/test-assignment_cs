package org.example.testassignmentcs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserCreateRequestDto {
    @NotNull
    @Email(message = "Must be valid")
    private String email;
    @NotNull
    @Size(min = 1, max = 25)
    private String firstName;
    @NotNull
    @Size(min = 1, max = 25)
    private String lastName;
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    @Size(max = 100, message = "Address should not exceed 100 characters")
    private String address;
    @Size(max = 20, message = "Phone number should not exceed 20 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Phone number should contain only digits")
    private String phoneNumber;
}
