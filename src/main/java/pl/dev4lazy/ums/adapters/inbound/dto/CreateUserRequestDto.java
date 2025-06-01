package pl.dev4lazy.ums.adapters.inbound.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import pl.dev4lazy.ums.utils.Messages;

public record CreateUserRequestDto(
        @NotBlank( message = Messages.FIRST_NAME_EMPTY ) String firstName,
        @NotBlank( message = Messages.LAST_NAME_EMPTY ) String lastName,
        @NotBlank( message = Messages.EMAIL_EMPTY )
        @Email( message = Messages.INCORRECT_EMAIL_FORMAT ) String email
) { }
