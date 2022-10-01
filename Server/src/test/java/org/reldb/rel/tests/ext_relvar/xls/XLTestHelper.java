package org.reldb.rel.tests.ext_relvar.xls;

import org.reldb.rel.tests.BaseOfTest;

import java.io.File;

public class XLTestHelper extends BaseOfTest {
    protected String path;
    protected File file;

    protected XLTestHelper(String path) {
        this.path = path;
        file = new File(path);
    }
}
