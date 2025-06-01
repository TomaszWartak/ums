package pl.dev4lazy.ums.domain.model.user;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class UserCreationTest {

    @Test
    public void whenRegisteringUser_thenFieldsAreSetAndStatusIsInactive() {
        PersonalName name = new PersonalName("Anna", "Nowak");
        Email email = new Email("anna.nowak@example.com");

        User user = User.create(name, email);

        assertNull(user.getId(), "Id powinno być null przed zapisaniem w repozytorium");
        assertEquals(user.getName(), name, "Imię i nazwisko powinny być takie, jak podane w VO");
        assertEquals(user.getEmail(), email, "E-mail powinien być taki, jak podany w VO");
        assertEquals(user.getStatus(), UserStatus.INACTIVE, "Nowo zarejestrowany użytkownik powinien być domyślnie INACTIVE");
    }

}
