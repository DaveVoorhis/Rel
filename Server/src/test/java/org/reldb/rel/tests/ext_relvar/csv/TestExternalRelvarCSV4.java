package org.reldb.rel.tests.ext_relvar.csv;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestExternalRelvarCSV4 extends BaseOfTest {
	
	private final String path = "test.csv";
	private File file = new File(path);
	
	@Before
	public void testCSV1() {
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsolutePath());
			fw.write("A,B,C\n" + "1,2,3\n" + "4,5,6\n" + "7,8,9\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String src = 
				"BEGIN;\n" +
						"var myvar external csv \"" + file.getAbsolutePath().replace("\\", "\\\\") + "\";" +
				"END;\n" +
				"true";
		testEquals("true", src);
	}
	
	@Test
	public void testCSV2() {
		String src = "myvar";		
		testEquals(	"RELATION {_AUTOKEY INTEGER, A CHARACTER, B CHARACTER, C CHARACTER} {" +
					"\n\tTUPLE {_AUTOKEY 1, A \"1\", B \"2\", C \"3\"}," +
					"\n\tTUPLE {_AUTOKEY 2, A \"4\", B \"5\", C \"6\"}," +
					"\n\tTUPLE {_AUTOKEY 3, A \"7\", B \"8\", C \"9\"}\n}", src);
	}
	
	@After
	public void testCSV3() {
		String src = 
				"BEGIN;\n" +
						"drop var myvar;" +
				"END;\n" +
				"true";
		file.delete();
		testEquals("true", src);
	}
}
