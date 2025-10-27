package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SearchPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By searchInput = By.name("q");
    private By searchButton = By.cssSelector("button[type='submit']");
    private By productItem = By.cssSelector(".product-item");
    private By resultMessage = By.cssSelector("main"); // phần text lớn trả về

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(7));
    }


    public String search(String keyword) {
        WebElement input = driver.findElement(searchInput);
        input.clear();


        if (keyword == null || keyword.trim().isEmpty()) {
            driver.findElement(searchButton).click();
            return getValidationMessage(input); // lấy thông báo HTML5
        }

        input.sendKeys(keyword);
        driver.findElement(searchButton).click();

        return getResultText();
    }


    private String getValidationMessage(WebElement input) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript("return arguments[0].validationMessage;", input);
    }


    public String getResultText() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(resultMessage));

            String pageText = driver.findElement(resultMessage).getText();


            if (driver.findElements(productItem).size() > 0) {
                String[] lines = pageText.split("\n");
                StringBuilder result = new StringBuilder();

                boolean startCollect = false;
                for (String line : lines) {
                    if (line.toUpperCase().contains("BADHABITS") || line.toUpperCase().contains("ÁO") || line.matches(".*\\d{1,3}(\\.\\d{3})*₫.*")) {
                        startCollect = true;
                    }
                    if (startCollect) {
                        result.append(line).append("\n");
                    }
                }
                return result.toString().trim();
            }


            if (pageText.toUpperCase().contains("KHÔNG TÌM THẤY")) {
                String[] lines = pageText.split("\n");
                StringBuilder result = new StringBuilder();

                for (String line : lines) {
                    if (line.toUpperCase().contains("KHÔNG TÌM THẤY") || line.contains("Vui lòng")) {
                        result.append(line).append("\n");
                    }
                }
                return result.toString().trim();
            }

            return pageText.trim();

        } catch (Exception e) {
            return "Không tìm thấy sản phẩm";
        }
    }
}
