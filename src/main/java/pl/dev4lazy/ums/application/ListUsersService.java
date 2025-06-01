package pl.dev4lazy.ums.application;

import org.springframework.stereotype.Service;
import pl.dev4lazy.ums.adapters.inbound.mapper.UserDtoMapper;
import pl.dev4lazy.ums.domain.repository.UserRepository;
import pl.dev4lazy.ums.adapters.outbound.dto.UserResponseDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serwis zwracający listę wszystkich użytkowników w postaci DTO.
 */
@Service
public class ListUsersService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public ListUsersService( UserRepository userRepository ) {
        this.userRepository = userRepository;
        this.userDtoMapper = new UserDtoMapper();
    }

    /**
     * Pobiera wszystkich użytkowników z bazy i mapuje na UserResponseDto.
     *
     * @return lista UserResponseDto
     */
    public List<UserResponseDto> listAll() {
        return userRepository.findAll().stream()
                .map( userDtoMapper::userToDto )
                .collect( Collectors.toList() );
    }

    /**
     * Pomocnicza metoda konwertująca encję domenową User na DTO UserResponseDto.
     */

}
