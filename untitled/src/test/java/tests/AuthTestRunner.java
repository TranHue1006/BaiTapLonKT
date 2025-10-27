package tests;

import base.BaseTest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.ExcelUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthTestRunner extends BaseTest {

    private String timeNow() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private void log(String level, String message) {
        System.out.println("[" + timeNow() + " " + level + "] " + message);
    }


    private String takeScreenshot(String testName) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String folderPath = "screenshots";
            File destFolder = new File(folderPath);
            if (!destFolder.exists()) destFolder.mkdirs();

            String filePath = folderPath + "/" + testName + "_" + timestamp + ".png";
            FileHandler.copy(src, new File(filePath));
            log("INFO", "📸 Đã chụp ảnh lỗi: " + filePath);
            return filePath;
        } catch (Exception e) {
            log("ERROR", "Không thể chụp ảnh lỗi: " + e.getMessage());
            return "";
        }
    }

    @Test
    public void testLoginStep1_FromExcel() throws Exception {
        log("INFO", "🚀 BẮT ĐẦU TEST FORM NHẬP EMAIL/SỐ ĐIỆN THOẠI ");

        ExcelUtils.setExcelFile("Sheet2");
        int rowCount = ExcelUtils.getRowCount();
        LoginPage loginPage = new LoginPage(driver);

        for (int i = 1; i < rowCount; i++) {
            log("INFO", "------------------------------------------");
            log("INFO", "🧪 Test Case #" + i);

            String emailOrPhone = ExcelUtils.getCellData(i, 1);
            String expected = ExcelUtils.getCellData(i, 2);

            driver.manage().deleteAllCookies();
            driver.get("https://www.badhabitsstore.vn/account/login");
            Thread.sleep(2000);

            String actual;

            try {
                loginPage.enterEmailOrPhone(emailOrPhone);
                Thread.sleep(1000);
                actual = loginPage.getResultText();
                log("INFO", "📋 Kết quả thực tế: " + actual);
            } catch (Exception e) {
                actual = "Không thể thao tác: " + e.getMessage();
                log("ERROR", actual);
            }

            ExcelUtils.setCellData(i, 3, actual);

            boolean isPass = actual != null &&
                    (actual.equalsIgnoreCase(expected)
                            || expected.contains(actual)
                            || actual.contains(expected));

            if (isPass) {
                ExcelUtils.setCellData(i, 4, "PASS");
                ExcelUtils.setCellData(i, 5, "");
                log("INFO", "✅ [PASS] Kết quả đúng như mong đợi.");
            } else {
                ExcelUtils.setCellData(i, 4, "FAIL");
                log("ERROR", "❌ [FAIL] Kết quả KHÔNG khớp mong đợi!");
                log("DEBUG", "Expected: " + expected);
                log("DEBUG", "Actual: " + actual);

                String screenshotPath = takeScreenshot("TestCase_" + i);
                ExcelUtils.setCellData(i, 5, "");
                ExcelUtils.insertImage(i, 5, screenshotPath);
            }
        }

        log("INFO", "🏁 HOÀN TẤT KIỂM THỬ FORM EMAIL/SĐT ");
    }

    @AfterClass
    public void tearDownExcel() throws Exception {
        ExcelUtils.closeWorkbook();
    }
}
