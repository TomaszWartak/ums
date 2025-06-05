package pl.dev4lazy.ums.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import pl.dev4lazy.ums.adapters.outbound.persistence.UserRepositoryAdapter;
import pl.dev4lazy.ums.application.service.UserActivationService;
import pl.dev4lazy.ums.application.service.UserCreationService;
import pl.dev4lazy.ums.domain.model.user.UserStatus;
import pl.dev4lazy.ums.domain.service.UserNotFoundException;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.model.user.UserId;

import java.util.Optional;

import static org.testng.Assert.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserActivationServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserCreationService userCreationService;

    @Autowired
    private UserActivationService userActivationService;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter; // implementacja JPA dla UserRepository

    @BeforeMethod
    public void cleanDatabase() {
        userRepositoryAdapter.deleteAll();
    }

    @AfterMethod
    public void tearDown() {
        userRepositoryAdapter.deleteAll();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testActivate_NullId_ThrowsIllegalArgumentException() {
        userActivationService.execute(null);
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void testActivate_NonExistingUser_ThrowsUserNotFoundException() {
        Long nonExistingId = 999L;
        userActivationService.execute(nonExistingId);
    }

    @Test
    public void testActivate_ExistingUser_StatusBecomesActive() {
        Long newUserId = userCreationService.execute(
                "Marek", "Wójcik", "marek.wojcik@example.com"
        );

        Optional<User> maybeUserBefore = userRepositoryAdapter.findByUserId(new UserId(newUserId));
        assertTrue(maybeUserBefore.isPresent(), "Użytkownik powinien być w bazie przed aktywacją");
        assertEquals(maybeUserBefore.get().getStatus(), pl.dev4lazy.ums.domain.model.user.UserStatus.INACTIVE,
                "Status powinien być INACTIVE przed aktywacją");

        userActivationService.execute(newUserId);

        Optional<User> maybeUserAfter = userRepositoryAdapter.findByUserId(new UserId(newUserId));
        assertTrue(maybeUserAfter.isPresent(), "Użytkownik powinien być nadal w bazie po aktywacji");
        assertEquals(maybeUserAfter.get().getStatus(), pl.dev4lazy.ums.domain.model.user.UserStatus.ACTIVE,
                "Status powinien być ACTIVE po aktywacji");
    }

    @Test
    public void testActivate_ExistingActiveUser_SaveStillCalledButStatusRemainsActive() {
        // użytkownik domyślnie INACTIVE
        Long userId = userCreationService.execute("Ola", "Nowak", "ola.n@example.com");

        Optional<User> initial = userRepositoryAdapter.findByUserId(new UserId(userId));
        assertTrue(initial.isPresent());
        assertEquals(initial.get().getStatus(), UserStatus.INACTIVE,
                "Początkowy status powinien być INACTIVE");

        userActivationService.execute(userId);

        userActivationService.execute(userId);

        Optional<User> after = userRepositoryAdapter.findByUserId(new UserId(userId));
        assertTrue(after.isPresent());
        assertEquals(after.get().getStatus(), UserStatus.ACTIVE,
                "Status po ponownej aktywacji powinien pozostać ACTIVE");
    }
}
