package org.reldb.rel.tests.ext_relvar.xls;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.reldb.rel.tests.helpers.XLSXTestHelper;

import java.io.FileOutputStream;
import java.io.IOException;

public class TestExternalRelvarXLSXDupCount extends XLSXTestHelper {
    @Before
    public void testXLS1() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet();
            XSSFRow row = null;
            XSSFCell cell = null;
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue("A");
            cell = row.createCell(1);
            cell.setCellValue("B");
            cell = row.createCell(2);
            cell.setCellValue("C");

            insert(1, sheet, row, cell, 1, 2, 3);
            insert(2, sheet, row, cell, 4, 5, 6);
            insert(3, sheet, row, cell, 4, 5, 6);
            insert(4, sheet, row, cell, 1, 2, 3);
            insert(5, sheet, row, cell, 7, 8, 9);
            insert(6, sheet, row, cell, 7, 8, 9);
            insert(7, sheet, row, cell, 4, 5, 6);

            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String src =
                "BEGIN;\n" +
                        "var myvar external xls \"" + file.getAbsolutePath().replace("\\", "\\\\") + "\" dup_count;" +
                        "END;\n" +
                        "true";
        testEquals("true", src);
    }

    @Test
    public void testXLSDupCount() {
        String src = "myvar";
        testEquals("RELATION {_DUP_COUNT INTEGER, A CHARACTER, B CHARACTER, C CHARACTER} {" +
                "\n\tTUPLE {_DUP_COUNT 2, A \"1\", B \"2\", C \"3\"}," +
                "\n\tTUPLE {_DUP_COUNT 3, A \"4\", B \"5\", C \"6\"}," +
                "\n\tTUPLE {_DUP_COUNT 2, A \"7\", B \"8\", C \"9\"}\n}", src);
    }
}
