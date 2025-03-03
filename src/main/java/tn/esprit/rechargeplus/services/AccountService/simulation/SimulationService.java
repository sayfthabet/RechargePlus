package tn.esprit.rechargeplus.services.AccountService.simulation;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulationService {
    private static final Logger logger = LoggerFactory.getLogger(SimulationService.class);

    private final OrderBook orderBook;
    private double referencePrice;
    private double profitLoss;
    private final String stockSymbol;

    // Caching variables
    private JSONObject cachedStockData;
    private long lastFetchTime = 0;
    private final long CACHE_DURATION_MS = 60 * 1000; // Cache for 1 minute

        // Alpha Vantage API key (replace with your actual API key)
    private final String apiKey = "N06MS9AIP5424YD3";

    public SimulationService(String stockSymbol, double referencePrice) {
        this.stockSymbol = stockSymbol;
        this.orderBook = new OrderBook();
        this.referencePrice = referencePrice;
        this.profitLoss = 0.0;
    }

    // Simulate placing a buy order
    public void simulateBuy(double price, int quantity) {
        Order buyOrder = new Order(OrderType.BUY, price, quantity);
        orderBook.addOrder(buyOrder);
        logger.info("Buy order placed: {}", buyOrder);
    }

    // Simulate placing a sell order
    public void simulateSell(double price, int quantity) {
        Order sellOrder = new Order(OrderType.SELL, price, quantity);
        orderBook.addOrder(sellOrder);
        logger.info("Sell order placed: {}", sellOrder);
    }

    // Trigger order matching
    public void simulateMatching() {
        logger.info("Matching orders...");
        orderBook.matchOrders();
    }

    // Display the order book
    public void displayOrderBook() {
        orderBook.display();
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    public double getReferencePrice() {
        return referencePrice;
    }

    public void setReferencePrice(double referencePrice) {
        this.referencePrice = referencePrice;
    }

    // Method to update P&L with mock price
    public void updateProfitLossMock(double currentPrice) {
        this.profitLoss = currentPrice - this.referencePrice;
        logger.info("Simulated Market Price for {}: ${}, Reference Price: ${}, Profit/Loss: ${}",
                stockSymbol, currentPrice, referencePrice, profitLoss);
    }

    // Generate a mock price based on previous price
    private double generateMockPrice(double previousPrice) {
        // Random change between -0.5% to +0.5%
        double changePercent = (Math.random() - 0.5) * 0.01;
        return previousPrice * (1 + changePercent);
    }

    /**
     * Main method demonstrating continuous simulation with mock data.
     */
    public static void main(String[] args) throws InterruptedException {
        String stockSymbol = "AAPL";
        double initialPrice = 100.0; // Default initial price

        // Initialize simulation with initial price
        SimulationService simulation = new SimulationService(stockSymbol, initialPrice);
        logger.info("Initial price for {}: ${}", stockSymbol, initialPrice);

        // Continuous simulation loop
        while (true) {
            try {
                // Generate new mock price
                double currentPrice = simulation.generateMockPrice(simulation.getReferencePrice());
                simulation.setReferencePrice(currentPrice);

                // Place orders around current price
                simulation.simulateBuy(currentPrice * 1.01, 10);
                simulation.simulateBuy(currentPrice * 1.02, 5);
                simulation.simulateSell(currentPrice * 0.99, 8);
                simulation.simulateSell(currentPrice * 0.98, 12);

                // Display order book and match orders
                simulation.displayOrderBook();
                simulation.simulateMatching();
                simulation.displayOrderBook();

                // Update and display P&L
                simulation.updateProfitLossMock(currentPrice);

                // Wait for 1 second before next iteration
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Simulation loop interrupted", e);
                break;
            } catch (Exception e) {
                logger.error("Error in simulation loop", e);
            }
        }
    }
}