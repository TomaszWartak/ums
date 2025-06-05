package pl.dev4lazy.ums.application.service;

import org.springframework.stereotype.Service;
import pl.dev4lazy.ums.adapters.inbound.mapper.UserDtoMapper;
import pl.dev4lazy.ums.adapters.outbound.dto.UserResponseDto;
import pl.dev4lazy.ums.application.usecase.GetUserUseCase;
import pl.dev4lazy.ums.domain.repository.UserRepository;
import pl.dev4lazy.ums.domain.service.UserNotFoundException;
import pl.dev4lazy.ums.utils.Messages;

@Service
public class UserGettingService implements GetUserUseCase {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public UserGettingService(UserRepository userRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public UserResponseDto execute(Long id) {
        return userRepository.findById(id)
                .map(userDtoMapper::userToDto)
                .orElseThrow(() -> new UserNotFoundException(String.format(Messages.USER_NOT_FOUND, id)));
    }
}
