package org.reldb.rel.tests.ext_relvar.xls;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;

public class TestExternalRelvarXLSXDupRemove extends XLSXTestHelper {
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
                        "var myvar external xls \"" + file.getAbsolutePath().replace("\\", "\\\\") + "\" dup_remove;" +
                        "END;\n" +
                        "true";
        testEquals("true", src);
    }

    @Test
    public void testXLSDupRemove() {
        String src = "myvar";
        testEquals("RELATION {A CHARACTER, B CHARACTER, C CHARACTER} {" +
                "\n\tTUPLE {A \"1\", B \"2\", C \"3\"}," +
                "\n\tTUPLE {A \"4\", B \"5\", C \"6\"}," +
                "\n\tTUPLE {A \"7\", B \"8\", C \"9\"}\n}", src);
    }
}
