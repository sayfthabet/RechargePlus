package tn.esprit.rechargeplus.controllers.ProductController;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.Basket_items;
import tn.esprit.rechargeplus.services.ProductService.IBasket_itemsService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/basket_items")
public class Basket_itemsController {
    @Autowired
    IBasket_itemsService b_itemsService;
    @GetMapping("/getBasketItems")
    public List<Basket_items> AllBasketItems() {return b_itemsService.getAllBasket_items();}
    @PostMapping("/addBasketItem")
    public Basket_items addBasketItem(@RequestBody Basket_items basket_items) {
    return b_itemsService.addBasket_items(basket_items);
    }

    @DeleteMapping("/deleteBasketItem/{id}")
    public void deleteBasketItem(@PathVariable Long id) {
        b_itemsService.deleteBasket_items(id);
    }

    public void updateBasketItem() {
    }

    public void getBasketItemById() {
    }


}
