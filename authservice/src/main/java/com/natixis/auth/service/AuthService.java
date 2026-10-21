package com.natixis.auth.service;

import com.natixis.auth.dto.request.LoginRequest;
import com.natixis.auth.dto.request.RegisterRequest;
import com.natixis.auth.dto.response.LoginResponse;

public interface AuthService {
    public void register(RegisterRequest registerRequest);
    public LoginResponse login(LoginRequest loginRequest);
    public void logout();
}
