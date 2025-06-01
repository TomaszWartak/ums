package pl.dev4lazy.ums.domain.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.dev4lazy.ums.utils.Messages;

import java.util.Objects;

@Setter
@Getter
@Builder
public class User {
    private UserId id;
    private PersonalName name;
    private Email email;
    private UserStatus status;

    private User( UserId id, PersonalName name, Email email, UserStatus status ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
    }

    private User( PersonalName name, Email email ) {
        this(null, name, email, UserStatus.INACTIVE);
    }

    public static User create( PersonalName name, Email email ) {
        Objects.requireNonNull(name, Messages.NAME_NULL );
        Objects.requireNonNull(email, Messages.EMAIL_NULL );
        return new User(name, email);
    }

    public void activate() {
        /* TODO czy to ma sens?
        if (status == UserStatus.ACTIVE) {
            throw new IllegalStateException( Messages.USER_IS_ACTIVE_ALREADY);
        }
         */
        this.status = UserStatus.ACTIVE;
    }

    public void deactivate() {
        /* TODO czy to ma sens?
        if (status == UserStatus.INACTIVE) {
            throw new IllegalStateException( Messages.USER_IS_INACTIVE_ALREADY );
        }
        */
        this.status = UserStatus.INACTIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User other = (User) o;
        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name=" + name +
                ", email=" + email +
                ", status=" + status +
                '}';
    }
}