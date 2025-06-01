package pl.dev4lazy.ums.adapters.outbound.persistence;

import pl.dev4lazy.ums.adapters.outbound.persistence.entity.UserEntity;
import pl.dev4lazy.ums.adapters.outbound.persistence.mapper.UserEntityMapper;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.model.user.UserId;
import pl.dev4lazy.ums.domain.repository.UserRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserRepositoryAdapter implements UserRepository {
    private final SpringDataUserJpa springDataUserJpa;
    private final UserEntityMapper userEntityMapper;

    public UserRepositoryAdapter( SpringDataUserJpa springDataUserJpa,
                                 UserEntityMapper userEntityMapper) {
        this.springDataUserJpa = springDataUserJpa;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = userEntityMapper.toEntity( user );
        UserEntity savedEntity = springDataUserJpa.save(entity);
        return userEntityMapper.toDomain( savedEntity );
    }
    @Override
    public Optional<User> findByUserId(UserId id) {
        return findById( id.getValue() );
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataUserJpa
                .findById( id )
                .map(userEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserJpa
                .findByEmail( email )
                .map(userEntityMapper::toDomain);    }

    @Override
    public List<User> findAll() {
        return springDataUserJpa.findAll()
                .stream()
                .map(userEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Long findMaxId() {
        Optional<UserEntity> userWithMaxId = springDataUserJpa.findTopByOrderByIdDesc();
        Long maxIdWrapped = 0L;
        if (userWithMaxId.isPresent()) {
            maxIdWrapped = userWithMaxId.get().getId();
        }
        return maxIdWrapped;
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserJpa.findByEmail( email ).isPresent();
    }

    @Override
    public void deleteAll() {
        springDataUserJpa.deleteAll();
    }
}
