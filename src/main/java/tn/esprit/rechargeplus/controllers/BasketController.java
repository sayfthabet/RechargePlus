package tn.esprit.rechargeplus.controllers;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.Basket;
import tn.esprit.rechargeplus.entities.Basket_items;
import tn.esprit.rechargeplus.services.IBasketService;
import tn.esprit.rechargeplus.services.BasketServiceImpl;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/basket")
public class BasketController {
    @Autowired
    IBasketService basketService;
    @GetMapping("/getBaskets")
    public List<Basket> AllBaskets() {return basketService.getAllBaskets();}
    @PostMapping("/addBasket")
    public Basket addBasket(@RequestBody Basket basket) {
    return basketService.addBasket(basket);
    }

    @DeleteMapping("/deleteBasket/{id}")
    public void deleteBasket(@PathVariable Long id) {
        basketService.deleteBasket(id);
    }

    @PatchMapping("/updateBasket")
    public void updateBasket(@RequestBody Basket basket) {
        basketService.updateBasket(basket);
    }

    @GetMapping("/getBasketById/{id}")
    public Basket getBasketById(@PathVariable long id) {
        return basketService.getBasketById(id);
    }

    @PatchMapping("/{basketId}/add/{productId}/{quantity}")
    public ResponseEntity<Basket> addProduct(@PathVariable Long basketId,
                                             @PathVariable Long productId,
                                             @PathVariable int quantity) {
        return ResponseEntity.ok(basketService.addProductToBasket(basketId, productId, quantity));
    }


     @PatchMapping("/{basketId}/remove/{productId}")
    public ResponseEntity<Basket> removeProduct(@PathVariable Long basketId,
                                                @PathVariable Long productId) {
        return ResponseEntity.ok(basketService.removeProductFromBasket(basketId, productId));
    }

    @GetMapping("/{basketId}/items")
    public ResponseEntity<List<Basket_items>> getBasketItems(@PathVariable Long basketId) {
        return ResponseEntity.ok(basketService.getBasketItems(basketId));
    }
/*
    @DeleteMapping("/{basketId}/clear")
    public ResponseEntity<String> clearBasket(@PathVariable Long basketId) {
        basketService.clearBasket(basketId);
        return ResponseEntity.ok("Panier vidé avec succès");
    }
    */
}
