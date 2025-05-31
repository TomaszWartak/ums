package pl.dev4lazy.ums.adapters.outbound.persistence;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pl.dev4lazy.ums.adapters.outbound.persistence.mapper.UserEntityMapper;
import pl.dev4lazy.ums.domain.model.user.Email;
import pl.dev4lazy.ums.domain.model.user.PersonalName;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.repository.AbstractUserRepositoryTest;
import pl.dev4lazy.ums.domain.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryAdapterTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SpringDataUserJpa springDataUserJpa;

    @Autowired
    private UserEntityMapper userEntityMapper;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    UserRepository repository;

    @BeforeMethod
    public void setup() {
        repository = userRepositoryAdapter;
        repository.deleteAll();
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
