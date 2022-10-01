package org.reldb.rel.tests.main;

import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestSearch extends BaseOfTest {

	@Test
	public void testSearch01() {
		String src = 
			"SEARCH("
			+ "TUP {x 1, y 2.3, z 'blah'},"
			+ "'1')";
		String expected = "true";
		testEquals(expected, src);
	}

	@Test
	public void testSearch02() {
		String src = 
				"SEARCH("
				+ "TUP {x 1, y 2.3, z 'blah'},"
				+ "'2.3')";
		String expected = "true";
		testEquals(expected, src);
	}
		
	@Test
	public void testSearch03() {
		String src = 
				"SEARCH("
				+ "TUP {x 1, y 2.3, z 'blah'},"
				+ "'blah')";
		String expected = "true";
		testEquals(expected, src);
	}
	
	@Test
	public void testSearch04() {
		String src = 
				"SEARCH("
				+ "TUP {x 1, y 2.3, z 'blah'},"
				+ "'.*la.*')";
		String expected = "true";
		testEquals(expected, src);
	}
	
	@Test
	public void testSearch05() {
		String src = 
				"SEARCH("
				+ "TUP {x 1, y 2.3, z 'blah'},"
				+ "'.*3.*')";
		String expected = "true";
		testEquals(expected, src);
	}
	
	@Test
	public void testSearch06() {
		String src = 
				"SEARCH("
				+ "TUP {x 1, y 2.3, z 'blah'},"
				+ "'.*4.*')";
		String expected = "false";
		testEquals(expected, src);
	}

}
