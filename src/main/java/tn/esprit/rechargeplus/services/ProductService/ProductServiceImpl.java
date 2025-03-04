package tn.esprit.rechargeplus.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Product;
import tn.esprit.rechargeplus.repositories.ProductRepository.ProductRepository;
import java.util.List;
@Service
@AllArgsConstructor
public class ProductServiceImpl implements IProductService{
    @Autowired
    ProductRepository productRepository;
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
    @Override
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }
    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }
    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
