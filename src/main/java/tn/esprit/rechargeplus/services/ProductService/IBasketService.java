package tn.esprit.rechargeplus.services.ProductService;
import tn.esprit.rechargeplus.entities.Basket;
import tn.esprit.rechargeplus.entities.Basket_items;

import java.util.List;
public interface IBasketService {
    List<Basket> getAllBaskets();
    Basket getBasketById(Long id);
    Basket addBasket(Basket basket);
    Basket updateBasket(Basket basket);
    void deleteBasket(Long id);
    Basket addProductToBasket(Long basketId, Long productId, int quantity);
    Basket removeProductFromBasket(Long basketId, Long productId);
    List<Basket_items> getBasketItems(Long basketId);
    //void clearBasket(Long basketId);
}
