package org.reldb.rel.tests.main;

import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;
import org.reldb.rel.v0.values.ValueBoolean;

public class TestEquiv extends BaseOfTest {

	@Test
	public void testBinaryEquiv0() {
		String src =
				"false equiv true";
			assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());		
	}

	@Test
	public void testBinaryEquiv1() {
		String src =
				"true equiv false";
			assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());		
	}

	@Test
	public void testBinaryEquiv2() {
		String src =
				"true equiv true";
			assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());		
	}

	@Test
	public void testBinaryEquiv3() {
		String src =
				"false equiv false";
			assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());		
	}
	
	@Test
	public void testNAdicEquiv0() {
		String src =
			"equiv {}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());						
	}

	@Test
	public void testNAdicEquiv1() {
		String src =
			"equiv {true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());						
	}

	@Test
	public void testNAdicEquiv2() {
		String src =
			"equiv {false}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());						
	}	

	@Test
	public void testNAdicEquiv3() {
		String src =
			"equiv {false, true}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());					
	}	

	@Test
	public void testNAdicEquiv4() {
		String src =
			"equiv {true, true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());					
	}	

	@Test
	public void testNAdicEquiv5() {
		String src =
			"equiv {false, false}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());						
	}	

	@Test
	public void testNAdicEquiv6() {
		String src =
			"equiv {false, false, true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());					
	}	

	@Test
	public void testNAdicEquiv7() {
		String src =
			"equiv {false, true, true}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());					
	}

	@Test
	public void testNAdicEquiv8() {
		String src =
			"equiv {false, true, false, true, true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());					
	}

	@Test
	public void testNAdicEquiv9() {
		String src =
			"equiv {false, true, false, true, true, false}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());					
	}

	@Test
	public void testRelationEquiv1() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot true, x_zot 2}, tuple {x_zot 3, w_zot true}};" + 
			"END;" +
			"EQUIV(a, w_zot)";
		testEquals("true", src);	
	}

	@Test
	public void testRelationEquiv2() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot false, x_zot 2}, tuple {x_zot 3, w_zot true}};" + 
			"END;" +
			"EQUIV(a, w_zot)";
		testEquals("false", src);	
	}

	@Test
	public void testRelationEquiv3() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot false, x_zot 2}, tuple {x_zot 3, w_zot false}};" + 
			"END;" +
			"EQUIV(a, w_zot)";
		testEquals("true", src);	
	}

	@Test
	public void testRelationEquiv4() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
			"END;" +
			"EQUIV(a, w_zot)";
		testEquals("true", src);	
	}

	@Test
	public void testSummarizeEquiv() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x false, y 1.2, z \"falsefalse\"},\n" +
		        "       tuple {x false, y 1.3, z \"falsefalse\"},\n" +
		        "       tuple {x true, y 1.4, z \"truefalse\"},\n" +
		        "       tuple {x false, y 1.5, z \"truefalse\"},\n" +
		        "       tuple {x true, y 1.6, z \"truetrue\"},\n" +
		        "       tuple {x true, y 1.7, z \"truetrue\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a BY {z} : {equivOfx := EQUIV(x)}";
		testEquals("RELATION {z CHARACTER, equivOfx BOOLEAN} {\n\tTUPLE {z \"falsefalse\", equivOfx true},\n\tTUPLE {z \"truefalse\", equivOfx false},\n\tTUPLE {z \"truetrue\", equivOfx true}\n}", src);		
	}
  	 	
}
