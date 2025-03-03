package tn.esprit.rechargeplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.rechargeplus.entities.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByVerificationToken(String token);
}
