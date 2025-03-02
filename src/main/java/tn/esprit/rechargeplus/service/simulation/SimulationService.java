package tn.esprit.rechargeplus.service.simulation;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

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
    private final String apiKey = "YOUR_API_KEY";

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

    /**
     * Updates profit or loss based on the real-time market price fetched from Alpha Vantage.
     */
    public void updateProfitLoss() {
        try {
            JSONObject stockData = getCachedStockData(stockSymbol, 3);
            if (stockData != null) {
                // Alpha Vantage returns JSON with structure: {"Global Quote": {"05. price": "..." ... }}
                JSONObject globalQuote = stockData.getJSONObject("Global Quote");
                String priceStr = globalQuote.getString("05. price");
                double currentMarketPrice = Double.parseDouble(priceStr);
                double delta = currentMarketPrice - referencePrice;
                profitLoss = delta;
                logger.info("Real Market Price for {}: ${}, Reference Price: ${}, Profit/Loss: ${}",
                        stockSymbol, currentMarketPrice, referencePrice, profitLoss);
            }
        } catch (Exception e) {
            logger.error("Failed to fetch market data for " + stockSymbol, e);
        }
    }

    public double getProfitLoss() {
        return profitLoss;
    }

    /**
     * Fetches and caches stock data using Alpha Vantage with retry logic.
     *
     * @param symbol     The stock symbol.
     * @param maxRetries Maximum number of retries.
     * @return JSONObject containing the stock data.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    private JSONObject getCachedStockData(String symbol, int maxRetries) throws IOException, InterruptedException {
        long now = System.currentTimeMillis();
        if (cachedStockData != null && (now - lastFetchTime) < CACHE_DURATION_MS) {
            return cachedStockData;
        }
        cachedStockData = getStockDataWithRetry(symbol, maxRetries);
        lastFetchTime = now;
        return cachedStockData;
    }

    private JSONObject getStockDataWithRetry(String symbol, int maxRetries) throws IOException, InterruptedException {
        int retryCount = 0;
        long waitTime = 5000; // Start with 5 seconds
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
                + symbol + "&apikey=" + apiKey;
        while (retryCount < maxRetries) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                if (json.has("Global Quote") && !json.getJSONObject("Global Quote").isEmpty()) {
                    return json;
                }
            } else if (response.statusCode() == 429) {
                logger.warn("Received 429 error. Retrying in {} ms...", waitTime);
                Thread.sleep(waitTime);
                waitTime *= 2; // Exponential backoff
            } else {
                logger.error("Received error status: " + response.statusCode());
                break;
            }
            retryCount++;
        }
        throw new IOException("Failed to fetch stock data after " + maxRetries + " attempts.");
    }

    /**
     * Main method demonstrating the simulation using real-time Alpha Vantage data.
     */
    public static void main(String[] args) throws InterruptedException {
        String stockSymbol = "AAPL";
        double initialPrice = 0.0;
        SimulationService simulation = new SimulationService(stockSymbol, 0);
        try {
            JSONObject stockData = simulation.getStockDataWithRetry(stockSymbol, 3);
            JSONObject globalQuote = stockData.getJSONObject("Global Quote");
            initialPrice = Double.parseDouble(globalQuote.getString("05. price"));
            logger.info("Initial price for {}: ${}", stockSymbol, initialPrice);
        } catch (Exception e) {
            logger.error("Failed to fetch initial market data for " + stockSymbol, e);
            initialPrice = 100.0; // Fallback value
        }
        // Reinitialize simulation with proper initial price
        simulation = new SimulationService(stockSymbol, initialPrice);

        // Simulate order placements based on the real stock price
        simulation.simulateBuy(initialPrice * 1.01, 10);
        simulation.simulateBuy(initialPrice * 1.02, 5);
        simulation.simulateSell(initialPrice * 0.99, 8);
        simulation.simulateSell(initialPrice * 0.98, 12);

        // Display the order book before matching
        simulation.displayOrderBook();

        // Match orders
        simulation.simulateMatching();
        simulation.displayOrderBook();

        // Update profit/loss based on current market price
        simulation.updateProfitLoss();

        // Pause and simulate additional orders
        Thread.sleep(2000);
        simulation.simulateBuy(initialPrice * 1.03, 15);
        simulation.simulateSell(initialPrice * 0.97, 10);
        simulation.displayOrderBook();
        simulation.simulateMatching();
        simulation.displayOrderBook();
        simulation.updateProfitLoss();
    }
}
