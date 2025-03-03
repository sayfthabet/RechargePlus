package tn.esprit.rechargeplus.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.User;
import tn.esprit.rechargeplus.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ResponseEntity<?> register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerified(false);
        userRepository.save(user);

        emailService.sendVerificationEmail(user);
        
        return ResponseEntity.ok("User registered successfully! Please check your email for verification.");
    }

    public ResponseEntity<?> verifyEmail(String token) {
        Optional<User> user = userRepository.findByVerificationToken(token);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid verification token");
        }
        
        User verifiedUser = user.get();
        verifiedUser.setVerified(true);
        verifiedUser.setVerificationToken(null);
        userRepository.save(verifiedUser);
        
        return ResponseEntity.ok("Email verified successfully!");
    }
}
