package pl.dev4lazy.ums.domain.model.user;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.dev4lazy.ums.utils.Messages;

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
        // stan początkowy: INACTIVE
        assertEquals(user.getStatus(), UserStatus.INACTIVE);

        // aktywacja
        user.activate();
        assertEquals(user.getStatus(), UserStatus.ACTIVE, "Po wywołaniu activate() status powinien być ACTIVE");
    }

    @Test(expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp = Messages.USER_IS_ACTIVE_ALREADY )
    public void whenActivatingAlreadyActiveUser_thenThrowsException() {
        // najpierw aktywujemy
        user.activate();
        assertEquals(user.getStatus(), UserStatus.ACTIVE);

        // próbujemy aktywować jeszcze raz → wyjątek
        user.activate();
    }

    @Test
    public void whenDeactivatingActiveUser_thenStatusBecomesInactive() {
        // ustawmy użytkownika na ACTIVE
        user.activate();
        assertEquals(user.getStatus(), UserStatus.ACTIVE);

        // dezaktywacja
        user.deactivate();
        assertEquals(user.getStatus(), UserStatus.INACTIVE, "Po wywołaniu deactivate() status powinien być INACTIVE");
    }

    @Test(expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp = Messages.USER_IS_INACTIVE_ALREADY)
    public void whenDeactivatingAlreadyInactiveUser_thenThrowsException() {
        // początkowo user jest INACTIVE
        assertEquals(user.getStatus(), UserStatus.INACTIVE);

        // próbujemy ponownie zdeaktywować → wyjątek
        user.deactivate();
    }
}
