package pl.dev4lazy.ums.domain.service;

import pl.dev4lazy.ums.domain.model.user.Email;
import pl.dev4lazy.ums.domain.model.user.PersonalName;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.repository.UserRepository;

public class UserCreationService {
    private final UserRepository userRepository;

    public UserCreationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long create(String firstName, String lastName, String emailStr) {
        PersonalName name = new PersonalName(firstName, lastName);
        Email email = new Email(emailStr);
        User newUser = User.create(name, email);

        User savedUser = userRepository.save(newUser);
        return savedUser.getId().getValue();
    }

}