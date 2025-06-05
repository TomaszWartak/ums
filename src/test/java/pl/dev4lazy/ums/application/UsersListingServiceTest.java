package pl.dev4lazy.ums.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.dev4lazy.ums.adapters.outbound.persistence.UserRepositoryAdapter;
import pl.dev4lazy.ums.adapters.outbound.dto.UserResponseDto;
import pl.dev4lazy.ums.application.service.UsersListingService;
import pl.dev4lazy.ums.application.service.UserCreationService;
import pl.dev4lazy.ums.domain.model.user.UserStatus;

import java.util.List;

import static org.testng.Assert.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UsersListingServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserCreationService userCreationService;

    @Autowired
    private UsersListingService usersListingService;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @BeforeMethod
    public void cleanDatabase() {
        userRepositoryAdapter.deleteAll();
    }

    @Test
    public void testListAll_EmptyDatabase_ReturnsEmptyList() {
        List<UserResponseDto> result = usersListingService.listAll();
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Oczekiwano pustej listy użytkowników");
    }

    @Test
    public void testListAll_WithExistingUsers_ReturnsMappedDtos() {
        userCreationService.execute("Jan", "Kowalski", "jan.kowalski@example.com");
        userCreationService.execute("Anna", "Nowak", "anna.nowak@example.com");

        List<UserResponseDto> result = usersListingService.listAll();

        assertNotNull(result);
        assertEquals(result.size(), 2, "Powinny być dwa elementy w liście");

        UserResponseDto dto1 = result.getFirst();
        assertEquals(dto1.firstName(), "Jan");
        assertEquals(dto1.lastName(), "Kowalski");
        assertEquals(dto1.email(), "jan.kowalski@example.com");
        assertEquals(dto1.status(), UserStatus.INACTIVE.name());

        UserResponseDto dto2 = result.get(1);
        assertEquals(dto2.firstName(), "Anna");
        assertEquals(dto2.lastName(), "Nowak");
        assertEquals(dto2.email(), "anna.nowak@example.com");
        assertEquals(dto2.status(), UserStatus.INACTIVE.name());
    }
}
