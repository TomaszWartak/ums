package pl.dev4lazy.ums.mock;

import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.model.user.UserId;
import pl.dev4lazy.ums.domain.repository.UserRepository;

import java.util.*;

public class UserRepositoryMockAdapter implements UserRepository {

    private final Map<Long, User> storage = new HashMap<>();

    @Override
    public User save( User user ) {
        if (user.getId() == null) {
            // Generujemy ID (np. autoinkrementacja)
            UserId newUserId = new UserId( storage.size() + 1L);
            // Zakładamy, że User ma konstruktor lub builder z id
            user = User.create( user.getName(), user.getEmail() ); // dopasuj do swojej klasy User
            user.setId( newUserId );
        }
        storage.put( user.getId().getValue(), user);
        return user;
    }

    @Override
    public Optional<User> findByUserId(UserId id ) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id.getValue()));
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable( storage.get( id ) );
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return storage.values().stream()
                .filter(user -> user.getEmail().emailValue().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) {
            return false;
        }
        return storage
                .values()
                .stream()
                .anyMatch(user -> user.getEmail().emailValue().equals(email) );
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
