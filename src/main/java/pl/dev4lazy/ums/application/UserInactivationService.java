package pl.dev4lazy.ums.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev4lazy.ums.domain.service.UserNotFoundException;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.model.user.UserId;
import pl.dev4lazy.ums.domain.repository.UserRepository;
import pl.dev4lazy.ums.utils.Messages;

@Service
public class UserInactivationService {

    private final UserRepository userRepository;

    public UserInactivationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Dezaktywuje (ustawia status na INACTIVE) użytkownika o podanym ID.
     * @param idValue identyfikator użytkownika (long)
     * @throws IllegalArgumentException gdy idValue jest null
     * @throws UserNotFoundException gdy nie ma użytkownika o tym ID w bazie
     */
    @Transactional
    public void inactivate(Long idValue) {
        if (idValue == null) {
            throw new IllegalArgumentException( Messages.ID_NULL );
        }

        UserId userId = new UserId(idValue);
        User user = userRepository
                .findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException( String.format(Messages.USER_NOT_FOUND, idValue ) ) );

        // Zakładamy, że w domenie User istnieje metoda deactivate(), która ustawia status na INACTIVE
        user.deactivate();

        userRepository.save(user);
    }
}
