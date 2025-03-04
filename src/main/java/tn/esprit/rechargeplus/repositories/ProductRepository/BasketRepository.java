package tn.esprit.rechargeplus.repositories.ProductRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.rechargeplus.entities.Basket;
@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
}
