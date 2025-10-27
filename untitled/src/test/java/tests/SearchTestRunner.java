package tests;

import base.BaseTest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import pages.SearchPage;
import utils.ExcelUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchTestRunner extends BaseTest {

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
    public void testSearchFromExcel() throws Exception {
        log("INFO", "🚀 BẮT ĐẦU CHẠY TEST TÌM KIẾM TỪ EXCEL");

        ExcelUtils.setExcelFile("TK"); // Sheet tên "TK"
        int rowCount = ExcelUtils.getLastUsedRow();
        SearchPage searchPage = new SearchPage(driver);

        for (int i = 1; i < rowCount; i++) {
            log("INFO", "---------------------------------------------");
            log("INFO", "🧪 Test Case #" + i);

            String keyword = ExcelUtils.getCellData(i, 0);
            String expected = ExcelUtils.getCellData(i, 1);

            // Bỏ qua dòng trống
            if ((keyword == null || keyword.trim().isEmpty()) &&
                    (expected == null || expected.trim().isEmpty())) {
                log("WARN", "⚠️ Dòng " + i + " trống -> bỏ qua.");
                continue;
            }

            log("INFO", "🌐 Mở trang chủ BadHabits...");
            driver.get("https://www.badhabitsstore.vn/");
            Thread.sleep(2000);

            log("INFO", "🔎 Từ khóa tìm kiếm: '" + keyword + "'");

            String actual = "";
            try {
                actual = searchPage.search(keyword);
                log("INFO", "📄 Kết quả thực tế: " + actual);
            } catch (Exception e) {
                actual = "Lỗi khi tìm kiếm";
                log("ERROR", "⚠️ Gặp lỗi khi thao tác tìm kiếm: " + e.getMessage());
            }


            ExcelUtils.setCellData(i, 2, actual);


            boolean isPass = false;
            String actualLower = actual.toLowerCase().trim();
            String expectedLower = expected.toLowerCase().trim();

            if (expectedLower.contains("hiển thị danh sách sản phẩm") ||
                    expectedLower.contains("hiển thị sản phẩm")) {

                
                if (!actualLower.contains("không tìm thấy") &&
                        !actualLower.contains("vui lòng") &&
                        !actualLower.contains("please fill out") &&
                        !actualLower.contains("lỗi")) {
                    isPass = true;
                }

            } else if (expectedLower.contains("không tìm thấy")) {
                if (actualLower.contains("không tìm thấy")) isPass = true;

            } else if (expectedLower.contains("vui lòng") ||
                    expectedLower.contains("nhập từ khóa") ||
                    expectedLower.contains("trống")) {

                if (actualLower.contains("vui lòng") || actualLower.contains("please fill out")) {
                    if (expectedLower.contains("please fill out")) {
                        isPass = actualLower.contains("please fill out");
                    } else if (expectedLower.contains("vui lòng")) {
                        isPass = actualLower.contains("vui lòng");
                    }
                }

            } else {
                isPass = false;
            }


            if (isPass) {
                ExcelUtils.setCellData(i, 3, "PASS");
                ExcelUtils.setCellData(i, 4, "");
                log("INFO", "✅ [PASS] Kết quả đúng như mong đợi.");
            } else {
                ExcelUtils.setCellData(i, 3, "FAIL");
                log("ERROR", "❌ [FAIL] Kết quả KHÔNG khớp mong đợi!");

                String screenshotPath = takeScreenshot("SearchCase_" + i);
                ExcelUtils.setCellData(i, 4, "");
                ExcelUtils.insertImage(i, 4, screenshotPath);
            }

            Thread.sleep(1000);
        }

        log("INFO", "🏁 HOÀN TẤT CHẠY TEST TÌM KIẾM");
    }

    @AfterClass
    public void tearDownExcel() throws Exception {
        ExcelUtils.closeWorkbook();
    }
}
