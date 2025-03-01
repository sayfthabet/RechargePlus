package tn.esprit.rechargeplus.services;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Basket_items;
import tn.esprit.rechargeplus.repositories.ProductRepository.Basket_itemsRepository;
import java.util.List;
@Service
@AllArgsConstructor
public class Basket_itemsServiceImpl implements IBasket_itemsService {
    @Autowired
    Basket_itemsRepository basket_itemsRepository;
    @Override
    public List<Basket_items> getAllBasket_items() {
        return basket_itemsRepository.findAll();
    }
    @Override
    public Basket_items getBasket_itemsById(Long id) {
        return basket_itemsRepository.findById(id).orElse(null);
    }
    @Override
    public Basket_items addBasket_items(Basket_items basket_items) {
        return basket_itemsRepository.save(basket_items);
    }
    @Override
    public Basket_items updateBasket_items(Basket_items basket_items) {return basket_itemsRepository.save(basket_items); }
    @Override
    public void deleteBasket_items(Long id) {
        basket_itemsRepository.deleteById(id);
    }
}
