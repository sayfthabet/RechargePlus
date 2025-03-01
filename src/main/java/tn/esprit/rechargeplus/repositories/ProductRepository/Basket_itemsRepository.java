package tn.esprit.rechargeplus.repositories.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.rechargeplus.entities.Basket_items;
@Repository
public interface Basket_itemsRepository extends JpaRepository<Basket_items, Long> {
}
