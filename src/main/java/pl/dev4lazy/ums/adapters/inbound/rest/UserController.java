package pl.dev4lazy.ums.adapters.inbound.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dev4lazy.ums.adapters.inbound.dto.CreateUserRequestDto;
import pl.dev4lazy.ums.adapters.outbound.dto.UserResponseDto;
import pl.dev4lazy.ums.application.service.UsersListingService;
import pl.dev4lazy.ums.application.service.UserActivationService;
import pl.dev4lazy.ums.application.service.UserCreationService;
import pl.dev4lazy.ums.application.service.UserDeactivationService;
import pl.dev4lazy.ums.utils.Messages;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final UserCreationService userCreationService;
    private final UserActivationService userActivationService;
    private final UserDeactivationService userDeactivationService;
    private final UsersListingService usersListingService;

    public UserController(
            UserCreationService userCreationService,
            UsersListingService usersListingService,
            UserActivationService userActivationService,
            UserDeactivationService userDeactivationService) {
        this.userCreationService = userCreationService;
        this.usersListingService = usersListingService;
        this.userActivationService = userActivationService;
        this.userDeactivationService = userDeactivationService;
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
        List<UserResponseDto> users = usersListingService.listAll();
        return ResponseEntity.ok( users );
    }

    @PutMapping("/api/users/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userActivationService.activate( id );
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userDeactivationService.inactivate(id);
        return ResponseEntity.ok().build();
    }
}