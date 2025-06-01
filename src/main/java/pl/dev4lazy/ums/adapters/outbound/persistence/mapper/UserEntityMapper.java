package pl.dev4lazy.ums.adapters.outbound.persistence.mapper;

import pl.dev4lazy.ums.adapters.outbound.persistence.entity.UserEntity;
import pl.dev4lazy.ums.domain.model.user.Email;
import pl.dev4lazy.ums.domain.model.user.PersonalName;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.model.user.UserId;
import pl.dev4lazy.ums.domain.model.user.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        if (user.getId() != null) {
            entity.setId( user.getId().getValue() );
        }

        PersonalName name = user.getName();
        entity.setFirstName( name.firstName());
        entity.setLastName( name.lastName());

        entity.setEmail( user.getEmail().getValue() );

        entity.setStatus(user.getStatus());

        return entity;
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        PersonalName name = new PersonalName(entity.getFirstName(), entity.getLastName());
        Email email = new Email(entity.getEmail());

        User user = User.create(name, email);

        user.setId(new UserId(entity.getId()));

        if (entity.getStatus() == UserStatus.ACTIVE) {
            user.activate();
        }

        return user;
    }
}