package pl.dev4lazy.ums.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev4lazy.ums.domain.model.user.Email;
import pl.dev4lazy.ums.domain.model.user.PersonalName;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.repository.UserRepository;
import pl.dev4lazy.ums.domain.service.EmailAlreadyExistsException;

@Service
public class UserCreationService {
    private final UserRepository userRepository;

    public UserCreationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Long create(String firstName, String lastName, String emailStr) {
        PersonalName name = new PersonalName(firstName, lastName);
        if (userRepository.existsByEmail( emailStr )) {
            throw new EmailAlreadyExistsException("Użytkownik o e-mailu " + emailStr + " już istnieje.");
        }
        Email email = new Email(emailStr);
        User newUser = User.create(name, email);

        User savedUser = userRepository.save(newUser);
        return savedUser.getId().getValue();
    }

}