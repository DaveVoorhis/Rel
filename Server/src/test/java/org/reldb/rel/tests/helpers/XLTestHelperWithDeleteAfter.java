package org.reldb.rel.tests.helpers;

import org.junit.After;

public class XLTestHelperWithDeleteAfter extends XLTestHelper {

    protected XLTestHelperWithDeleteAfter(String path) {
        super(path);
    }

    @After
    public void after() {
        String src =
                "BEGIN;\n" +
                        "drop var myvar;" +
                        "END;\n" +
                        "true";
        file.delete();
        testEquals("true", src);
    }
}
