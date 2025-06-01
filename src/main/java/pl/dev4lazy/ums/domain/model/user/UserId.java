package pl.dev4lazy.ums.domain.model.user;

import pl.dev4lazy.ums.utils.Messages;

import java.util.Objects;

public record UserId( Long userIdValue) {

    public UserId {
        if (userIdValue == null) {
            throw new IllegalArgumentException( Messages.USER_ID_NULL );
        }
        if (userIdValue <= 0) {
            throw new IllegalArgumentException( String.format( Messages.USER_ID_NEGATIVE, userIdValue) );
        }
    }

    public Long getValue() {
        return userIdValue;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserId(Long idValue))) return false;
        return Objects.equals(userIdValue, idValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userIdValue);
    }

    @Override
    public String toString() {
        return String.valueOf(userIdValue);
    }

}
