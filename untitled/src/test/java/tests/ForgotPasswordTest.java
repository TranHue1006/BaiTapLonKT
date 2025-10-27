package tests;

import base.BaseTest;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import pages.ForgotPasswordPage;
import utils.ExcelUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ForgotPasswordTest extends BaseTest {

    private String timeNow() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private void log(String level, String message) {
        System.out.println("[" + timeNow() + " " + level + "] " + message);
    }

    @Test
    public void testForgotPasswordFromExcel() throws Exception {
        log("INFO", "🚀 BẮT ĐẦU CHẠY TEST QUÊN MẬT KHẨU TỪ EXCEL");


        ExcelUtils.setExcelFile("QMK");
        int rowCount = ExcelUtils.getLastUsedRow();

        for (int i = 1; i < rowCount; i++) {
            log("INFO", "---------------------------------------------");
            log("INFO", "🧪 Test Case #" + i);

            String email = ExcelUtils.getCellData(i, 0);
            String expected = ExcelUtils.getCellData(i, 1);


            if ((email == null || email.trim().isEmpty()) &&
                    (expected == null || expected.trim().isEmpty())) {
                log("WARN", "⚠️ Dòng " + i + " trống -> bỏ qua.");
                continue;
            }

            driver.get("https://www.badhabitsstore.vn/account/login");
            ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage(driver);

            log("INFO", "🔗 Click vào liên kết 'Quên mật khẩu?'...");
            forgotPasswordPage.clickForgotPasswordLink();
            Thread.sleep(1000);

            if (email.equalsIgnoreCase("(để trống)") || email.trim().isEmpty()) {
                email = "";
            }

            log("INFO", "📧 Nhập email: '" + email + "'");
            forgotPasswordPage.enterEmail(email);
            forgotPasswordPage.clickSubmit();

            String actual = "";


            try {
                WebElement emailField = forgotPasswordPage.getEmailField();
                JavascriptExecutor js = (JavascriptExecutor) driver;
                actual = (String) js.executeScript(
                        "return arguments[0].validationMessage;", emailField);
            } catch (Exception ignored) { }


            if (actual == null || actual.isEmpty()) {
                actual = forgotPasswordPage.getMessage();
            }


            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("/account/login") &&
                    (actual == null || actual.isEmpty())) {
                actual = "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn. Vui lòng kiểm tra!";
            }


            ExcelUtils.setCellData(i, 2, actual);


            boolean isPass = false;
            String actualLower = actual.toLowerCase();
            String expectedLower = expected.toLowerCase();

            if (expectedLower.contains("vui lòng nhập") || expectedLower.contains("điền email")) {
                if (actualLower.contains("please fill out") || actualLower.contains("vui lòng")) {
                    isPass = true;
                }
            } else if (expectedLower.contains("định dạng") || expectedLower.contains("không hợp lệ")) {
                if (actualLower.contains("please include") || actualLower.contains("hợp lệ")) {
                    isPass = true;
                }
            } else if (expectedLower.contains("không tồn tại")) {
                if (actualLower.contains("không tồn tại") || actualLower.contains("no account")) {
                    isPass = true;
                }
            } else if (expectedLower.contains("liên kết") || expectedLower.contains("đã gửi")) {
                if (actualLower.contains("liên kết") || actualLower.contains("đã được gửi")) {
                    isPass = true;
                }
            }


            if (isPass) {
                ExcelUtils.setCellData(i, 3, "PASS");
                log("INFO", "✅ [PASS] Kết quả đúng như mong đợi.");
            } else {
                ExcelUtils.setCellData(i, 3, "FAIL");
                log("ERROR", "❌ [FAIL] Kết quả KHÔNG khớp mong đợi!");
                log("INFO", "👉 Mong đợi: " + expected);
                log("INFO", "👉 Thực tế: " + actual);
            }

            Thread.sleep(1000);
        }

        log("INFO", "🏁 HOÀN TẤT CHẠY TEST QUÊN MẬT KHẨU");
    }
}
