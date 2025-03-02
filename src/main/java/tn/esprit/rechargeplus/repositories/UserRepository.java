package tn.esprit.rechargeplus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.rechargeplus.entities.User;
import tn.esprit.rechargeplus.entities.role_enum;


import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    User findUserByRole(role_enum role);

}
