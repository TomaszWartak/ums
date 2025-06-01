package pl.dev4lazy.ums.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import pl.dev4lazy.ums.adapters.outbound.persistence.UserRepositoryAdapter;
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
        // Wywołanie z null powinno rzucić IllegalArgumentException
        userActivationService.activate(null);
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void testActivate_NonExistingUser_ThrowsUserNotFoundException() {
        Long nonExistingId = 999L;
        // Baza jest pusta, więc wywołanie activate na nieistniejącym ID wyrzuci UserNotFoundException
        userActivationService.activate(nonExistingId);
    }

    @Test
    public void testActivate_ExistingUser_StatusBecomesActive() {
        // 1. Utwórzmy użytkownika; domyślnie status INACTIVE
        Long newUserId = userCreationService.create(
                "Marek", "Wójcik", "marek.wojcik@example.com"
        );

        // Najpierw sprawdźmy, że w bazie faktycznie istnieje i że status = INACTIVE
        Optional<User> maybeUserBefore = userRepositoryAdapter.findByUserId(new UserId(newUserId));
        assertTrue(maybeUserBefore.isPresent(), "Użytkownik powinien być w bazie przed aktywacją");
        assertEquals(maybeUserBefore.get().getStatus(), pl.dev4lazy.ums.domain.model.user.UserStatus.INACTIVE,
                "Status powinien być INACTIVE przed aktywacją");

        // 2. Aktywujemy użytkownika
        userActivationService.activate(newUserId);

        // 3. Pobierzmy ponownie z bazy i sprawdźmy, czy status to ACTIVE
        Optional<User> maybeUserAfter = userRepositoryAdapter.findByUserId(new UserId(newUserId));
        assertTrue(maybeUserAfter.isPresent(), "Użytkownik powinien być nadal w bazie po aktywacji");
        assertEquals(maybeUserAfter.get().getStatus(), pl.dev4lazy.ums.domain.model.user.UserStatus.ACTIVE,
                "Status powinien być ACTIVE po aktywacji");
    }
}
