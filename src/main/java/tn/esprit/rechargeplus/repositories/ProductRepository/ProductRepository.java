package tn.esprit.rechargeplus.repositories.ProductRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.rechargeplus.entities.Product;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
