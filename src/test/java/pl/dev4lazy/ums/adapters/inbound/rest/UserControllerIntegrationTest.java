package pl.dev4lazy.ums.adapters.inbound.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.dev4lazy.ums.adapters.inbound.dto.CreateUserRequestDto;
import pl.dev4lazy.ums.adapters.outbound.persistence.UserRepositoryAdapter;
import pl.dev4lazy.ums.application.UserCreationService;
import pl.dev4lazy.ums.domain.model.user.UserStatus;
import pl.dev4lazy.ums.utils.Messages;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserControllerIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    /* TODO
    @Autowired
    private UserCreationService userCreationService;

     */

    @Autowired
    private ObjectMapper objectMapper;

        @BeforeMethod
    public void cleanDatabase() {
        // Przed każdym testem czyścimy tabelę users w H2
        userRepositoryAdapter.deleteAll();
    }

    @Test
    public void rootEndpoint_ShouldReturnJson() throws Exception {
        mockMvc.perform(get("/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCreateUser_ValidInput_ReturnsCreatedWithId() throws Exception {
        CreateUserRequestDto validRequest = new CreateUserRequestDto(
                "Jan",
                "Kowalski",
                "jan.kowalski@example.com"
        );

        Long maxId = userRepositoryAdapter.findMaxId();
        maxId++;
        mockMvc.perform( post("/api/users")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString(validRequest) )
                        .accept( MediaType.APPLICATION_JSON) )
                .andExpect( status().isCreated() )
                .andExpect( content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON) )
                .andExpect( jsonPath("$.id").value( maxId ) );
    }

    @Test
    public void testCreateUser_MissingFirstName_ReturnsBadRequest() throws Exception {
        String jsonMissingFirstName = """
        {
          "lastName": "Kowalski",
          "email": "jan.kowalski@example.com"
        }
        """;

        mockMvc.perform( post("/api/users")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( jsonMissingFirstName )
                        .accept( MediaType.APPLICATION_JSON) )
                .andExpect( status().isBadRequest() );
    }


    @Test
    public void testCreateUser_MissingLastName_ReturnsBadRequest() throws Exception {
        String jsonMissingLastName = """
        {
          "firstName": "Jan",
          "email": "jan.kowalski@example.com"
        }
        """;

        mockMvc.perform( post("/api/users")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( jsonMissingLastName )
                        .accept( MediaType.APPLICATION_JSON) )
                .andExpect( status().isBadRequest() );
    }


    @Test
    public void testCreateUser_MissingEmail_ReturnsBadRequest() throws Exception {
        String jsonMissingEmail = """
        {
          "firstName": "Jan",
          "lastName": "Kowalski"
        }
        """;

        mockMvc.perform( post("/api/users")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( jsonMissingEmail )
                        .accept( MediaType.APPLICATION_JSON) )
                .andExpect( status().isBadRequest() );
    }

    @Test
    public void testCreateUser_EmptyFirstName_ReturnsBadRequest() throws Exception {
        CreateUserRequestDto dto = new CreateUserRequestDto(
                "",
                "Kowalski",
                "jan.kowalski@example.com"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUser_EmptyLastName_ReturnsBadRequest() throws Exception {
        CreateUserRequestDto dto = new CreateUserRequestDto(
                "Jan",
                "",
                "jan.kowalski@example.com"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUser_EmptyEmail_ReturnsBadRequest() throws Exception {
        CreateUserRequestDto dto = new CreateUserRequestDto(
                "Jan",
                "Kowalski",
                ""
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUser_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
        CreateUserRequestDto dto = new CreateUserRequestDto(
                "Jan",
                "Kowalski",
                "invalid-email-format"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateUser_WithDuplicateEmail_ReturnsConflict() throws Exception {
        String emailDuplicated = "duplicate@example.com";
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "Jan",
                "Kowalski",
                emailDuplicated
        );

        mockMvc.perform( post("/api/users")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( requestDto ) )
                        .accept( MediaType.APPLICATION_JSON) )
                .andExpect( status().isCreated() )
                .andExpect( content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON) );

        mockMvc.perform(post("/api/users")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString(requestDto) )
                        .accept( MediaType.APPLICATION_JSON) )
                 // Weryfikacja odpowiedzi: 409 Conflict i JSON z polem "error"
                .andExpect( status().isConflict() )
                .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
                .andExpect( jsonPath("$.error").value( String.format( Messages.USER_EMAIL_DUPLICATED, emailDuplicated ) ) );
    }

    @Test
    public void testCreateUser_WrongContentType_ReturnsUnsupportedMediaType() throws Exception {
        CreateUserRequestDto dto = new CreateUserRequestDto(
                "Jan", "Kowalski", "jan.kowalski@example.com"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.TEXT_PLAIN)  // zamiast application/json
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void testGetAllUsers_EmptyDatabase_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testGetAllUsers_WithExistingUsers_ReturnsListOfUsers() throws Exception {
        // 1. Utwórzmy kilku użytkowników przez endpoint POST /api/users
        CreateUserRequestDto dto1 = new CreateUserRequestDto(
                "Jan", "Kowalski", "jan.kowalski@example.com"
        );
        CreateUserRequestDto dto2 = new CreateUserRequestDto(
                "Anna", "Nowak", "anna.nowak@example.com"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // Sprawdźmy, że zwrócono dokładnie 2 elementy w tablicy
                .andExpect(jsonPath("$.length()").value(2))
                // Weryfikujemy pola pierwszego użytkownika (kolejność w tablicy wg zapisu do DB)
                .andExpect(jsonPath("$[0].firstName").value("Jan"))
                .andExpect(jsonPath("$[0].lastName").value("Kowalski"))
                .andExpect(jsonPath("$[0].email").value("jan.kowalski@example.com"))
                .andExpect(jsonPath("$[0].status").value(UserStatus.INACTIVE.name()))
                // Weryfikujemy pola drugiego użytkownika
                .andExpect(jsonPath("$[1].firstName").value("Anna"))
                .andExpect(jsonPath("$[1].lastName").value("Nowak"))
                .andExpect(jsonPath("$[1].email").value("anna.nowak@example.com"))
                .andExpect(jsonPath("$[1].status").value(UserStatus.INACTIVE.name()));
    }

    @Test
    public void testActivate_ExistingUser_SetsStatusToActive() throws Exception {
        // 1. Utwórz nowego użytkownika za pomocą POST /api/users
        CreateUserRequestDto createDto = new CreateUserRequestDto(
                "Marek", "Nowak", "marek.nowak@example.com"
        );
        String createResponseJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Wyciągnij ID z odpowiedzi { "id": <number> }
        JsonNode createResponse = objectMapper.readTree(createResponseJson);
        Long userId = createResponse.get("id").asLong();

        // 2. Upewnij się, że przed aktywacją status jest INACTIVE
        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userId.intValue()))
                .andExpect(jsonPath("$[0].status").value(UserStatus.INACTIVE.name()));

        // 3. Wywołaj PUT /api/users/{id}/activate
        mockMvc.perform( put("/api/users/{id}/activate", userId) )
                .andExpect(status().isOk());

        // 4. Po aktywacji sprawdź ponownie GET /api/users i weryfikuj status ACTIVE
        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userId.intValue()))
                .andExpect(jsonPath("$[0].status").value(UserStatus.ACTIVE.name()));
    }

    @Test
    public void testActivate_NonExistingUser_ReturnsNotFound() throws Exception {
        // Załóżmy, że w bazie nie ma użytkownika o ID 9999
        Long nonExistingId = 9999L;

        // Wywołaj PUT /api/users/{nonExistingId}/activate
        mockMvc.perform(put("/api/users/{id}/activate", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                // Oczekujemy 404 Not Found
                .andExpect(status().isNotFound())
                // Oraz, że odpowiedź jest JSON-em zawierającym klucz "error"
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value( equalTo(String.format(Messages.USER_NOT_FOUND, nonExistingId))));
    }
}