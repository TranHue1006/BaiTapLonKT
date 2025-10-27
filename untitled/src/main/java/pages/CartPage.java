package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class CartPage {
    WebDriver driver;

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public void searchProduct(String keyword) {
        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.sendKeys(keyword);
        searchBox.submit();
    }

    public void selectFirstProduct() {
        List<WebElement> products = driver.findElements(By.cssSelector(".product-item"));
        if (!products.isEmpty()) {
            products.get(0).click();
        }
    }

    public void addToCart() {
        WebElement addButton = driver.findElement(By.cssSelector("button[name='add']"));
        addButton.click();
    }

    public boolean verifyAdded() {
        try {
            WebElement popup = driver.findElement(By.cssSelector(".drawer__title"));
            return popup.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
