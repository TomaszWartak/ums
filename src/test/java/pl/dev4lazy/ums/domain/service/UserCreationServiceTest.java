package pl.dev4lazy.ums.domain.service;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.dev4lazy.ums.application.UserCreationService;
import pl.dev4lazy.ums.domain.model.user.Email;
import pl.dev4lazy.ums.domain.model.user.PersonalName;
import pl.dev4lazy.ums.domain.model.user.User;
import pl.dev4lazy.ums.domain.repository.UserRepository;
import pl.dev4lazy.ums.mock.UserRepositoryMockAdapter;

import static org.testng.Assert.*;
import static pl.dev4lazy.ums.domain.model.user.UserStatus.INACTIVE;

public class UserCreationServiceTest {

    private UserRepository userRepository;
    private UserCreationService userCreationService;

    @BeforeMethod
    public void setUp() {
        userRepository = new UserRepositoryMockAdapter();
        userRepository.deleteAll();
        userCreationService = new UserCreationService( userRepository );
    }

    @Test
    public void testCreateUser_WithProperData_Success() {
        // --- Przygotowanie danych wejściowych ---
        String firstName = "Jan";
        String lastName  = "Kowalski";
        String emailStr  = "jan.kowalski@example.com";

        // --- Przygotowanie „zapisanej” encji zwracanej przez mockowane repo ---
        User newUser = User.create(
                new PersonalName( firstName, lastName ),
                new Email( emailStr )
        );

        // --- Wywołanie metody serwisu ---
        Long returnedId = userCreationService.create( firstName, lastName, emailStr );

        // --- Weryfikacje zwróconego ID ---
        assertNotNull( returnedId, "Zwrócone ID nie może być null");
        assertEquals( returnedId.longValue(), 1L);

        // --- Pobranie zapisanego obiektu User
        User savedUser = userRepository
                .findById( 1L )
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        assertEquals( savedUser.getName(), new PersonalName(firstName, lastName) );
        assertEquals( savedUser.getEmail(), new Email(emailStr) );
        assertEquals( savedUser.getStatus(), INACTIVE );
    }

    @DataProvider(name = "invalidNames")
    public Object[][] invalidNames() {
        return new Object[][] {
                {null, "Kowalski"},
                {"", "Nowak"},
                {"   ", "Wiśniewska"},
                {"Jan", null},
                {"Anna", ""},
                {"Maria", "   "},
                {null, null},
                {"", ""},
                {"   ", "   "}
        };
    }

    @Test(dataProvider = "invalidNames", expectedExceptions = IllegalArgumentException.class)
    public void testCreateUser_InvalidPersonalNames_ThrowsException( String firstName, String lastName ) {
        String email = "test@example.com";

        try {
            // Tutaj firstName = null → w PersonalName albo w metodzie factory rzuci NullPointerException
            userCreationService.create(null, lastName, email);
        } finally {
            // Repozytorium wciąż puste
            assertTrue( userRepository.findAll().isEmpty(),
                    "Repozytorium nie powinno zawierać żadnego wpisu, gdy firstName jest null");
        }
    }

    @DataProvider(name = "invalidEmails")
    public Object[][] invalidEmails() {
        return new Object[][] {
                {null},
                {""},
                {"plainaddress"},
                {"@missingusername.com"},
                {"missingatsign.com"},
                {"missingdomain@.com"},
                {"missingdot@domaincom"},
                {"missing@dotcom."}
        };
    }

    @Test(dataProvider = "invalidEmails", expectedExceptions = IllegalArgumentException.class)
    public void testCreateUser_WithInvalidEmail_ThrowsException( String email ) {
        String firstName = "Anna";
        String lastName  = "Nowak";

        try {
            userCreationService.create(firstName, lastName, email );
        } finally {
            // Repozytorium nie powinno zapisać niczego, bo metoda powinna rzucić wyjątek
            assertTrue( userRepository.findAll().isEmpty(),
                    "Nie powinno być żadnego zapisu w repozytorium, gdy format e-maila jest niepoprawny");
        }
    }

    // TODO
    /*
    testCreateUserDuplicateEmailThrowsBusinessException –
    jeżeli w domenie wprowadzisz regułę „e-mail musi być unikalny”
    realizowaną przez UserDomainService lub w CreateUserService
    (np. najpierw userRepository.existsByEmail(...) → rzut DuplicateEmailException),
    to warto dodać test weryfikujący, że serwis nie wywołuje save(...), tylko zwraca odpowiedni wyjątek.

    testCreateUserTrimsInputStrings – jeżeli chcesz, aby CreateUserService przycinał białe znaki z początku/końca
    (np. firstName.trim()), możesz to sprawdzić w podobny sposób za pomocą ArgumentCaptor.
     */
}
