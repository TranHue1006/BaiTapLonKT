package tests;

import base.BaseTest;
import org.testng.annotations.Test;
import pages.RegisterPage;
import utils.ExcelUtils;

public class RegisterTestRunner extends BaseTest {

    @Test
    public void testRegisterFromExcel() throws Exception {
        // Mở file Excel sheet "ĐK"
        ExcelUtils.setExcelFile("ĐK");

        int rowCount = ExcelUtils.getRowCount();
        RegisterPage registerPage = new RegisterPage(driver);

        for (int i = 1; i < rowCount; i++) { // bỏ tiêu đề
            String ho = ExcelUtils.getCellData(i, 0);
            String ten = ExcelUtils.getCellData(i, 1);
            String email = ExcelUtils.getCellData(i, 2);
            String password = ExcelUtils.getCellData(i, 3);
            String birthday = ExcelUtils.getCellData(i, 4);
            String gender = ExcelUtils.getCellData(i, 5);
            String accept = ExcelUtils.getCellData(i, 6);
            String expected = ExcelUtils.getCellData(i, 7);

            driver.get("https://www.badhabitsstore.vn/account/login");
            registerPage.openRegisterPage();

            String actual = registerPage.register(ho, ten, email, password, birthday, gender, accept);


            ExcelUtils.setCellData(i, 8, actual);
            if (actual.contains(expected)) {
                ExcelUtils.setCellData(i, 9, "PASS");
            } else {
                ExcelUtils.setCellData(i, 9, "FAIL");
            }

            System.out.println("Row " + i + " | Expected: " + expected + " | Actual: " + actual);
        }
    }
}
