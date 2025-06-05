package pl.dev4lazy.ums.adapters.inbound.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import pl.dev4lazy.ums.utils.Messages;

public record CreateUserRequestDto(
        @NotBlank( message = Messages.FIRST_NAME_EMPTY )
        @Size(min=2, max=50, message = Messages.FIRST_NAME_LENGTH_NOT_VALID)
        String firstName,

        @NotBlank( message = Messages.LAST_NAME_EMPTY )
        @Size(min=2, max=50, message = Messages.LAST_NAME_LENGTH_NOT_VALID)
        String lastName,

        @NotBlank( message = Messages.EMAIL_EMPTY )
        @Email( message = Messages.INCORRECT_EMAIL_FORMAT )
        String email
) { }
