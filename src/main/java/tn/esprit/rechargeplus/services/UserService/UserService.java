package tn.esprit.rechargeplus.services.UserService;


import jakarta.persistence.EntityNotFoundException;
import tn.esprit.rechargeplus.entities.Authority;
import tn.esprit.rechargeplus.entities.User;
import tn.esprit.rechargeplus.repositories.UserRepository.UserRepository;
import tn.esprit.rechargeplus.repositories.UserRepository.AuthorityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Authority authority = authorityRepository.findByAuthority("ROLE_USER");
        if (authority == null) {
            throw new EntityNotFoundException("Authority nullll");
        }
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);
    }

    public void saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Authority authority = authorityRepository.findByAuthority("ROLE_ADMIN");
        user.setAuthorities(Set.of(authority));
        userRepository.save(user);
    }

    // Ajouter un utilisateur
    public User createUser(User user) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("L'email est déjà utilisé !");
        }

        // Hashage du mot de passe avant l'enregistrement
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());

        // Mise à jour du mot de passe uniquement s'il est fourni
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Récupérer un utilisateur par email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Supprimer un utilisateur
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}