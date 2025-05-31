package pl.dev4lazy.ums.domain.model.user;

public record UserId(Long value) {

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId nie może być null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("UserId musi być dodatnie: " + value);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
