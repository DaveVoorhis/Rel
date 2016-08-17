package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestAggregateUserdefined extends BaseOfTest {

	public static void testAggregateSetup1() {
		String src =
			"begin;" +
			"OPERATOR SQRT(a RATIONAL) RETURNS RATIONAL Java FOREIGN \n" + 
			"// Returns the correctly rounded positive square root of a RATIONAL \n" +
			"// value. \n" +
			"	return ValueRational.select(context.getGenerator(), Math.sqrt(a.doubleValue())); \n" +
			"END OPERATOR;" +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);								
	}
	
	public static void testAggregateSetup2() {
		String src =
			"begin;" +
			"OPERATOR AGGREGATE_STDEV(data RELATION {AGGREGAND INTEGER, AGGREGATION_SERIAL INTEGER}) RETURNS RATIONAL; " +
			"	RETURN WITH ( " +
			"		mean := AVG(data, AGGREGAND), " +
			"		squarediffs := EXTEND data: { " +
			"			squaredifference := WITH (difference := CAST_AS_RATIONAL(AGGREGAND) - mean): " +
			"				difference * difference " +
			"		} " +
			"	): SQRT(AVG(squarediffs, squaredifference)); " +
			"END OPERATOR; " +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);								
	}
	
	public static void testAggregateSetup3() {
		String src =
			"begin;" +
			"OPERATOR AGGREGATE_TEST(data RELATION {AGGREGAND INTEGER, AGGREGATION_SERIAL INTEGER}, i INT) RETURNS INT; " +
			"	RETURN AGGREGATE(data, AGGREGAND, i); RETURN VALUE1 + VALUE2; END AGGREGATE; " +
			"END OPERATOR; " +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}
	
	public static void testAggregateSetup4() {
		String src =
			"begin;" +
			"OPERATOR AGGREGATE_TEST(data RELATION {AGGREGAND INTEGER, AGGREGATION_SERIAL INTEGER}) RETURNS INT; " +
			"	RETURN AGGREGATE(data, AGGREGAND); RETURN VALUE1 + VALUE2; END AGGREGATE; " +
			"END OPERATOR; " +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}
	
	@BeforeClass
	public static void testSetup() {
		testAggregateSetup1();
		testAggregateSetup2();
		testAggregateSetup3();
		testAggregateSetup4();
	}
	
	@Test
	public void testAggregate00() {
		String src = 
			"SUMMARIZE \n" +
			"	REL { \n" +
			"		TUP {x 1, c 'A', d 'A'}, \n" +
			"		TUP {x 1, c 'A', d 'B'}, \n" +
			"		TUP {x 1, c 'B', d 'A'}, \n" +
			"		TUP {x 1, c 'C', d 'B'}, \n" +
			"		TUP {x 2, c 'A', d 'A'} \n" +
			"	} \n" +
			"BY {c}: { \n" +
			"	std_dev_distinct_x := STDEV(DISTINCT x), \n" +
			"	std_dev_distinct_1 := STDEV(DISTINCT 1), \n" +
			"	std_dev_x := STDEV(x), \n" +
			"	std_dev_1 := STDEV(1), \n" +
			"	std_dev_distinct_x2 := STDEV(DISTINCT x * 2), \n" +
			"	std_dev_x2 := STDEV(x * 2), \n" +
			"	test_distinct_x := TEST(DISTINCT x), \n" +
			"	test_distinct_1 := TEST(DISTINCT 1), \n" +
			"	test_x := TEST(x), \n" +
			"	test_1 := TEST(1), \n" +
			"	test_distinct_x2 := TEST(DISTINCT x * 2), \n" +
			"	test_x2 := TEST(x * 2), \n" +
			"	test_distinct_x_start2 := TEST(DISTINCT x, 2), \n" +
			"	test_distinct_1_start2 := TEST(DISTINCT 1, 2), \n" +
			"	test_x_start2 := TEST(x, 2), \n" +
			"	test_1_start2 := TEST(1, 2), \n" +
			"	test_distinct_x2_start2 := TEST(DISTINCT x * 2, 2), \n" +
			"	test_x2_start2 := TEST(x * 2, 2), \n" +
			"	N := COUNT(), \n" +
			"	XBAR := AVG(x), \n" +
			"	agg_x := AGGREGATE(x); RETURN VALUE1 + VALUE2; END, \n" +
			"	agg_distinct_x := AGGREGATED(x); RETURN VALUE1 + VALUE2; END, \n" +
			"	agg_1_start1 := AGGREGATE(x, 1); RETURN VALUE1 + VALUE2; END, \n" +
			"	agg_distinct_start2 := AGGREGATED(x, 2); RETURN VALUE1 + VALUE2; END \n" +
			"} ";
		String expected = "RELATION {c CHARACTER, std_dev_distinct_x RATIONAL, std_dev_distinct_1 RATIONAL, std_dev_x RATIONAL, std_dev_1 RATIONAL, std_dev_distinct_x2 RATIONAL, std_dev_x2 RATIONAL, test_distinct_x INTEGER, test_distinct_1 INTEGER, test_x INTEGER, test_1 INTEGER, test_distinct_x2 INTEGER, test_x2 INTEGER, test_distinct_x_start2 INTEGER, test_distinct_1_start2 INTEGER, test_x_start2 INTEGER, test_1_start2 INTEGER, test_distinct_x2_start2 INTEGER, test_x2_start2 INTEGER, N INTEGER, XBAR RATIONAL, agg_x INTEGER, agg_distinct_x INTEGER, agg_1_start1 INTEGER, agg_distinct_start2 INTEGER} {\n" +
				"\tTUPLE {c \"A\", std_dev_distinct_x 0.5, std_dev_distinct_1 0.0, std_dev_x 0.4714045207910317, std_dev_1 0.0, std_dev_distinct_x2 1.0, std_dev_x2 0.9428090415820634, test_distinct_x 3, test_distinct_1 1, test_x 4, test_1 3, test_distinct_x2 6, test_x2 8, test_distinct_x_start2 5, test_distinct_1_start2 3, test_x_start2 6, test_1_start2 5, test_distinct_x2_start2 8, test_x2_start2 10, N 3, XBAR 1.3333333333333333, agg_x 4, agg_distinct_x 3, agg_1_start1 5, agg_distinct_start2 5},\n" +
				"\tTUPLE {c \"B\", std_dev_distinct_x 0.0, std_dev_distinct_1 0.0, std_dev_x 0.0, std_dev_1 0.0, std_dev_distinct_x2 0.0, std_dev_x2 0.0, test_distinct_x 1, test_distinct_1 1, test_x 1, test_1 1, test_distinct_x2 2, test_x2 2, test_distinct_x_start2 3, test_distinct_1_start2 3, test_x_start2 3, test_1_start2 3, test_distinct_x2_start2 4, test_x2_start2 4, N 1, XBAR 1.0, agg_x 1, agg_distinct_x 1, agg_1_start1 2, agg_distinct_start2 3},\n" +
				"\tTUPLE {c \"C\", std_dev_distinct_x 0.0, std_dev_distinct_1 0.0, std_dev_x 0.0, std_dev_1 0.0, std_dev_distinct_x2 0.0, std_dev_x2 0.0, test_distinct_x 1, test_distinct_1 1, test_x 1, test_1 1, test_distinct_x2 2, test_x2 2, test_distinct_x_start2 3, test_distinct_1_start2 3, test_x_start2 3, test_1_start2 3, test_distinct_x2_start2 4, test_x2_start2 4, N 1, XBAR 1.0, agg_x 1, agg_distinct_x 1, agg_1_start1 2, agg_distinct_start2 3}\n" +
				"}";
		testEquals(expected, src);
	}

	@Test
	public void testAggregate01() {
		String src = 
			"AGGREGATE TEST(" +
			"REL { \n" +
			"	TUP {x 1, c 'A', d 'A'}, \n" +
			"	TUP {x 1, c 'A', d 'B'}, \n" +
			"	TUP {x 1, c 'B', d 'A'}, \n" +
			"	TUP {x 1, c 'C', d 'B'}, \n" +
			"	TUP {x 2, c 'A', d 'A'} \n" +
			"}, \n" +
			"x)";
		String expected = "6";
		testEquals(expected, src);
	}
	
	@Test
	public void testAggregate02() {
		String src = 
			"AGGREGATE TEST(" +
			"REL { \n" +
			"	TUP {x 1, c 'A', d 'A'}, \n" +
			"	TUP {x 1, c 'A', d 'B'}, \n" +
			"	TUP {x 1, c 'B', d 'A'}, \n" +
			"	TUP {x 1, c 'C', d 'B'}, \n" +
			"	TUP {x 2, c 'A', d 'A'} \n" +
			"}, \n" +
			"x, 2)";
		String expected = "8";
		testEquals(expected, src);
	}
	
	@AfterClass
	public static void testSummarizeComplexTeardown() {
		String src =
			"begin;" +
			"  DROP OPERATOR AGGREGATE_TEST(RELATION {AGGREGAND INTEGER, AGGREGATION_SERIAL INTEGER});" +
			"  DROP OPERATOR AGGREGATE_TEST(RELATION {AGGREGAND INTEGER, AGGREGATION_SERIAL INTEGER}, INT);" +
			"  DROP OPERATOR AGGREGATE_STDEV(RELATION {AGGREGAND INTEGER, AGGREGATION_SERIAL INTEGER});" +
			"  DROP OPERATOR SQRT(RATIONAL);" +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);						
	}

}
