package com.trackit.auth.service;

import com.trackit.auth.entity.Authentication;

public interface AuthService {

    Authentication register(Authentication user);

    Authentication updateUserById(Authentication user, Long id);

    Authentication getUserByEmail(String email);
    
    boolean checkPassword(String rawPassword, String encodedPassword);
    
    void forgotPassword(String email);

}
