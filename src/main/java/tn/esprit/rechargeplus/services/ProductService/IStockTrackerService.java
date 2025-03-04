package tn.esprit.rechargeplus.services.ProductService;


public interface IStockTrackerService {

    public void initializeStock();
    public int getInitialQuantity(Long productId);
    public void updateInitialQuantity(Long productId, int quantity);
    public void checkLowStock();
}
