package pl.dev4lazy.ums.adapters.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dev4lazy.ums.adapters.outbound.persistence.entity.UserEntity;

@Repository
public interface SpringDataUserJPA extends JpaRepository<UserEntity, Long> {
    // todo tutaj możesz dodać metody typu: boolean existsByEmail(String email);
}