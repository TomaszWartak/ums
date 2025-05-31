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
    private final UserEntityMapper mapper;

    public UserRepositoryAdapter( SpringDataUserJpa springDataUserJpa,
                                 UserEntityMapper mapper ) {
        this.springDataUserJpa = springDataUserJpa;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity( user );
        UserEntity savedEntity = springDataUserJpa.save(entity);
        return mapper.toDomain( savedEntity );
    }
    @Override
    public Optional<User> findByUserId(UserId id) {
        return findById( id.getValue() );
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataUserJpa
                .findById( id )
                .map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return springDataUserJpa.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        springDataUserJpa.deleteAll();
    }
}
