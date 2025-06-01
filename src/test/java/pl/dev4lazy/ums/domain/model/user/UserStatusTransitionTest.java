package pl.dev4lazy.ums.domain.model.user;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class UserStatusTransitionTest {

    private User user;

    @BeforeMethod
    public void setUp() {
        PersonalName name = new PersonalName("Jan", "Kowalski");
        Email email = new Email("jan.kowalski@example.com");
        user = User.create(name, email);
    }

    @Test
    public void whenActivatingInactiveUser_thenStatusBecomesActive() {
        assertEquals(user.getStatus(), UserStatus.INACTIVE);

        user.activate();
        assertEquals(user.getStatus(), UserStatus.ACTIVE, "Po wywołaniu activate() status powinien być ACTIVE");
    }

    @Test
    public void whenDeactivatingActiveUser_thenStatusBecomesInactive() {
        user.activate();
        assertEquals(user.getStatus(), UserStatus.ACTIVE);

        user.deactivate();
        assertEquals(user.getStatus(), UserStatus.INACTIVE, "Po wywołaniu deactivate() status powinien być INACTIVE");
    }

}
