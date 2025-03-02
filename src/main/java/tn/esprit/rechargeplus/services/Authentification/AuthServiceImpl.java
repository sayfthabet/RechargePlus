package tn.esprit.rechargeplus.services.Authentification;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.User;
import tn.esprit.rechargeplus.entities.role_enum;
import tn.esprit.rechargeplus.repositories.UserRepository;
import tn.esprit.rechargeplus.dto.*;
import tn.esprit.rechargeplus.utils.JwtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;
@Autowired
    private PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }
    public AuthentificationResponse Register(SignUpRequest signUpRequest){




        var user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .name(signUpRequest.getName())
                .role(role_enum.CLIENT)
                .build();
        userRepository.save(user);




        var jwtToken = jwtUtil.generateToken(user);

        return AuthentificationResponse.builder()
                .Token(jwtToken)
                .build();
    }


    public User changepassword(ChangePasswordDto changePasswordDto) {
        // Logging for debugging purposes
        log.info("Processing password update for userID: " + changePasswordDto);

        // Retrieve the user from the database
        Optional<User> userOptional = userRepository.findById(changePasswordDto.getIdUser());
        if (!userOptional.isPresent()) {
            // Handle the case where the user is not found
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        // Check if the old password matches


        // Validate the new password (e.g., check its length, complexity, etc.)
        String newPassword = changePasswordDto.getNewPassword();
        /*  if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("New password does not meet the requirements");
        }*/

        // Encode the new password before saving
        String encodedPassword = new BCryptPasswordEncoder().encode(newPassword);
        user.setPassword(encodedPassword);

        // Save the updated user
        userRepository.save(user);

        return user;
    }

    public User updateUser(resetPasswordDto resetPasswordDto) {
        // Logging for debugging purposes
        log.info("Processing password update for userID: " + resetPasswordDto);

        // Retrieve the user from the database
        Optional<User> userOptional = userRepository.findById(resetPasswordDto.getIdUser());
        if (!userOptional.isPresent()) {
            // Handle the case where the user is not found
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        // Check if the old password matches
        String oldPassword = resetPasswordDto.getOldPassword();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            // If the old password does not match, throw an exception
            throw new RuntimeException("Invalid old password");
        }

        // Validate the new password (e.g., check its length, complexity, etc.)
        String newPassword = resetPasswordDto.getNewPassword();
        /*  if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("New password does not meet the requirements");
        }*/

        // Encode the new password before saving
        String encodedPassword = new BCryptPasswordEncoder().encode(newPassword);
        user.setPassword(encodedPassword);

        // Save the updated user
        userRepository.save(user);

        return user;
    }
    @PostConstruct
    public void createAdminAccount(){
        User userAdmin = userRepository.findUserByRole(role_enum.ADMIN);
        if (userAdmin == null) {
            User user = new User();
            user.setEmail("admin@test.com");
            user.setName("Mohamed");
            user.setRole(role_enum.ADMIN);
            user.setPassword(passwordEncoder.encode("Mohamed"));
            userRepository.save(user);

        }
    }


    public List<User> getAllUsers(){
        List<User> list = userRepository.findAll();
        List<User> l = new ArrayList<>();
        for (User i:list
        ) {
            if (i.getRole().equals(role_enum.CLIENT)){
                l.add(i);
            }

        }
        return l;
    }


    public boolean deleteUser(Long id){
        Optional<User> optionalUser= userRepository.findById(id);
        if(optionalUser.isPresent()){
            userRepository.deleteById(id);
            return true;
        }else {
            return false;
        }
    }

  /*  @Override
    public UserDto createUser(SignUpRequest signUpRequest){
    User user = new User();
    user.setEmail(signUpRequest.getEmail());
    user.setName(signUpRequest.getName());
    user.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
    user.setRole(UserRole.CUSTUMER);
    User userCreated = userRepository.save(user);
    UserDto userDto = new  UserDto();
    userDto.setId(userCreated.getId());
    userDto.setEmail(userCreated.getEmail());
    userDto.setName(userCreated.getName());
    userDto.setUserRole(userCreated.getRole());
    log.info("user "+user);
    return userDto;

}
public Boolean hasUserWithEmail(String Email){
return userRepository.findFirstByEmail(Email).isPresent();
}
*/


}
