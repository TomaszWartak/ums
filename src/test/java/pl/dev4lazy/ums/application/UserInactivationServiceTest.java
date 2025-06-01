package pl.dev4lazy.ums.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.dev4lazy.ums.adapters.outbound.persistence.UserRepositoryAdapter;
import pl.dev4lazy.ums.domain.service.UserNotFoundException;
import pl.dev4lazy.ums.domain.model.user.UserStatus;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.model.user.UserId;

import java.util.Optional;

import static org.testng.Assert.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserInactivationServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserCreationService userCreationService;

    @Autowired
    private UserActivationService userActivationService;

    @Autowired
    private UserInactivationService userInactivationService;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @BeforeMethod
    public void cleanDatabase() {
        userRepositoryAdapter.deleteAll();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInactivate_NullId_ThrowsIllegalArgumentException() {
        userInactivationService.inactivate(null);
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void testInactivate_NonExistingUser_ThrowsUserNotFoundException() {
        Long missingId = 12345L;
        userInactivationService.inactivate(missingId);
    }

    @Test
    public void testInactivate_ExistingActiveUser_StatusBecomesInactive() {
        // 1. Utwórz użytkownika (domyślnie INACTIVE)
        Long userId = userCreationService.create("Tomek", "Kowalski", "tomek.k@example.com");

        // 2. Aktywuj użytkownika, żeby mieć stan ACTIVE
        userActivationService.activate(userId);

        // 3. Sprawdź status w bazie - powinien być ACTIVE
        Optional<User> before = userRepositoryAdapter.findByUserId(new UserId(userId));
        assertTrue(before.isPresent());
        assertEquals(before.get().getStatus(), UserStatus.ACTIVE,
                "Przed dezaktywacją status powinien być ACTIVE");

        // 4. Dezaktywuj użytkownika
        userInactivationService.inactivate(userId);

        // 5. Pobierz ponownie z bazy i zweryfikuj status INACTIVE
        Optional<User> after = userRepositoryAdapter.findByUserId(new UserId(userId));
        assertTrue(after.isPresent());
        assertEquals(after.get().getStatus(), UserStatus.INACTIVE,
                "Po dezaktywacji status powinien być INACTIVE");
    }

    @Test
    public void testInactivate_ExistingInactiveUser_SaveStillCalledButStatusRemainsInactive() {
        // 1. Utwórz użytkownika (domyślnie INACTIVE)
        Long userId = userCreationService.create("Ola", "Nowak", "ola.n@example.com");

        // 2. Sprawdź, że początkowo status jest INACTIVE
        Optional<User> initial = userRepositoryAdapter.findByUserId(new UserId(userId));
        assertTrue(initial.isPresent());
        assertEquals(initial.get().getStatus(), UserStatus.INACTIVE,
                "Początkowy status powinien być INACTIVE");

        // 3. Wywołaj inactivate ponownie (już jest INACTIVE)
        userInactivationService.inactivate(userId);

        // 4. Sprawdź, że status wciąż jest INACTIVE
        Optional<User> after = userRepositoryAdapter.findByUserId(new UserId(userId));
        assertTrue(after.isPresent());
        assertEquals(after.get().getStatus(), UserStatus.INACTIVE,
                "Status po ponownej dezaktywacji powinien pozostać INACTIVE");
    }
}
