package tn.esprit.rechargeplus.repositories.ProductRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.rechargeplus.entities.Rating;
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
}
