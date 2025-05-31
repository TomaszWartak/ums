package pl.dev4lazy.ums.domain.model.user;

public record PersonalName(String firstName, String lastName) {
    public PersonalName {
        if (firstName == null || firstName.isBlank() ||
                lastName  == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Imię i nazwisko nie mogą być puste");
        }
    }
}
