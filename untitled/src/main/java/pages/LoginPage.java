package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {
    private WebDriver driver;


    private By emailOrPhoneInput = By.xpath("//input[@type='email' or @type='text']");
    private By continueButton = By.xpath("//button[contains(text(),'Tiếp tục') or @type='submit']");


    private By errorMessage = By.xpath(
            "//p[contains(text(),'Email không hợp lệ') or contains(text(),'không hợp lệ') " +
                    "or contains(text(),'Email') or contains(text(),'điện thoại')]" +
                    "|//div[contains(text(),'Email không hợp lệ') or contains(text(),'Email') or contains(text(),'điện thoại')]" +
                    "|//*[contains(@class,'error') and contains(text(),'Email')]"
    );

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }


    public void enterEmailOrPhone(String value) {
        WebElement input = driver.findElement(emailOrPhoneInput);
        input.clear();
        input.sendKeys(value);
    }


    public boolean isContinueButtonEnabled() {
        try {
            WebElement button = driver.findElement(continueButton);
            return button.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }


    public String getErrorMessage() {
        try {
            WebElement error = driver.findElement(errorMessage);
            if (error != null && error.isDisplayed()) {
                return error.getText().trim();
            }
        } catch (Exception e) {

        }
        return "";
    }


    public String getResultText() {
        try {
            boolean isEnabled = isContinueButtonEnabled();

            if (!isEnabled) {

                String msg = getErrorMessage();
                return msg.isEmpty() ? "Nút Tiếp tục bị vô hiệu hóa (Email/SĐT không hợp lệ)" : msg;
            }


            return "Trường Email/SĐT hợp lệ (nút Tiếp tục được kích hoạt)";
        } catch (Exception e) {
            return "Không thể xác định trạng thái: " + e.getMessage();
        }
    }
}
