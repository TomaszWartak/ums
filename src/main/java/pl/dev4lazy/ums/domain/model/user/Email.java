package pl.dev4lazy.ums.domain.model.user;

import java.util.Objects;

public record Email( String emailValue) {
    public Email {
        if (emailValue == null || !emailValue.matches("^.+@.+\\..+$")) {
            throw new IllegalArgumentException("Nieprawidłowy format e-maila");
        }
    }
    public String getValue() {
        return emailValue;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Email(String value))) return false;
        return Objects.equals(emailValue, value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(emailValue);
    }
}
