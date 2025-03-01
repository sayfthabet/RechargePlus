package tn.esprit.rechargeplus.controllers;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.rechargeplus.entities.Product;
import tn.esprit.rechargeplus.services.IProductService;
import tn.esprit.rechargeplus.services.ProductServiceImpl;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/product")
public class ProductController {
    @Autowired
    IProductService productService;
    @GetMapping("/getProducts")
    public List<Product> AllProducts() {return productService.getAllProducts();}
    @PostMapping("/addProduct")
    public Product addProduct(@RequestBody Product product) {
    return productService.addProduct(product);
    }

    @DeleteMapping("/deleteProduct/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @PatchMapping("/updateProduct")
    public void updateProduct(@RequestBody Product product) {
        productService.updateProduct(product);
    }

    @GetMapping("/getProductById/{id}")
    public Product getProductById(@PathVariable Long id) {
      return  productService.getProductById(id);
    }
}
