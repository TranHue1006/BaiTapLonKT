package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.util.IOUtils;

import java.io.*;

public class ExcelUtils {
    private static XSSFWorkbook workbook;
    private static XSSFSheet sheet;
    private static final String filePath = "TestChucNang.xlsx"; // Đường dẫn file Excel


    public static void setExcelFile(String sheetName) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet(sheetName);
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
        FileInputStream fis = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fis);
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }
        fis.close();
    }


    public static String getCellData(int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) return "";
        Cell cell = row.getCell(colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }


    public static void setCellData(int rowNum, int colNum, String value) throws IOException {
        if (sheet == null) throw new IllegalStateException("Chưa khởi tạo sheet.");
        Row row = sheet.getRow(rowNum);
        if (row == null) row = sheet.createRow(rowNum);

        Cell cell = row.getCell(colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(value);

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            fos.flush();
        }
    }


    public static void insertImage(int rowNum, int colNum, String imagePath) throws IOException {
        if (sheet == null) throw new IllegalStateException("Chưa khởi tạo sheet.");
        if (!new File(imagePath).exists()) {
            System.out.println("⚠ Không tìm thấy ảnh: " + imagePath);
            return;
        }


        InputStream inputStream = new FileInputStream(imagePath);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        inputStream.close();


        XSSFDrawing drawing = sheet.createDrawingPatriarch();


        XSSFClientAnchor anchor = new XSSFClientAnchor();
        anchor.setCol1(colNum);
        anchor.setRow1(rowNum);
        anchor.setCol2(colNum + 1);
        anchor.setRow2(rowNum + 1);
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

        drawing.createPicture(anchor, pictureIdx);


        Row row = sheet.getRow(rowNum);
        if (row == null) row = sheet.createRow(rowNum);
        row.setHeightInPoints(90); // cao hơn để ảnh rõ hơn
        sheet.setColumnWidth(colNum, 25 * 256); // rộng hơn cho vừa ảnh


        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            fos.flush();
        }
    }


    public static int getRowCount() {
        return sheet.getPhysicalNumberOfRows();
    }


    public static int getLastUsedRow() {
        int lastRowNum = sheet.getLastRowNum();
        while (lastRowNum >= 0) {
            Row row = sheet.getRow(lastRowNum);
            if (row != null) {
                for (Cell cell : row) {
                    if (!new DataFormatter().formatCellValue(cell).trim().isEmpty()) {
                        return lastRowNum + 1;
                    }
                }
            }
            lastRowNum--;
        }
        return 0;
    }


    public static void closeWorkbook() throws IOException {
        if (workbook != null) {
            workbook.close();
            workbook = null;
            sheet = null;
        }
    }
}
