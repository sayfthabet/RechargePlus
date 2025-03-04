package tn.esprit.rechargeplus.controllers.ProductController;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.Basket;
import tn.esprit.rechargeplus.entities.Basket_items;
import tn.esprit.rechargeplus.services.ProductService.IBasketService;
import tn.esprit.rechargeplus.services.ProductService.IPdfService;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@AllArgsConstructor
@RequestMapping("/basket")
public class BasketController {
    private static final Logger log = LoggerFactory.getLogger(BasketController.class);
    @Autowired
    IPdfService pdfService;

    @Autowired
    IBasketService basketService;

    @GetMapping("/getBaskets")
    public List<Basket> AllBaskets() {
        return basketService.getAllBaskets();
    }

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

    @GetMapping("/{id}/download-pdf")
    public ResponseEntity<byte[]> downloadBasketPdf(@PathVariable Long id) {
            log.info("Received request to generate PDF for basket ID: {}", id);
            try {
                Basket basket = new Basket(); // Récupérer le panier selon l'ID (à modifier)
                byte[] pdfBytes = pdfService.generateBasketPdf(basket);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "basket.pdf");

                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            } catch (Exception e) {
                log.error("Error generating PDF", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }
