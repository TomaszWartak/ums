package pl.dev4lazy.ums.adapters.inbound.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequestDto(
        @NotBlank(message = "firstName nie może być puste") String firstName,
        @NotBlank(message = "lastName nie może być puste") String lastName,
        @NotBlank(message = "email nie może być pusty")
        @Email(message = "Nieprawidłowy format e-maila") String email
) { }
