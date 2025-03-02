package tn.esprit.rechargeplus.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.User;
import tn.esprit.rechargeplus.repositories.UserRepository;
import tn.esprit.rechargeplus.services.Authentification.AuthServiceImpl;
import tn.esprit.rechargeplus.dto.*;
import tn.esprit.rechargeplus.utils.JwtUtil;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthServiceImpl authService;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;
    @Autowired
    public AuthController(AuthServiceImpl authService, AuthenticationManager authenticationManager,
                          UserRepository userRepository, JwtUtil jwtUtil) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public static final String TOKEN_PREFIX ="Bearer ";
    public static final String HEADER_STRING ="Authorization";



    @PostMapping("/auth/login")
    public ResponseEntity<String> createAuthenticationToken(@RequestBody AuthentificationRequest authenticationRequest, HttpServletResponse response) throws IOException, JSONException {
        try {
            // Add debug log to check the received credentials
            System.out.println("Received Email: " + authenticationRequest.getUsername());
            System.out.println("Received Password: " + authenticationRequest.getPassword());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );

            // ... (rest of your authentication logic)
        } catch (BadCredentialsException e) {
            // Log the authentication failure for debugging purposes
            System.out.println("Authentication failed: " + e.getMessage());
            throw new BadCredentialsException("Username or password incorrect");
        }


        Optional<User> user = userRepository.findByEmail(authenticationRequest.getUsername());

        if (user.isPresent()) {
            var jwtToken = jwtUtil.generateToken(user.get());
            JSONObject jsonResponse = new JSONObject()
                    .put("userId", user.get().getIdUser())
                    .put("role", user.get().getRole())
                    .put("name", user.get().getName());

            response.addHeader("Access-Control-Expose-Headers","Authorization");
            response.addHeader("Access-Control-Allow-Headers","Authorization ,X-PINGOTHER ,Origin," +
                    "X-Requested-With,Content-Type,Accept,X-Custom-header");

            response.addHeader(HEADER_STRING, TOKEN_PREFIX + jwtToken);

            return ResponseEntity.ok().body(jsonResponse.toString());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }


    @PostMapping("/auth/signup")
    public ResponseEntity<AuthentificationResponse> signUp(@RequestBody SignUpRequest signUpRequest){

        return ResponseEntity.ok(authService.Register(signUpRequest));

    }
    @GetMapping("/getAllUsers")
        //@PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<User>> GetAll(){
        List<User> list = authService.getAllUsers();
        return ResponseEntity.status(HttpStatus.CREATED).body(list);

    }

    @DeleteMapping("/deleteUser/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delteuser(@PathVariable Long id){
        boolean delete = authService.deleteUser(id);
        if (delete){
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.notFound().build();
        }

    }
    @PutMapping("/auth/reset-password")
    public User UpdateUser(@RequestBody resetPasswordDto resetPasswordDto) {

        return authService.updateUser(resetPasswordDto);
    }
    @PutMapping("/auth/change-password")
    public User UpdateUser(@RequestBody ChangePasswordDto changePasswordDto) {

        return authService.changepassword(changePasswordDto);
    }


}
