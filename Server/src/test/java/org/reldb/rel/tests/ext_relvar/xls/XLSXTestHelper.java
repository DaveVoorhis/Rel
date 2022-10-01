package org.reldb.rel.tests.ext_relvar.xls;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class XLSXTestHelper extends XLTestHelperWithDeleteAfter {

    protected XLSXTestHelper() {
        super("test.xlsx");
    }

    protected static void insert(int rowNum, XSSFSheet sheet, XSSFRow row, XSSFCell cell, int arg0, int arg1, int arg2) {
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue(arg0);
        cell = row.createCell(1);
        cell.setCellValue(arg1);
        cell = row.createCell(2);
        cell.setCellValue(arg2);
    }
}
