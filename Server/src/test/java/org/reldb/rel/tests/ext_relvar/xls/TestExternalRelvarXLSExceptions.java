package org.reldb.rel.tests.ext_relvar.xls;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.tests.helpers.XLTestHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestExternalRelvarXLSExceptions extends XLTestHelper {

    private File NonExistingFile = new File("test2.xls");

    public TestExternalRelvarXLSExceptions() {
        super("test.xls");
    }

    @Before
    public void testXLS1() {
        try {
            try (HSSFWorkbook workbook = new HSSFWorkbook()) {
                workbook.createSheet();
                file.createNewFile();
                try (FileOutputStream out = new FileOutputStream(file)) {
                    workbook.write(out);
                }
                NonExistingFile.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String src =
                "BEGIN;\n" +
                        "var myvar external xls \"" + file.getAbsolutePath().replace("\\", "\\\\") + "\" dup_remove;" +
                        "END;\n" +
                        "true";
        testEquals("true", src);
    }

    @Test //Relvar created from empty file
    public void testXLS2() {
        String src = "myvar";
        testEquals("RELATION {} {\n}", src);
    }

    @Test(expected = ExceptionSemantic.class)
    public void testXLS3() { //Calling relvar after manually deleting file
        file.delete();
        String src = "myvar";
        testEvaluate(src);
    }

    @Test(expected = ExceptionSemantic.class)
    public void testXLS4() { //Creating relvar from non-existing file
        String src =
                "BEGIN;\n" +
                        "var brokenVAR external xls \"" + NonExistingFile.getAbsolutePath().replace("\\", "\\\\") + "\" dup_remove;" +
                        "END;\n";
        testEvaluate(src);
    }

    @Test(expected = ExceptionSemantic.class)
    public void testXLS5() { //Creating relvar with non-identified duplicate handling method
        String src =
                "BEGIN;\n" +
                        "var brokenVAR external xls \"" + file.getAbsolutePath().replace("\\", "\\\\") + "\" something;" +
                        "END;\n";
        testEvaluate(src);
    }

    @After
    public void testXLS10() { //Drop relvar and delete test file
        String src =
                "BEGIN;\n" +
                        "drop var myvar;" +
                        "END;\n" +
                        "true";
        file.delete();
        testEquals("true", src);
    }
}
