package pl.dev4lazy.ums.adapters.outbound.persistence.entity;

import jakarta.persistence.*;  // (albo javax.persistence w starszych wersjach)
import pl.dev4lazy.ums.domain.model.user.UserStatus;
import lombok.Data;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

}

