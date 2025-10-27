package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Locator
    private By goToRegisterLink = By.xpath("/html/body/main/div/div/div/div/div/div[1]/div[2]");
    private By firstNameField   = By.xpath("/html/body/main/div/div/div/div/div/div[3]/div/div/form/div[1]/div/input[1]");
    private By lastNameField    = By.xpath("/html/body/main/div/div/div/div/div/div[3]/div/div/form/div[1]/div/input[2]");
    private By emailField       = By.xpath("/html/body/main/div/div/div/div/div/div[3]/div/div/form/div[2]/input");
    private By passwordField    = By.xpath("/html/body/main/div/div/div/div/div/div[3]/div/div/form/div[3]/input");
    private By dobField         = By.xpath("//input[@placeholder='dd / mm / yyy']");
    private By genderSelect     = By.xpath("//select[@name='gender']");
    private By policyCheckbox   = By.xpath("/html/body/main/div/div/div/div/div/div[3]/div/div/form/div[6]/label/span[1]/svg");
    private By registerButton   = By.xpath("//button[@type='submit']");
    private By messageBox       = By.xpath("//div[contains(@class,'errors') or contains(@class,'form-success')]");


    public void openRegisterPage() {
        wait.until(ExpectedConditions.elementToBeClickable(goToRegisterLink)).click();
    }


    private void selectDate(String dob) {
        try {
            WebElement dobInput = wait.until(ExpectedConditions.elementToBeClickable(dobField));
            dobInput.click();


            WebElement datepicker = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.react-datepicker")));

            String[] parts = dob.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);


            WebElement yearDropdown = datepicker.findElement(By.cssSelector("select.react-datepicker__year-select"));
            yearDropdown.click();
            yearDropdown.findElement(By.xpath("//option[@value='" + year + "']")).click();


            WebElement monthDropdown = datepicker.findElement(By.cssSelector("select.react-datepicker__month-select"));
            monthDropdown.click();
            monthDropdown.findElement(By.xpath("//option[@value='" + (month - 1) + "']")).click();


            WebElement dayElement = datepicker.findElement(By.xpath(
                    "//div[contains(@class,'react-datepicker__day') and not(contains(@class,'outside-month')) and text()='" + day + "']"));
            wait.until(ExpectedConditions.elementToBeClickable(dayElement)).click();

        } catch (Exception ex) {
            System.out.println("⚠️ Lỗi chọn ngày sinh: " + ex.getMessage());
        }
    }


    public String register(String firstName, String lastName, String email,
                           String password, String dob, String gender, String accept) {
        try {

            wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField)).sendKeys(firstName);
            driver.findElement(lastNameField).sendKeys(lastName);

            if (email != null && !email.trim().isEmpty()) {
                driver.findElement(emailField).sendKeys(email);
            }

            if (password != null && !password.trim().isEmpty()) {
                driver.findElement(passwordField).sendKeys(password);
            }


            if (dob != null && !dob.trim().isEmpty()) {
                selectDate(dob);
            }


            if (gender != null && !gender.trim().isEmpty()) {
                WebElement genderDropdown = driver.findElement(genderSelect);
                genderDropdown.sendKeys(gender);
            }

            if ("Có".equalsIgnoreCase(accept)) {
                WebElement checkbox = driver.findElement(policyCheckbox);
                checkbox.click();
            }


            driver.findElement(registerButton).click();


            WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(messageBox));
            return msg.getText();

        } catch (Exception e) {
            return "❌ Không có thông báo trả về (" + e.getMessage() + ")";
        }
    }
}
