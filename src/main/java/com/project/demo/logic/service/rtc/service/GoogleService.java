package com.project.demo.logic.service.rtc.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.entity.user.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoogleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final String clientId;

    public GoogleService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            @Value("${google.client-id}") String clientId
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.clientId = clientId;
    }


    public LoginResponse authenticateWithGoogle(String credential) throws Exception {

        GoogleIdToken.Payload payload = verifyToken(credential);

        User user = findOrCreateUser(payload);

        return generateLoginResponse(user);
    }

    // VERIFICAR TOKEN GOOGLE
    private GoogleIdToken.Payload verifyToken(String credential) throws Exception {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(credential);

        if (idToken == null) {
            idToken = GoogleIdToken.parse(GsonFactory.getDefaultInstance(), credential);
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        if (!payload.getAudience().equals(clientId)) {
            throw new RuntimeException("Invalid audience");
        }

        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new RuntimeException("Email not verified by Google");
        }

        return payload;
    }


    private User findOrCreateUser(GoogleIdToken.Payload payload) {

        String email = payload.getEmail();
        String fullName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        Optional<Role> userRole = roleRepository.findByName(RoleEnum.USER);

        if (userRole.isEmpty()) {
            throw new RuntimeException("Role USER not found");
        }

        User newUser = new User(
                fullName,
                lastName,
                email,
                true,
                "google",
                passwordEncoder.encode(UUID.randomUUID().toString()),
                userRole.get()
        );

        return userRepository.save(newUser);
    }


    private LoginResponse generateLoginResponse(User user) {

        String jwtToken = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse();
        response.setToken(jwtToken);
        response.setExpiresIn(jwtService.getExpirationTime());
        response.setAuthUser(user);

        return response;
    }
}
