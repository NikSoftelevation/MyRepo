package com.alibou.security.auth;

import com.alibou.security.config.JwtService;
import com.alibou.security.model.Role;
import com.alibou.security.model.User;
import com.alibou.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest registerRequest) {

       var user= User.builder()
               .firstName(registerRequest.getFirstName())
               .lastName(registerRequest.getLastName())
               .email(registerRequest.getEmail())
               .password(passwordEncoder.encode(registerRequest.getPassword()))
               .role(Role.USER)
               .build();
       userRepository.save(user);
       var jwtToken=jwtService.generateToken(user);
       return AuthenticationResponse.builder()
               .token(jwtToken)
               .build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {

        authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),authenticationRequest.getPassword()));

        var user=userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();

        var jwtToken=jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}