package tn.esprit.rechargeplus.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.rechargeplus.entities.Product;
import tn.esprit.rechargeplus.repositories.ProductRepository.ProductRepository;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockTrackerService implements IStockTrackerService {
    private final Map<Long, Integer> initialStockMap = new HashMap<>();
    private final ProductRepository productRepository;

    private final NotificationService notificationService;
    @Autowired
    public StockTrackerService(ProductRepository productRepository, NotificationService notificationService) {
        this.notificationService = notificationService;
        this.productRepository = productRepository;
    }

    @Override
    @PostConstruct
    public void initializeStock() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            initialStockMap.putIfAbsent(product.getIdProduct(), product.getQuantity());
        }
    }

    @Override
    public int getInitialQuantity(Long productId) {
        return initialStockMap.getOrDefault(productId, 0);
    }

    public void updateInitialQuantity(Long productId, int quantity) {
        initialStockMap.put(productId, quantity);
    }


    @Scheduled(fixedRate = 60000) // Runs every minute
    public void checkLowStock() {
        List<Product> lowStockProducts = productRepository.findAll().stream()
                .filter(p -> {
                    int initialQty = getInitialQuantity(p.getIdProduct());
                    return initialQty > 0 && p.getQuantity() < (0.1 * initialQty);
                })
                .collect(Collectors.toList());

        for (Product product : lowStockProducts) {
            notificationService.notifyUser(product);
        }
    }
}
