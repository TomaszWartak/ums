package pl.dev4lazy.ums.domain.model.user;

import java.util.Objects;

public record UserId( Long userIdValue) {

    public UserId {
        if (userIdValue == null) {
            throw new IllegalArgumentException("UserId nie może być null");
        }
        if (userIdValue <= 0) {
            throw new IllegalArgumentException("UserId musi być dodatnie: " + userIdValue);
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
