package com.project.demo.logic.entity.auth;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<String> resetPassword(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) return Optional.empty();

        String tempPassword = generateSecurePassword();
        System.out.println("Contraseña generada: " + tempPassword);

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        return Optional.of(tempPassword);
    }

    private String generateSecurePassword() {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String allChars = uppercase + lowercase + digits;

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);


        sb.append(uppercase.charAt(random.nextInt(uppercase.length())));


        sb.append(lowercase.charAt(random.nextInt(lowercase.length())));


        sb.append(digits.charAt(random.nextInt(digits.length())));


        for (int i = 0; i < 7; i++) {
            sb.append(allChars.charAt(random.nextInt(allChars.length())));
        }


        return shuffleString(sb.toString(), random);
    }

    private String shuffleString(String input, SecureRandom random) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }

}
