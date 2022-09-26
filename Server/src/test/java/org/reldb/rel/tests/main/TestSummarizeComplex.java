package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestSummarizeComplex extends BaseOfTest {
	
	@BeforeClass
	public static void testSummarizeComplexSetup() {
		String src =
			"begin;" +
			"  VAR BirdTrack REAL RELATION {date CHAR, location CHAR, species CHAR, cnt INTEGER}  KEY {date, location, species};" +
			"  BirdTrack := RELATION {date CHAR, location CHAR, species CHAR, cnt INTEGER} {" +
			"	TUPLE {date \"20071119\", location \"SP26DW\", species \"Blue Tit\", cnt 5}," +
			"	TUPLE {date \"20071119\", location \"SP26DW\", species \"Jackdaw\", cnt 6}," +
			"	TUPLE {date \"20071120\", location \"SP26DX\", species \"Sparrow\", cnt 3}," +
			"	TUPLE {date \"20071121\", location \"SP26DY\", species \"Finch\", cnt 2}," +
			"	TUPLE {date \"20071122\", location \"SP26DZ\", species \"Hawk\", cnt 1}," +
			"	TUPLE {date \"20071119\", location \"SP26DW\", species \"Carrion Crow\", cnt 5}};" +
			"  VAR PossibleSpecies REAL RELATION {species CHAR} KEY {species};" +
			"  PossibleSpecies := BirdTrack {species};" +
			"  VAR PartialResult VIRTUAL summarize BirdTrack per(PossibleSpecies) : {VisitsSeenOn := count()};" +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);								
	}
	
	@Test
	public void testSummarizeComplex01() {
		String src =
			"extend (PartialResult) : {NoOfVisits := count(BirdTrack{date})}";
		String expected = "RELATION {species CHARACTER, VisitsSeenOn INTEGER, NoOfVisits INTEGER} {" +
			"\n\tTUPLE {species \"Blue Tit\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Carrion Crow\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Finch\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Hawk\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Jackdaw\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Sparrow\", VisitsSeenOn 1, NoOfVisits 4}" +
			"\n}";
		testEquals(expected, src);						
	}
	
	@Test
	public void testSummarizeComplex02() {
		String src =
			"extend (summarize BirdTrack per(PossibleSpecies) : {VisitsSeenOn := count()}) : {NoOfVisits := count(BirdTrack{date})}";
		String expected = "RELATION {species CHARACTER, VisitsSeenOn INTEGER, NoOfVisits INTEGER} {" +
			"\n\tTUPLE {species \"Blue Tit\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Carrion Crow\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Finch\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Hawk\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Jackdaw\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Sparrow\", VisitsSeenOn 1, NoOfVisits 4}" +
			"\n}";
		testEquals(expected, src);
	}
		
	@Test
	public void testSummarizeComplex03() {
		String src =
			"extend (summarize BirdTrack per(PossibleSpecies) : {VisitsSeenOn := count()}) : {NoOfVisits := 4}";
		String expected = "RELATION {species CHARACTER, VisitsSeenOn INTEGER, NoOfVisits INTEGER} {" +
			"\n\tTUPLE {species \"Blue Tit\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Carrion Crow\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Finch\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Hawk\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Jackdaw\", VisitsSeenOn 1, NoOfVisits 4}," +
			"\n\tTUPLE {species \"Sparrow\", VisitsSeenOn 1, NoOfVisits 4}" +
			"\n}";
		testEquals(expected, src);
	}
		
	@Test
	public void testSummarizeComplex04() {
		String src = 
			"extend (summarize BirdTrack : {VisitsSeenOn := count()}) : {NoOfVisits := 1}";
		String expected = "RELATION {VisitsSeenOn INTEGER, NoOfVisits INTEGER} {" +
			"\n\tTUPLE {VisitsSeenOn 6, NoOfVisits 1}" +
			"\n}";
		testEquals(expected, src);
	}
	
	@AfterClass
	public static void testSummarizeComplexTeardown() {
		String src =
			"begin;" +
			"  DROP VAR PartialResult;" +
			"  DROP VAR BirdTrack;" +
			"  DROP VAR PossibleSpecies;" +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);						
	}

}
