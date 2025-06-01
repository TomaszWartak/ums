package pl.dev4lazy.ums.adapters.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dev4lazy.ums.adapters.outbound.persistence.entity.UserEntity;

import java.util.Optional;

@Repository
public interface SpringDataUserJpa extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findTopByOrderByIdDesc();

}