package tn.esprit.rechargeplus.repositories.UserRepository;

import tn.esprit.rechargeplus.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsernameIgnoreCase(String username);
	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);
}