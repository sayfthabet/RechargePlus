package tn.esprit.rechargeplus.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.rechargeplus.services.StockMarketService;

import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockMarketController {

    @Autowired
    private StockMarketService stockMarketService;

    @GetMapping("/prices")
    public Map<String, String> getStockPrices() {
        return stockMarketService.getStockPrices();
    }
}
