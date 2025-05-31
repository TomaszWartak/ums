package pl.dev4lazy.ums.domain.repository;

import pl.dev4lazy.ums.domain.model.user.Email;
import pl.dev4lazy.ums.domain.model.user.PersonalName;
import pl.dev4lazy.ums.domain.model.user.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

public abstract class AbstractUserRepositoryTest {

    protected UserRepository repository;

    protected abstract UserRepository createRepository();

    @BeforeMethod
    public void setup() {
        repository = createRepository();
    }

    @Test
    public void testSave_NewUserWithProperData_AssignsIdAndStores() {
        User user = User.create(
                new PersonalName("Jan", "Kowalski"),
                new Email("jan.kowalski@example.com")
        );
        // Zakładamy, że user ma metodę getId() która zwraca null przed ustawieniem
        assertNull(user.getId());

        User savedUser = repository.save(user);

        assertNotNull(savedUser.getId(), "ID powinno być ustawione");
        assertEquals(savedUser.getName().getValue(), "Jan Kowalski");
        assertEquals(savedUser.getEmail().getValue(), "jan.kowalski@example.com");

        Optional<User> found = repository.findByUserId(savedUser.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get(), savedUser);
    }

    @Test
    public void testSave_ExistingUser_UpdatesStorage() {
        User user = User.create(
                new PersonalName("Anna", "Nowak"),
                new Email("anna.nowak@example.com")
        );

        User savedUser = repository.save(user);

        // Zmiana danych użytkownika
        savedUser.setName( new PersonalName("Anna", "K.") );
        savedUser.setEmail( new Email("anna.k@example.com") );

        User updatedUser = repository.save(savedUser);
        assertEquals( updatedUser.getName().getValue(), "Anna K.");
        assertEquals( updatedUser.getEmail().getValue(), "anna.k@example.com" );

        Optional<User> found = repository.findByUserId( updatedUser.getId() );
        assertTrue( found.isPresent() );
        assertEquals( found.get().getName().getValue(), "Anna K." );
    }


    @Test
    public void testFindById_ExistingUserId_ReturnsProperUserData() {
        User newUser = User.create(
                new PersonalName("Anna", "Nowak"),
                new Email("anna.nowak@example.com")
        );

        User savedUser = repository.save( newUser );

        Optional<User> user = repository.findByUserId( savedUser.getId() );
        assertTrue(user.isPresent());
    }

    @Test
    public void testFindById_NonExistingUserId_ReturnsEmpty() {
        Optional<User> user = repository.findById( 999L );
        assertFalse(user.isPresent());
    }

    @Test
    public void testFindById_NullUserId_ReturnsEmpty() {
        Optional<User> user = repository.findById(null);
        assertFalse(user.isPresent());
    }

    @Test
    public void testFindAll_ReturnsAllSavedUsers() {
        User user1 = User.create(
                new PersonalName("User", "1"),
                new Email("user1@example.com")
        );

        User user2 = User.create(
                new PersonalName("User", "2"),
                new Email("user2@example.com")
        );

        user1 = repository.save(user1);
        user2 = repository.save(user2);

        List<User> allUsers = repository.findAll();
        assertEquals(allUsers.size(), 2);
        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }
}
