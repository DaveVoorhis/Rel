package org.reldb.rel.plugins.tests.XLS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestExternalRelvarXLSX3 extends BaseOfTest {
	
	private final String path = "Database"+File.separator+"test.xlsx";
	private File file = new File(path);

	private static void insert(int rowNum, XSSFSheet sheet, XSSFRow row, XSSFCell cell, int arg0, int arg1, int arg2) {
		row = sheet.createRow(rowNum);
        cell = row.createCell(0);
		cell.setCellValue(arg0);
		cell = row.createCell(1);
		cell.setCellValue(arg1);
		cell = row.createCell(2);
		cell.setCellValue(arg2);
	}
	
	@Before
	public void testXLS1() {
		XSSFWorkbook workbook = new XSSFWorkbook();
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
		
		insert(1,sheet,row,cell,1,2,3);
		insert(2,sheet,row,cell,4,5,6);
		insert(3,sheet,row,cell,4,5,6);
		insert(4,sheet,row,cell,1,2,3);
		insert(5,sheet,row,cell,7,8,9);
		insert(6,sheet,row,cell,7,8,9);
		insert(7,sheet,row,cell,4,5,6);
        
		try {
			FileOutputStream out = 
		            new FileOutputStream(file);
		    workbook.write(out);
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String src = 
				"BEGIN;\n" +
						"var myvar external xls \"" + file.getAbsolutePath() + "\" dup_count;" +
				"END;\n" +
				"true";
		testEquals("true", src);
	}
	
	@Test
	public void testXLS2() {
		String src = "myvar";		
		testEquals(	"RELATION {DUP_COUNT INTEGER, A RATIONAL, B RATIONAL, C RATIONAL} {" +
				"\n\tTUPLE {DUP_COUNT 2, A 1.0, B 2.0, C 3.0}," +
				"\n\tTUPLE {DUP_COUNT 3, A 4.0, B 5.0, C 6.0}," +
				"\n\tTUPLE {DUP_COUNT 2, A 7.0, B 8.0, C 9.0}\n}", src);
	}
	
	@After
	public void testXLS3() {
		String src = 
				"BEGIN;\n" +
						"drop var myvar;" +
				"END;\n" +
				"true";
		file.delete();
		testEquals("true", src);
	}
}
