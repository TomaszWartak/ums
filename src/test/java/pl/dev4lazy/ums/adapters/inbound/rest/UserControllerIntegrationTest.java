package pl.dev4lazy.ums.adapters.inbound.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.dev4lazy.ums.adapters.inbound.dto.CreateUserRequestDto;
import pl.dev4lazy.ums.adapters.outbound.persistence.UserRepositoryAdapter;
import pl.dev4lazy.ums.application.UserCreationService;
import pl.dev4lazy.ums.domain.service.EmailAlreadyExistsException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private UserCreationService userCreationService;

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

        mockMvc.perform( post("/api/users")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString(validRequest) )
                        .accept( MediaType.APPLICATION_JSON) )
                .andExpect( status().isCreated() )
                .andExpect( content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON) )
                .andExpect( jsonPath("$.id").value(1) );
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
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "Jan",
                "Kowalski",
                "duplicate@example.com"
        );

        mockMvc.perform( post("/api/users")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( requestDto ) )
                        .accept( MediaType.APPLICATION_JSON) )
                .andExpect( status().isCreated() )
                .andExpect( content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON) )
                .andExpect( jsonPath("$.id").value(1) );

        mockMvc.perform(post("/api/users")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString(requestDto) )
                        .accept( MediaType.APPLICATION_JSON) )
                 // Weryfikacja odpowiedzi: 409 Conflict i JSON z polem "error"
                .andExpect( status().isConflict() )
                .andExpect( content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON) )
                .andExpect( jsonPath("$.error").value("E-mail już istnieje") );
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

}