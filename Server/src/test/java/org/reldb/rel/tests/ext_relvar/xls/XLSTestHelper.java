package org.reldb.rel.tests.ext_relvar.xls;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class XLSTestHelper extends XLTestHelperWithDeleteAfter {

    protected XLSTestHelper() {
        super("test.xls");
    }

    protected static void insert(int rowNum, HSSFSheet sheet, HSSFRow row, HSSFCell cell, int arg0, int arg1, int arg2) {
        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue(arg0);
        cell = row.createCell(1);
        cell.setCellValue(arg1);
        cell = row.createCell(2);
        cell.setCellValue(arg2);
    }
}
