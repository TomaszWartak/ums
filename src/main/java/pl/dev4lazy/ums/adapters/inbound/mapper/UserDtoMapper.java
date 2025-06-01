package pl.dev4lazy.ums.adapters.inbound.mapper;

import org.springframework.stereotype.Component;
import pl.dev4lazy.ums.adapters.outbound.dto.UserResponseDto;
import pl.dev4lazy.ums.domain.model.user.User;

@Component
public class UserDtoMapper {

    public UserResponseDto userToDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponseDto(
                user.getId().getValue(),
                user.getName().firstName(),
                user.getName().lastName(),
                user.getEmail().getValue(),
                user.getStatus().name()
        );
    }
}