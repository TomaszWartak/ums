package pl.dev4lazy.ums.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev4lazy.ums.application.usecase.ActivateUserUseCase;
import pl.dev4lazy.ums.domain.service.UserNotFoundException;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.model.user.UserId;
import pl.dev4lazy.ums.domain.repository.UserRepository;
import pl.dev4lazy.ums.utils.Messages;

@Service
public class UserActivationService implements ActivateUserUseCase {

    private final UserRepository userRepository;

    public UserActivationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void execute(Long idValue) {
        if (idValue == null) {
            throw new IllegalArgumentException( Messages.ID_NULL );
        }

        UserId userId = new UserId(idValue);
        User user = userRepository
                .findByUserId( userId )
                .orElseThrow( () -> new UserNotFoundException( String.format( Messages.USER_NOT_FOUND, idValue ) ) );

        user.activate();

        userRepository.save(user);
    }
}
