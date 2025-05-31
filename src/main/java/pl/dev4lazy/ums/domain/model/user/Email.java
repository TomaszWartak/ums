package pl.dev4lazy.ums.domain.model.user;

public record Email(String value) {
    public Email {
        if (value == null || !value.matches("^.+@.+\\..+$")) {
            throw new IllegalArgumentException("Nieprawidłowy format e-maila");
        }
    }
}
