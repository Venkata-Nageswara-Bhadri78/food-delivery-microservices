package com.natixis.auth.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;

import com.natixis.auth.dto.request.CreateCustomerRequest;
import com.natixis.auth.dto.request.LoginRequest;
import com.natixis.auth.dto.request.RegisterRequest;
import com.natixis.auth.dto.response.ApiResponse;
import com.natixis.auth.dto.response.LoginResponse;
import com.natixis.auth.entity.User;
import com.natixis.auth.enums.UserType;
import com.natixis.auth.exception.EmailAlreadyExistsException;
import com.natixis.auth.exception.InvalidCredentialsException;
import com.natixis.auth.repository.UserRepository;
import com.natixis.auth.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestBodyUriSpec postSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtService, restClient);
    }

    @Test
    void loginReturnsTokenWhenPasswordMatches() {
        User user = user("ada@test.com", "hashed", UserType.CUSTOMER);
        when(userRepository.findByEmail("ada@test.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);
        when(jwtService.generateToken(3L, "ada@test.com", UserType.CUSTOMER)).thenReturn("jwt-token");

        LoginResponse response = authService.login(login("ada@test.com", "secret123"));

        assertEquals("jwt-token", response.getAccessToken());
    }

    @Test
    void loginRejectsWrongPassword() {
        User user = user("ada@test.com", "hashed", UserType.CUSTOMER);
        when(userRepository.findByEmail("ada@test.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(login("ada@test.com", "wrong-password")));
        verify(jwtService, never()).generateToken(any(), anyString(), any());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmail("ada@test.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(register(UserType.CUSTOMER)));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerSavesCustomerAndCreatesCustomerProfile() {
        when(userRepository.existsByEmail("ada@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });

        when(restClient.post()).thenReturn(postSpec);
        when(postSpec.uri("http://CUSTOMER-SERVICE/customer")).thenReturn(postSpec);
        when(postSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(postSpec);

        doReturn(postSpec).when(postSpec).body(any(CreateCustomerRequest.class));

        when(postSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.toEntity(
                ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<Object>>>any()
        )).thenReturn(
                ResponseEntity.ok(
                        new ApiResponse<Object>(true, "created", null)
                )
        );

        authService.register(register(UserType.CUSTOMER));

        verify(userRepository).save(any(User.class));
        verify(restClient).post();
    }

    private static LoginRequest login(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setUserType(UserType.CUSTOMER);
        return request;
    }

    private static RegisterRequest register(UserType userType) {
        RegisterRequest request = new RegisterRequest();
        request.setName("Ada");
        request.setEmail("ada@test.com");
        request.setPassword("secret123");
        request.setPhone("9999999999");
        request.setAddress("12 Main Street");
        request.setUserType(userType);
        return request;
    }

    private static User user(String email, String password, UserType userType) {
        return User.builder()
                .id(3L)
                .name("Ada")
                .email(email)
                .password(password)
                .phone("9999999999")
                .address("12 Main Street")
                .isActive(true)
                .userType(userType)
                .build();
    }
}
