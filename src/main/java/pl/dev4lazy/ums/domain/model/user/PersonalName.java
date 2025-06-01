package pl.dev4lazy.ums.domain.model.user;

import jakarta.validation.constraints.NotNull;
import pl.dev4lazy.ums.utils.Messages;

import java.util.Objects;

public record PersonalName(String firstName, String lastName) {

    public PersonalName {
        if (firstName == null || firstName.isBlank() ||
                lastName  == null || lastName.isBlank()) {
            throw new IllegalArgumentException( Messages.NAME_EMPTY );
        }
    }

    public String getValue() {
        return firstName + ' ' +lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PersonalName that)) return false;
        return Objects.equals(lastName, that.lastName) && Objects.equals(firstName, that.firstName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

    @Override
    public String toString() {
        return firstName + ' ' +lastName;
    }
}
