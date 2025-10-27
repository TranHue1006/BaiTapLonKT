package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class CartTest {
    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        System.out.println("🚀 Khởi tạo ChromeDriver thành công!");
    }

    @Test
    public void testAddToCartFlow() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        driver.get("https://badhabitsstore.vn/");
        System.out.println("✅ Mở trang chủ thành công");


        try {
            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@placeholder='TÌM KIẾM' or @type='search' or contains(@class,'search__input')]")));
            searchBox.sendKeys("badhabits");
            searchBox.sendKeys(Keys.ENTER);
            System.out.println("✅ Đã nhập từ khóa 'badhabits' và nhấn Enter");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("❌ Không tìm thấy ô tìm kiếm: " + e.getMessage());
            return;
        }


        try {
            WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//a[contains(@href,'/products/')])[1]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", firstProduct);
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstProduct);
            System.out.println("✅ Đã click vào sản phẩm đầu tiên");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("❌ Không click được sản phẩm đầu tiên: " + e.getMessage());
            return;
        }


        try {
            WebElement sizeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'S')] | //label[contains(text(),'S')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", sizeBtn);
            System.out.println("✅ Chọn size S thành công");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("⚠️ Không tìm thấy nút chọn size (bỏ qua bước này)");
        }


        try {
            WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/main/div[2]/div[2]/div/div[2]/div/div/div[7]/a")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartBtn);
            System.out.println("✅ Đã nhấn 'Thêm vào giỏ hàng'");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("❌ Không nhấn được nút 'Thêm vào giỏ hàng': " + e.getMessage());
            return;
        }


        driver.get("https://badhabitsstore.vn/cart");
        System.out.println("🛒 Đang mở trang giỏ hàng...");
        Thread.sleep(2000);


        List<WebElement> items = driver.findElements(
                By.xpath("//div[contains(@class,'cart__row') or contains(@class,'cart-item')]"));

        if (!items.isEmpty()) {
            System.out.println("✅ Sản phẩm đã có trong giỏ hàng");
        } else {
            System.out.println("❌ Không thấy sản phẩm trong giỏ hàng!");
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("🧹 Đóng trình duyệt hoàn tất.");
        }
    }
}
