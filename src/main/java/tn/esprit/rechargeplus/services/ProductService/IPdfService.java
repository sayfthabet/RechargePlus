package tn.esprit.rechargeplus.services.ProductService;

import tn.esprit.rechargeplus.entities.Basket;

public interface IPdfService {
    byte[] generateBasketPdf(Basket basket);
}
