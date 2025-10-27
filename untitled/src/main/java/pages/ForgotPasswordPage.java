package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ForgotPasswordPage {
    private WebDriver driver;


    private By forgotPasswordLink = By.linkText("Quên mật khẩu?");
    private By emailField = By.xpath("/html/body/main/div/div/div/div/div/div[2]/div/div[2]/form/div[1]/input");
    private By submitButton = By.xpath("/html/body/main/div/div/div/div/div/div[2]/div/div[2]/form/div[2]/button");
    private By messageBox = By.cssSelector(".form-message, .errors, .note.form-success");

    public ForgotPasswordPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickForgotPasswordLink() {
        driver.findElement(forgotPasswordLink).click();
    }

    public void enterEmail(String email) {
        WebElement emailInput = driver.findElement(emailField);
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    public void clickSubmit() {
        driver.findElement(submitButton).click();
    }

    public WebElement getEmailField() {
        return driver.findElement(emailField);
    }

    public String getMessage() {
        try {
            return driver.findElement(messageBox).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
