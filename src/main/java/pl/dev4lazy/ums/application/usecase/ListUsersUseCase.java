package pl.dev4lazy.ums.application.usecase;

import pl.dev4lazy.ums.adapters.outbound.dto.UserResponseDto;

import java.util.List;

public interface ListUsersUseCase {
    List<UserResponseDto> execute();
}

