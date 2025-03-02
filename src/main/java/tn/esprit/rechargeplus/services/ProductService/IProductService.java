package tn.esprit.rechargeplus.services.ProductService;
import tn.esprit.rechargeplus.entities.Basket_items;
import tn.esprit.rechargeplus.entities.Product;

import java.util.List;
public interface IProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product addProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Long id);
}
