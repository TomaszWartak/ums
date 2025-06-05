package pl.dev4lazy.ums.application.usecase;

import pl.dev4lazy.ums.adapters.outbound.dto.UserResponseDto;

public interface GetUserUseCase {
    UserResponseDto execute(Long id);
}
