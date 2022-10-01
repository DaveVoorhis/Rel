package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestOuterJoins01 extends BaseOfTest {
	
	@BeforeClass
	public static void before() {
		String src = "BEGIN;" +
		"VAR LeftData REAL RELATION {L CHARACTER, Common CHARACTER} INIT(RELATION {" +
		"	TUPLE {L \"L\", Common \"1\"}," +
		"	TUPLE {L \"LL\", Common \"2\"}" +
		"}) KEY {L};" +
		"VAR RightData REAL RELATION {R CHARACTER, Common CHARACTER} INIT(RELATION {" +
		"	TUPLE {R \"R\", Common \"3\"}," +
		"	TUPLE {R \"RR\", Common \"2\"}" +
		"}) KEY {R};" +		
		"END; true";
		testEquals("true", src);
	}
	
	@Test
	public void testLeftJoin01() {
		String src = "LeftData LEFT JOIN RightData";
		String rsp = "TUPLE {Matched RELATION {L CHARACTER, Common CHARACTER, R CHARACTER} {\n" + 
				"\tTUPLE {L \"LL\", Common \"2\", R \"RR\"}\n" + 
				"}, Missing RELATION {L CHARACTER, Common CHARACTER} {\n" + 
				"\tTUPLE {L \"L\", Common \"1\"}\n" + 
				"}}";
		testEquals(rsp, src);
	}

	@Test
	public void testRightJoin01() {
		String src = "LeftData RIGHT JOIN RightData";
		String rsp = "TUPLE {Matched RELATION {L CHARACTER, Common CHARACTER, R CHARACTER} {\n" + 
				"\tTUPLE {L \"LL\", Common \"2\", R \"RR\"}\n" + 
				"}, Missing RELATION {R CHARACTER, Common CHARACTER} {\n" + 
				"\tTUPLE {R \"R\", Common \"3\"}\n" + 
				"}}";
		testEquals(rsp, src);
	}

	@Test
	public void testFullJoin01() {
		String src = "LeftData FULL JOIN RightData";
		String rsp = "TUPLE {Matched RELATION {L CHARACTER, Common CHARACTER, R CHARACTER} {\n" + 
				"\tTUPLE {L \"LL\", Common \"2\", R \"RR\"}\n" + 
				"}, MissingLeft RELATION {L CHARACTER, Common CHARACTER} {\n" + 
				"\tTUPLE {L \"L\", Common \"1\"}\n" + 
				"}, MissingRight RELATION {R CHARACTER, Common CHARACTER} {\n" + 
				"\tTUPLE {R \"R\", Common \"3\"}\n" + 
				"}}";
		testEquals(rsp, src);
	}
	
	@AfterClass
	public static void after() {
		String src = "BEGIN;" +
		"DROP VAR LeftData;" +
		"DROP VAR RightData;" +
		"END; true";
		testEquals("true", src);		
	}
	
}
