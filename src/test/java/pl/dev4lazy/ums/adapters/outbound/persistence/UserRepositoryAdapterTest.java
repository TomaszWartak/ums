package pl.dev4lazy.ums.adapters.outbound.persistence;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.dev4lazy.ums.domain.model.user.Email;
import pl.dev4lazy.ums.domain.model.user.PersonalName;
import pl.dev4lazy.ums.domain.model.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import pl.dev4lazy.ums.utils.Messages;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryAdapterTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;


    @BeforeMethod
    public void setup() {
        userRepositoryAdapter.deleteAll();
    }

    @Test
    public void testSave_NewUserWithProperData_AssignsIdAndStores() {
        User user = User.create(
                new PersonalName("Jan", "Kowalski"),
                new Email("jan.kowalski@example.com")
        );
        assertNull(user.getId());

        User savedUser = userRepositoryAdapter.save(user);

        assertNotNull( savedUser.getId(), Messages.USER_ID_NULL );
        assertEquals( savedUser.getName().getValue(), "Jan Kowalski");
        assertEquals( savedUser.getEmail().getValue(), "jan.kowalski@example.com");

        Optional<User> found = userRepositoryAdapter.findByUserId(savedUser.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get(), savedUser);
    }

    @Test
    public void testSave_ExistingUser_UpdatesStorage() {
        User user = User.create(
                new PersonalName("Anna", "Nowak"),
                new Email("anna.nowak@example.com")
        );

        User savedUser = userRepositoryAdapter.save(user);

        savedUser.setName( new PersonalName("Anna", "K.") );
        savedUser.setEmail( new Email("anna.k@example.com") );

        User updatedUser = userRepositoryAdapter.save(savedUser);
        assertEquals( updatedUser.getName().getValue(), "Anna K.");
        assertEquals( updatedUser.getEmail().getValue(), "anna.k@example.com" );

        Optional<User> found = userRepositoryAdapter.findByUserId( updatedUser.getId() );
        assertTrue( found.isPresent() );
        assertEquals( found.get().getName().getValue(), "Anna K." );
    }


    @Test
    public void testFindById_ExistingUserId_ReturnsProperUserData() {
        User newUser = User.create(
                new PersonalName("Anna", "Nowak"),
                new Email("anna.nowak@example.com")
        );

        User savedUser = userRepositoryAdapter.save( newUser );

        Optional<User> user = userRepositoryAdapter.findByUserId( savedUser.getId() );
        assertTrue(user.isPresent());
    }

    @Test
    public void testFindById_NonExistingUserId_ReturnsEmpty() {
        Optional<User> user = userRepositoryAdapter.findById( 999L );
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

        user1 = userRepositoryAdapter.save(user1);
        user2 = userRepositoryAdapter.save(user2);

        List<User> allUsers = userRepositoryAdapter.findAll();
        assertEquals(allUsers.size(), 2);
        assertTrue(allUsers.contains(user1));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void testFindMaxId_Returns0WhenNoSavedUsers() {
        Long maxId = userRepositoryAdapter.findMaxId();
        assertEquals( maxId, 0);
    }

    @Test
    public void testFindMaxId_ReturnsMoreAbout2After2SavedUsers() {
        User user1 = User.create(
                new PersonalName("User", "1"),
                new Email("user1@example.com")
        );

        User user2 = User.create(
                new PersonalName("User", "2"),
                new Email("user2@example.com")
        );


        User user3 = User.create(
                new PersonalName("User", "3"),
                new Email("user3@example.com")
        );

        userRepositoryAdapter.save(user1);

        Long maxIdBeforeSaving = userRepositoryAdapter.findMaxId();

        userRepositoryAdapter.save(user2);
        userRepositoryAdapter.save(user3);

        Long maxIdAfterSaving = userRepositoryAdapter.findMaxId();
        assertEquals( maxIdAfterSaving, maxIdBeforeSaving+2);
    }

}
