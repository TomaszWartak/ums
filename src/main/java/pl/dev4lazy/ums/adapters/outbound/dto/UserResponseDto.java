package pl.dev4lazy.ums.adapters.outbound.dto;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String status
) { }
