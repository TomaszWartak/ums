package pl.dev4lazy.ums.adapters.outbound.persistence.mapper;

import pl.dev4lazy.ums.adapters.outbound.persistence.entity.UserEntity;
import pl.dev4lazy.ums.domain.model.user.Email;
import pl.dev4lazy.ums.domain.model.user.PersonalName;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.model.user.UserId;
import pl.dev4lazy.ums.domain.model.user.UserStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper odpowiedzialny za konwersję między UserEntity (JPA) a User (domena).
 */
@Component
public class UserEntityMapper {

    /**
     * Mapuje czystą encję domenową User → JPA‐ową encję UserEntity.
     */
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        // Jeżeli domenowy User ma już nadane ID, przekazujemy je do encji;
        // w przeciwnym razie zostawiamy entity.id = null, żeby JPA wygenerowało nowe.
        if (user.getId() != null) {
            entity.setId( user.getId().getValue() );
        }

        // VO PersonalName rozbijamy na pola firstName/lastName
        PersonalName name = user.getName();
        entity.setFirstName( name.firstName());
        entity.setLastName( name.lastName());

        // VO Email → proste String
        entity.setEmail( user.getEmail().getValue() );

        // Status (enum) mapujemy bezpośrednio
        entity.setStatus(user.getStatus());

        return entity;
    }

    /**
     * Mapuje JPA‐ową encję UserEntity → czystą encję domenową User.
     *
     * Ponieważ domenowe User.create(...) zawsze ustawia status na INACTIVE
     * i id == null, musimy po utworzeniu obiektu w pamięci:
     * 1) nadać mu UserId
     * 2) ustawić odpowiedni status (jeśli w DB było ACTIVE, wywołujemy activate()).
     */
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        // 1) VO: PersonalName i Email
        PersonalName name = new PersonalName(entity.getFirstName(), entity.getLastName());
        Email email = new Email(entity.getEmail());

        // 2) Tworzymy nowy obiekt domenowy (status domyślnie INACTIVE, id == null)
        User user = User.create(name, email);

        // 3) Ustawiamy w domenie ID, które przyszło z bazy
        user.setId(new UserId(entity.getId()));

        // 4) Ustawiamy status: jeżeli w DB było ACTIVE, wywołujemy activate()
        if (entity.getStatus() == UserStatus.ACTIVE) {
            user.activate();
        }
        // Jeśli w DB jest INACTIVE, nic nie robimy, bo create() już ustawiło status = INACTIVE.

        return user;
    }
}