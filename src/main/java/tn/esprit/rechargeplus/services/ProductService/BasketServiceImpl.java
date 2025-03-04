package tn.esprit.rechargeplus.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Basket;
import tn.esprit.rechargeplus.entities.Basket_items;
import tn.esprit.rechargeplus.entities.Product;
import tn.esprit.rechargeplus.repositories.ProductRepository.BasketRepository;
import tn.esprit.rechargeplus.repositories.ProductRepository.Basket_itemsRepository;
import tn.esprit.rechargeplus.repositories.ProductRepository.ProductRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BasketServiceImpl implements IBasketService {
    @Autowired
    BasketRepository basketRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    Basket_itemsRepository basket_itemsRepository;


    @Override
    public List<Basket> getAllBaskets() {
        return basketRepository.findAll();
    }
    @Override
    public Basket getBasketById(Long id) {
        return basketRepository.findById(id).orElse(null);
    }
    @Override
    public Basket addBasket(Basket basket) {
        return basketRepository.save(basket);
    }
    @Override
    public Basket updateBasket(Basket basket) {
        return basketRepository.save(basket);
    }
    @Override
    public void deleteBasket(Long id) {
        basketRepository.deleteById(id);
    }

    @Override
    public Basket addProductToBasket(Long basketId, Long productId, int quantity) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Vérifier le stock
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Stock insuffisant");
        }

        // Vérifier si le produit est déjà dans le panier
        Optional<Basket_items> existingItem = basket.getBasket_items().stream()
                .filter(item -> Objects.equals(item.getProduct().getIdProduct(), productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Si le produit est déjà dans le panier, on met à jour la quantité
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
            basketRepository.save(basket);
        } else {
            // Sinon, on ajoute un nouvel élément
            Basket_items newItem = new Basket_items();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setBasket(basket);
            basket.getBasket_items().add(newItem);
            basket_itemsRepository.save(newItem);
            basketRepository.save(basket);
        }

        // Mettre à jour le stock
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        return basketRepository.save(basket);    }


     @Override
    public Basket removeProductFromBasket(Long basketId, Long productId) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        Basket_items itemToRemove = basket.getBasket_items().stream()
                .filter(item -> Objects.equals(item.getProduct().getIdProduct(),productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Produit non trouvé dans le panier"));

        // Remettre la quantité dans le stock
        Product product = itemToRemove.getProduct();
        product.setQuantity(product.getQuantity() + itemToRemove.getQuantity());
        productRepository.save(product);

        // Supprimer l'élément du panier
        basket.getBasket_items().remove(itemToRemove);
        basket_itemsRepository.delete(itemToRemove);

        return basketRepository.save(basket);
    }

    @Override
    public List<Basket_items> getBasketItems(Long basketId) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));
        return basket.getBasket_items();
    }
/*
    @Override
    public void clearBasket(Long basketId) {
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        // Remettre tous les produits en stock
        for (Basket_items item : basket.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        // Supprimer tous les items du panier
        basket_itemsRepository.deleteAll(basket.getItems());
        basket.getItems().clear();
        basketRepository.save(basket);
        }

    }*/
}
