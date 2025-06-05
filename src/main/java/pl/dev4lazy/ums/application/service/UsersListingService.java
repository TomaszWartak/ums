package pl.dev4lazy.ums.application.service;

import org.springframework.stereotype.Service;
import pl.dev4lazy.ums.adapters.inbound.mapper.UserDtoMapper;
import pl.dev4lazy.ums.domain.repository.UserRepository;
import pl.dev4lazy.ums.adapters.outbound.dto.UserResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsersListingService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public UsersListingService(UserRepository userRepository ) {
        this.userRepository = userRepository;
        this.userDtoMapper = new UserDtoMapper();
    }

    public List<UserResponseDto> listAll() {
        return userRepository.findAll().stream()
                .map( userDtoMapper::userToDto )
                .collect( Collectors.toList() );
    }

}
