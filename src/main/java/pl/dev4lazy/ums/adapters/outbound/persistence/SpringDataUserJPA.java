package pl.dev4lazy.ums.adapters.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataUserJPA extends JpaRepository<UserEntity, Long> {
    // tutaj możesz dodać metody typu: boolean existsByEmail(String email);
}