package pl.dev4lazy.ums.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.dev4lazy.ums.adapters.outbound.persistence.UserRepositoryAdapter;
import pl.dev4lazy.ums.application.service.UserActivationService;
import pl.dev4lazy.ums.application.service.UserCreationService;
import pl.dev4lazy.ums.application.service.UserDeactivationService;
import pl.dev4lazy.ums.domain.service.UserNotFoundException;
import pl.dev4lazy.ums.domain.model.user.UserStatus;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.model.user.UserId;

import java.util.Optional;

import static org.testng.Assert.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserDeactivationServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserCreationService userCreationService;

    @Autowired
    private UserActivationService userActivationService;

    @Autowired
    private UserDeactivationService userDeactivationService;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @BeforeMethod
    public void cleanDatabase() {
        userRepositoryAdapter.deleteAll();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecute_NullId_ThrowsIllegalArgumentException() {
        userDeactivationService.execute(null);
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void testExecute_NonExistingUser_ThrowsUserNotFoundException() {
        Long missingId = 12345L;
        userDeactivationService.execute(missingId);
    }

    @Test
    public void testExecute_ExistingActiveUser_StatusBecomesInactive() {
        Long userId = userCreationService.execute("Tomek", "Kowalski", "tomek.k@example.com");

        userActivationService.execute(userId);

        Optional<User> before = userRepositoryAdapter.findByUserId(new UserId(userId));
        assertTrue(before.isPresent());
        assertEquals(before.get().getStatus(), UserStatus.ACTIVE,
                "Przed dezaktywacją status powinien być ACTIVE");

        userDeactivationService.execute(userId);

        Optional<User> after = userRepositoryAdapter.findByUserId(new UserId(userId));
        assertTrue(after.isPresent());
        assertEquals(after.get().getStatus(), UserStatus.INACTIVE,
                "Po dezaktywacji status powinien być INACTIVE");
    }

    @Test
    public void testExecute_ExistingInactiveUser_SaveStillCalledButStatusRemainsInactive() {
        Long userId = userCreationService.execute("Ola", "Nowak", "ola.n@example.com");

        Optional<User> initial = userRepositoryAdapter.findByUserId(new UserId(userId));
        assertTrue(initial.isPresent());
        assertEquals(initial.get().getStatus(), UserStatus.INACTIVE,
                "Początkowy status powinien być INACTIVE");

        userDeactivationService.execute(userId);

        Optional<User> after = userRepositoryAdapter.findByUserId(new UserId(userId));
        assertTrue(after.isPresent());
        assertEquals(after.get().getStatus(), UserStatus.INACTIVE,
                "Status po ponownej dezaktywacji powinien pozostać INACTIVE");
    }
}
