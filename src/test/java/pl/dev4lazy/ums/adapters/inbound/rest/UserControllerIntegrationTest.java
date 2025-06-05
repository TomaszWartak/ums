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

    @Autowired
    private ObjectMapper objectMapper;

        @BeforeMethod
    public void cleanDatabase() {
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
        CreateUserRequestDto dummyRequest = new CreateUserRequestDto(
                "Dummy",
                "Dummy",
                "dummy@example.com"
        );

        mockMvc.perform( post("/api/users")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( dummyRequest ) )
                        .accept( MediaType.APPLICATION_JSON) )
                .andExpect( status().isCreated() );

        Long maxId = userRepositoryAdapter.findMaxId();
        maxId++;

        CreateUserRequestDto validRequest = new CreateUserRequestDto(
                "Jan",
                "Kowalski",
                "jan.kowalski@example.com"
        );

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
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnsupportedMediaType());
    }

    // ************ testy dotyczące pobierania danych użytkownika
    @Test
    public void testGetUser_ExistingUser_ReturnsUser() throws Exception {
        // given
        CreateUserRequestDto createDto = new CreateUserRequestDto(
                "Jan", "Testowy", "jan.testowy@example.com"
        );

        String createResponseJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode createResponse = objectMapper.readTree(createResponseJson);
        Long userId = createResponse.get("id").asLong();

        // when & then
        mockMvc.perform(get("/api/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Testowy"))
                .andExpect(jsonPath("$.email").value("jan.testowy@example.com"))
                .andExpect(jsonPath("$.status").value(UserStatus.INACTIVE.name()));
    }

    @Test
    public void testGetUser_NonExistingUser_ReturnsNotFound() throws Exception {
        // given
        Long nonExistingId = 9999L;

        // when & then
        mockMvc.perform(get("/api/users/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value(String.format(Messages.USER_NOT_FOUND, nonExistingId)));
    }

    @Test
    public void testGetAllUsers_EmptyDatabase_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ************ testy dotyczące pobierania listy użytkowników
    @Test
    public void testGetAllUsers_WithExistingUsers_ReturnsListOfUsers() throws Exception {
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
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Jan"))
                .andExpect(jsonPath("$[0].lastName").value("Kowalski"))
                .andExpect(jsonPath("$[0].email").value("jan.kowalski@example.com"))
                .andExpect(jsonPath("$[0].status").value(UserStatus.INACTIVE.name()))
                .andExpect(jsonPath("$[1].firstName").value("Anna"))
                .andExpect(jsonPath("$[1].lastName").value("Nowak"))
                .andExpect(jsonPath("$[1].email").value("anna.nowak@example.com"))
                .andExpect(jsonPath("$[1].status").value(UserStatus.INACTIVE.name()));
    }

    @Test
    public void testActivate_ExistingUser_SetsStatusToActive() throws Exception {
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

        JsonNode createResponse = objectMapper.readTree(createResponseJson);
        Long userId = createResponse.get("id").asLong();

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userId.intValue()))
                .andExpect(jsonPath("$[0].status").value(UserStatus.INACTIVE.name()));

        mockMvc.perform( put("/api/users/{id}/activate", userId) )
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userId.intValue()))
                .andExpect(jsonPath("$[0].status").value(UserStatus.ACTIVE.name()));
    }

    @Test
    public void testActivate_NonExistingUser_ReturnsNotFound() throws Exception {
        Long nonExistingId = 9999L;

        mockMvc.perform(put("/api/users/{id}/activate", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value( equalTo(String.format(Messages.USER_NOT_FOUND, nonExistingId))));
    }

    @Test
    public void testDeactivate_ExistingActiveUser_SetsStatusToInactive() throws Exception {
        CreateUserRequestDto createDto = new CreateUserRequestDto(
                "Marek", "Nowak", "marek.nowak@example.com"
        );
        String createJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode created = objectMapper.readTree(createJson);
        Long userId = created.get("id").asLong();

        mockMvc.perform(put("/api/users/{id}/activate", userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userId.intValue()))
                .andExpect(jsonPath("$[0].status").value(UserStatus.ACTIVE.name()));

        mockMvc.perform(put("/api/users/{id}/deactivate", userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userId.intValue()))
                .andExpect(jsonPath("$[0].status").value(UserStatus.INACTIVE.name()));
    }

    @Test
    public void testDeactivate_NonExistingUser_ReturnsNotFound() throws Exception {
        Long nonExistingId = 9999L;

        mockMvc.perform(put("/api/users/{id}/deactivate", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error")
                        .value(String.format(Messages.USER_NOT_FOUND, nonExistingId)));
    }

    @Test
    public void testDeactivate_ExistingInactiveUser_StatusRemainsInactive() throws Exception {
        CreateUserRequestDto createDto = new CreateUserRequestDto(
                "Ola", "Nowak", "ola.nowak@example.com"
        );
        String createJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode created = objectMapper.readTree(createJson);
        Long userId = created.get("id").asLong();

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userId.intValue()))
                .andExpect(jsonPath("$[0].status").value(UserStatus.INACTIVE.name()));

        mockMvc.perform(put("/api/users/{id}/deactivate", userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userId.intValue()))
                .andExpect(jsonPath("$[0].status").value(UserStatus.INACTIVE.name()));
    }
}