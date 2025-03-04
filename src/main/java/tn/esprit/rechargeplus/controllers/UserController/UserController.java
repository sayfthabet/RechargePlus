package tn.esprit.rechargeplus.controllers.UserController;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.JwtResponse;
import tn.esprit.rechargeplus.entities.User;
import tn.esprit.rechargeplus.services.UserService.UserService;
import tn.esprit.rechargeplus.util.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

/**
 * Main controller class of the application.
 *
 * @author Rezaur Rahman
 */
@RestController
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    /**
     * Constructor for MainController class.
     *
     * @param authenticationManager the instance of authentication manager.
     * @param jwtUtils              the instance of utility class for JWT operations.
     * @param userService           the instance of user service class.
     */
    public UserController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    /**
     * Handles the authentication request and generates a JWT token.
     *
     * @param user the user credentials.
     * @return ResponseEntity containing the JWT token.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody User user) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    user.getUsername(), user.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Incorrect credentials!", HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);
        JwtResponse jwtResponse = new JwtResponse.Builder()
                .type("Bearer")
                .username(user.getUsername())
                .token(jwt)
                .build();

        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    /**
     * Registers a new user.
     *
     * @param user the user to be registered.
     * @return ResponseEntity indicating the registration status.
     */
    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (user == null) {
            return ResponseEntity.badRequest().body("Request body cannot be null");
        }

        User existingUser = userService.findUserByUsername(user.getUsername());

        if (existingUser != null) {
            return ResponseEntity.badRequest().body("User already exists!");
        }

        userService.saveUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Registers a new admin.
     * <p>
     * To use this API, client application needs to pass access token with role 'ADMIN'.
     *
     * @param user the admin to be registered.
     * @return ResponseEntity indicating the registration status.
     */
        @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(@RequestBody User user) {

            if (user == null) {
                return ResponseEntity.badRequest().body("Request body cannot be null");
            }

            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                return ResponseEntity.badRequest().body("Username cannot be empty");
            }


        User existingUser = userService.findUserByUsername(user.getUsername());

        if (existingUser != null)
            return ResponseEntity.badRequest().body("User already exists!");

        userService.saveAdmin(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Dummy controller endpoint for regular users.
     * <p>
     * To use this API, client application needs to pass access token with role 'USER' or 'ADMIN'.
     *
     * @return Welcome message.
     */
    @GetMapping("/user")
    public String welcomeUser() {
        return "Welcome to user controller";
    }

    /**
     * Dummy controller endpoint for admin users.
     * <p>
     * To use this API, client application needs to pass access token with role 'ADMIN'.
     *
     * @return Welcome message.
     */
    @GetMapping("/admin")
    public String welcomeAdmin() {
        return "Welcome to admin controller";
    }

    ////////////////////////////////////////////////////////
// Récupérer tous les utilisateurs
    @GetMapping ("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Récupérer un utilisateur par email
    @GetMapping("/{email}")
    public Optional<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    // Ajouter un nouvel utilisateur
    @PostMapping("/add")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    //  Mettre à jour un utilisateur
    @PutMapping("/up/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userService.updateUser(id, updatedUser);
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
