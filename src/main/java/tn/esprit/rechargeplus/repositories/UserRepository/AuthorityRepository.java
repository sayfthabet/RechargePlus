package tn.esprit.rechargeplus.repositories.UserRepository;

import tn.esprit.rechargeplus.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

	Authority findByAuthority(String authority);

}