package tn.esprit.rechargeplus.services;

import tn.esprit.rechargeplus.entities.Basket_items;
import java.util.List;

public interface IBasket_itemsService {
    List<Basket_items> getAllBasket_items();
    Basket_items getBasket_itemsById(Long id);
    Basket_items addBasket_items(Basket_items basket_items);
    Basket_items updateBasket_items(Basket_items basket_items);
    void deleteBasket_items(Long id);
}
