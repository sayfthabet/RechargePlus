/*package tn.esprit.rechargeplus.services.ProductService;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockMarketService {

    @Scheduled(fixedRate = 30000)
    public Map<String, String> getStockPrices() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\seift\\Documents\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless"); // Remove this if elements are not found
        options.addArguments("--disable-gpu");
        WebDriver driver = new ChromeDriver(options);
        Map<String, String> stockPrices = new HashMap<>();

        try {
            driver.get("https://www.bvmt.com.tn/fr/market-place");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            List<WebElement> frames = driver.findElements(By.tagName("iframe"));
            if (!frames.isEmpty()) {
                System.out.println("Switching to iframe...");
                driver.switchTo().frame(frames.get(0));
            }

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ag-cell-last-left-pinned.ag-cell-value")));

            WebElement table = driver.findElement(By.cssSelector(".ag-cell-last-left-pinned.ag-cell-value"));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", table);
            Thread.sleep(2000); // Allow time for lazy loading

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ag-cell-last-left-pinned.ag-cell-value")));

            List<WebElement> stockNameElements = driver.findElements(
                    By.cssSelector(".ag-cell-last-left-pinned.ag-cell-value")
            ).stream()
                    .filter(element -> !element.getText().trim().isEmpty())
                    .collect(Collectors.toList());

            List<WebElement> stockPriceElements = driver.findElements(
                    By.cssSelector("[colid='limit.ask']")
            ).stream()
                    .filter(element -> !element.getText().trim().isEmpty())
                    .collect(Collectors.toList());


            //if (stockNameElements.size() == stockPriceElements.size() && !stockNameElements.isEmpty()) {
                for (int i = 0; i < stockNameElements.size(); i++) {
                    String stockName = stockNameElements.get(i).getText().trim();
                    String stockPrice = stockPriceElements.get(i).getText().trim();
                    stockPrices.put(stockName, stockPrice);
                }
           // } else {
               // System.err.println("Mismatch or No Data Found: Names = " + stockNameElements.size() + ", Prices = " + stockPriceElements.size());
           // }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return stockPrices;
    }
}*/
