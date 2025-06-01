package pl.dev4lazy.ums.adapters.inbound.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.dev4lazy.ums.adapters.inbound.dto.CreateUserRequestDto;
import pl.dev4lazy.ums.adapters.outbound.dto.UserResponseDto;
import pl.dev4lazy.ums.application.ListUsersService;
import pl.dev4lazy.ums.application.UserCreationService;
import pl.dev4lazy.ums.utils.Messages;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final UserCreationService userCreationService;
    private final ListUsersService listUsersService;

    public UserController( UserCreationService userCreationService, ListUsersService listUsersService) {
        this.userCreationService = userCreationService;
        this.listUsersService = listUsersService;
    }

    @GetMapping("/")
    public Map<String,String> home() {
        return Map.of( Messages.STATUS, Messages.OK );
    }

    @PostMapping("/api/users")
    public ResponseEntity<Map<String, Long>> createUser( @Valid @RequestBody CreateUserRequestDto dto) {
        Long newId = userCreationService.create( dto.firstName(), dto.lastName(), dto.email());
        return ResponseEntity
                .status( HttpStatus.CREATED)
                .body( Map.of("id", newId) );
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = listUsersService.listAll();
        return ResponseEntity.ok(users);
    }

}