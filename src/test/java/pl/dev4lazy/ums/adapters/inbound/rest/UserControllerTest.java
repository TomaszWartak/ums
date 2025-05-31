package pl.dev4lazy.ums.adapters.inbound.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import pl.dev4lazy.ums.adapters.inbound.dto.CreateUserRequestDto;
//import pl.dev4lazy.ums.application.ActivateUserService;
import pl.dev4lazy.ums.application.UserCreationService;
//import pl.dev4lazy.ums.application.DeactivateUserService;
//import pl.dev4lazy.ums.application.ListUsersService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
//@AutoConfigureMockMvc
public class UserControllerTest /* todo extends AbstractTestNGSpringContextTests */ {

    @Mock
    private UserCreationService userCreationService;

    @InjectMocks
    private UserController userController;
    private CreateUserRequestDto validRequest;

    // todo @Autowired
    private MockMvc mockMvc;
    private static ObjectMapper objectMapper;

    @BeforeClass
    public void initAll() {
        // Inicjalizujemy @Mock i @InjectMocks
        MockitoAnnotations.openMocks(this);

        // Tworzymy MockMvc w trybie „standalone”
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        // Tworzymy ObjectMapper (do serializacji JSON‐a)
        objectMapper = new ObjectMapper();
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

        when( userCreationService.create(
                eq( validRequest.firstName() ),
                eq( validRequest.lastName() ),
                eq( validRequest.email() )
        )).thenReturn( 10L );

        // 2. Wywołanie endpointu POST /api/users
        mockMvc.perform( post("/api/users")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString(validRequest) )
                        .accept( MediaType.APPLICATION_JSON) )
                // 3. Oczekujemy statusu 201 CREATED
                .andExpect( status().isCreated() )
                // 4. Oczekujemy JSON-a: { "id": 10 }
                .andExpect( content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON) )
                .andExpect( jsonPath("$.id").value(10) );
    }

}