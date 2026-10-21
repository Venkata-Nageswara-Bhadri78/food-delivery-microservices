package com.natixis.auth.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.natixis.auth.dto.request.CreateAdminRequest;
import com.natixis.auth.dto.request.CreateAgentRequest;
import com.natixis.auth.dto.request.CreateCustomerRequest;
import com.natixis.auth.dto.request.LoginRequest;
import com.natixis.auth.dto.request.RegisterRequest;
import com.natixis.auth.dto.response.ApiResponse;
import com.natixis.auth.dto.response.LoginResponse;
import com.natixis.auth.entity.User;
import com.natixis.auth.enums.UserType;
import com.natixis.auth.exception.EmailAlreadyExistsException;
import com.natixis.auth.exception.InvalidCredentialsException;
import com.natixis.auth.exception.UserNotFoundException;
import com.natixis.auth.repository.UserRepository;
import com.natixis.auth.security.JwtService;
import com.natixis.auth.service.AuthService;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AuthServiceImpl implements AuthService{
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final RestClient restClient;

    @Transactional 
    public void register(RegisterRequest registerRequest){
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new EmailAlreadyExistsException("Email Already Exists");
        }

        User user = User.builder()
            .name(registerRequest.getName())
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .phone(registerRequest.getPhone())
            .address(registerRequest.getAddress())
            .isActive(true)
            .userType(registerRequest.getUserType())
            .build();
        User savedUser = userRepository.save(user);
        if(user.getUserType().equals(UserType.CUSTOMER)){
            CreateCustomerRequest customerRequest = new CreateCustomerRequest(savedUser.getEmail(), savedUser.getId(), savedUser.getPhone(), savedUser.getAddress());
            ResponseEntity<ApiResponse<String>> response = restClient.post()
                                                        .uri("http://CUSTOMER-SERVICE/customer")
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .body(customerRequest)
                                                        .retrieve()
                                                        .toEntity(new ParameterizedTypeReference<ApiResponse<String>>() {});
            if(!response.getBody().getSuccess()){
                throw new UserNotFoundException("User Not Found");
            }
        }
        else if(user.getUserType().equals(UserType.AGENT)){
            CreateAgentRequest agentRequest = new CreateAgentRequest(savedUser.getId(), registerRequest.getEmail(), null, null, null);
            ResponseEntity<ApiResponse<String>> response = restClient.post()
                                                        .uri("http://AGENT-SERVICE/agent")
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .body(agentRequest)
                                                        .retrieve()
                                                        .toEntity(new ParameterizedTypeReference<ApiResponse<String>>() {});
            if(!response.getBody().getSuccess()){
                throw new UserNotFoundException("Agent Registration Failed");
            }
        }
        else if(user.getUserType().equals(UserType.ADMIN)){
            CreateAdminRequest adminRequest = new CreateAdminRequest(savedUser.getId(), registerRequest.getEmail());
            ResponseEntity<ApiResponse<String>> response = restClient.post()
                                                        .uri("http://ADMIN-SERVICE/admin")
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .body(adminRequest)
                                                        .retrieve()
                                                        .toEntity(new ParameterizedTypeReference<ApiResponse<String>>() {});
            if(!response.getBody().getSuccess()){
                throw new UserNotFoundException("Admin Registration Failed");
            }
        }
    }

    public LoginResponse login(LoginRequest loginRequest){
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new UserNotFoundException("User Not Found"));
        boolean isPasswordMatched = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        if(!isPasswordMatched){
            throw new InvalidCredentialsException("Invalid Password - try again");
        }
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getUserType());
        return new LoginResponse(token);
    }

    public void logout() {
    }

    public UserType getCurrentUserType(){
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        return UserType.valueOf(role.replace("ROLE_", ""));
    }
}
