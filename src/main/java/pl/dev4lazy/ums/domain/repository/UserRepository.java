package pl.dev4lazy.ums.domain.repository;

import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.model.user.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save( User user );
    Optional<User> findByUserId(UserId id );
    Optional<User> findById(Long id );
    Optional<User> findByEmail(String email);
    List<User> findAll();
    Long findMaxId();
    boolean existsByEmail(String email);
    void deleteAll();
}