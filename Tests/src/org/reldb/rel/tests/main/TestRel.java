package org.reldb.rel.tests.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.tests.BaseOfTest;
import org.reldb.rel.v0.storage.relvars.external.ColumnName;
import org.reldb.rel.v0.values.*;

public class TestRel extends BaseOfTest {
	
	@Test
	public void testExternalRelvarColumnRenamer() {
		String source = "0abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890#!@£$%^&*()_-+=:;'\"|\\/?><.,~`±§";
		String target = "_0abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890#______________________________";
		assertEquals(ColumnName.cleanName(source), target);
		// target length should equal source length plus 1 to allow for leading '_' because of leading '0' in source
		assertEquals(target.length(), source.length() + 1);
	}
	
	@Test 
	public void testTrueIsTrue() {
		assertValueEquals(ValueBoolean.select(generator, true), ValueBoolean.select(generator, true));
	}

	@Test
	public void testFalseIsFalse() {
		assertValueEquals(ValueBoolean.select(generator, false), ValueBoolean.select(generator, false));
	}
	
	@Test
	public void testInterpretTrue() {
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate("true").getValue());
	}

	@Test
	public void testInterpretFalse() {
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate("false").getValue());
	}

	@Test
	public void testSimpleExpression1() {
		Value v1 = ValueInteger.select(generator, 4);
		Value v2 = testEvaluate("3 + 4 * 2 / 8").getValue();
		assertValueEquals(v1, v2);
	}

	@Test
	public void testSimpleExpression2() {
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate("3 = 4").getValue());
	}

	@Test
	public void testSimpleExpression3() {
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate("3 < 4").getValue());
	}

	@Test
	public void testSimpleExpression4() {
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate("3 ≤ 4").getValue());
	}
	
	@Test
	public void testSimpleExpression5() {
		Value v1 = ValueInteger.select(generator, 4);
		Value v2 = testEvaluate("3 + 4 × 2 ÷ 8").getValue();
		assertValueEquals(v1, v2);
	}

	@Test
	public void testSimpleExpression6() {
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate("3 ≠ 4").getValue());
	}
	
	@Test
	public void testSimpleExpression7() {
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate("4 ≠ 4").getValue());
	}
	
	@Test
	public void testSimpleVariable1() {
		String src = 	
			"BEGIN;" +
				"VAR a INIT (3);" +
			"END;" +
			"a";
		assertValueEquals(ValueInteger.select(generator, 3), testEvaluate(src).getValue());
	}
	
	@Test
	public void testSimpleVariable2() {
		String src = 	
			"BEGIN;" +
				"var a integer init (3);" +
			"END;" +
			"a";
		assertValueEquals(ValueInteger.select(generator, 3), testEvaluate(src).getValue());
	}

	@Test
	public void testSimpleVariable3() {
		String src = 	
			"BEGIN;" +
			  "var a integer;" +
			  "a := 5;" +
			"END;" +
			"a";
		assertValueEquals(ValueInteger.select(generator, 5), testEvaluate(src).getValue());
	}	

	@Test
	public void testSimpleVariable3a() {
		String src = 	
			"BEGIN;" +
			  "var a int;" +
			  "a := 5;" +
			"END;" +
			"a";
		assertValueEquals(ValueInteger.select(generator, 5), testEvaluate(src).getValue());
	}	

	@Test
	public void testSimpleVariable4() {
		String src = 	
			"BEGIN;" +
			  "var a rational;" +
			  "a := 5.6;" +
			"END;" +
			"a";
		assertValueEquals(ValueRational.select(generator, 5.6), testEvaluate(src).getValue());
	}	
	
	@Test
	public void testSimpleVariable4a() {
		String src = 	
			"BEGIN;" +
			  "var a rat;" +
			  "a := 5.6;" +
			"END;" +
			"a";
		assertValueEquals(ValueRational.select(generator, 5.6), testEvaluate(src).getValue());
	}	
	
	@Test
	public void testSimpleVariable5() {
		String src = 	
			"BEGIN;" +
			  "var a boolean;" +
			  "a := true;" +
			"END;" +
			"a";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	
	
	@Test
	public void testSimpleVariable5a() {
		String src = 	
			"BEGIN;" +
			  "var a bool;" +
			  "a := true;" +
			"END;" +
			"a";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	
	
	@Test
	public void testSimpleVariables() {
		String src = 	
			"BEGIN;" +
				"var a integer;" +
				"var b init (3 + 5);" +
				"var c integer init (4 + 6);" +
				"var d same_type_as (5 * 2);" +
				"d := 2;" +
				"a := 1;" +
				"a := 3 + a + 4 + b + 5 + c + d;" +
			"END;" +
			"a";
		assertValueEquals(ValueInteger.select(generator, 33), testEvaluate(src).getValue());
	}
	
	@Test
	public void testEquality01() {
		String src = "RELATION  {TUPLE {P RELATION {TUPLE {PID \"P5\"}}}, TUPLE {P RELATION {TUPLE {PID \"P2\"}}}, TUPLE {P RELATION {TUPLE {PID \"P4\"}}}}" + 
					 "=" + 
					 "RELATION  {TUPLE {P RELATION {TUPLE {PID \"P5\"}}}, TUPLE {P RELATION {TUPLE {PID \"P2\"}}}, TUPLE {P RELATION {TUPLE {PID \"P4\"}}}}";
		testEquals("true", src);
	}

	@Test
	public void testEquality02() {
		String src = 
				"RELATION  {" +
				" TUPLE {P RELATION {TUPLE {PID \"P5\"}, TUPLE {PID \"P2\"}}}, " +
				" TUPLE {P RELATION {TUPLE {PID \"P2\"}, TUPLE {PID \"P7\"}}}, " +
				" TUPLE {P RELATION {TUPLE {PID \"P4\"}, TUPLE {PID \"P5\"}}}" +
				"}" + 
				"=" + 
				"RELATION  {" +
				" TUPLE {P RELATION {TUPLE {PID \"P5\"}, TUPLE {PID \"P2\"}}}, " +
				" TUPLE {P RELATION {TUPLE {PID \"P2\"}, TUPLE {PID \"P7\"}}}, " +
				" TUPLE {P RELATION {TUPLE {PID \"P4\"}, TUPLE {PID \"P5\"}}}" +
				"}";
		testEquals("true", src);
	}

	@Test
	public void testEquality03() {
		String src = 
				"RELATION  {" +
				" TUPLE {P RELATION {TUPLE {PID \"P7\"}, TUPLE {PID \"P2\"}}}, " +
				" TUPLE {P RELATION {TUPLE {PID \"P5\"}, TUPLE {PID \"P2\"}}}, " +
				" TUPLE {P RELATION {TUPLE {PID \"P5\"}, TUPLE {PID \"P4\"}}}" +
				"}" + 
				"=" + 
				"RELATION  {" +
				" TUPLE {P RELATION {TUPLE {PID \"P5\"}, TUPLE {PID \"P2\"}}}, " +
				" TUPLE {P RELATION {TUPLE {PID \"P2\"}, TUPLE {PID \"P7\"}}}, " +
				" TUPLE {P RELATION {TUPLE {PID \"P4\"}, TUPLE {PID \"P5\"}}}" +
				"}";
		testEquals("true", src);
	}
	
	@Test
	public void testIf1() {
		String src =
			"BEGIN;" +
				"var a integer;" +
				"var b integer;" +
				"var c integer;" +
				"if 3 < 4 then " +
					"a := 1;" +
				"end if;" +
				"if 3 < 4 then " +
					"b := 10;" +
				"else " +
					"b := 20;" +
				"end if;" +
				"if 3 > 4 then " +
					"c := 100;" +
				"else " +
					"c := 200;" +
				"end if;" +			
			"END;" +
			"a + b + c";
		assertValueEquals(ValueInteger.select(generator, 211), testEvaluate(src).getValue());
	}

	@Test
	public void testIf2() {
		String src =
			"BEGIN;" +
				"VAR a INIT(5);" +
				"var b integer;" +
				"if a = 1 then " +
				"   b := 1;" +
				"else " +
				"   if a = 2 then " +
				"      b := 2;" +
				"   else " +
				"      if a = 3 then " +
				"         b := 3;" +
				"      else " +
				"         b := 4;" +
				"      end if;" +			
				"   end if;" +			
				"end if;" +			
			"END;" +
			"b";
		assertValueEquals(ValueInteger.select(generator, 4), testEvaluate(src).getValue());
	}
		
	@Test
	public void testIf4() {
		String src =
			"if 3 < 4 then " +
				"10" +
			"else " +
				"20" +
			"end if";
	assertValueEquals(ValueInteger.select(generator, 10), testEvaluate(src).getValue());
	}

	@Test
	public void testIf5() {
		String src =
			"if 3 > 4 then " +
				"10" +
			"else " +
				"20" +
			"end if";
		assertValueEquals(ValueInteger.select(generator, 20), testEvaluate(src).getValue());
	}
	
	@Test
	public void testDoLoop() {
		String src =
			"BEGIN;" +
				"var a integer;" +
				"var i integer;" +
				"DO i := 1 TO 10;" +
				"   a := a + 1;" +
				"END DO;" +
			"END;" +
			"a";
		assertValueEquals(ValueInteger.select(generator, 10), testEvaluate(src).getValue());	
	}

	@Test
	public void testWhileLoop() {
		String src =
			"BEGIN;" +
				"var a integer;" +
				"var i init(10);" +
				"WHILE i > 0;" +
				"  BEGIN;" +
				"    a := a + 1;" +
				"    i := i - 1;" +
				"  END;" +
				"END WHILE;" +
			"END;" +
			"a";
		assertValueEquals(ValueInteger.select(generator, 10), testEvaluate(src).getValue());	
	}
	
	@Test
	public void testNAdicOr1() {
		String src =
			"or {true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());						
	}

	@Test
	public void testNAdicOr2() {
		String src =
			"or {false}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());						
	}	

	@Test
	public void testNAdicOr3() {
		String src =
			"or {false, true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());					
	}	

	@Test
	public void testNAdicOr4() {
		String src =
			"or {false, false}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());						
	}	

	@Test
	public void testNAdicOr5() {
		String src =
			"or {false, false, true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());					
	}	

	@Test
	public void testNAdicOr6() {
		String src =
			"or {}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());					
	}	

	@Test
	public void testNAdicAnd1() {
		String src =
			"and {true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());						
	}

	@Test
	public void testNAdicAnd2() {
		String src =
			"and {false}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());	
	}	

	@Test
	public void testNAdicAnd3() {
		String src =
			"and {false, true}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());
	}	

	@Test
	public void testNAdicAnd4() {
		String src =
			"and {false, false}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());
	}	

	@Test
	public void testNAdicAnd5() {
		String src =
			"and {false, false, true}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());
	}	
	
	@Test
	public void testNAdicAnd6() {
		String src =
			"and {true, true, true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

	@Test
	public void testNAdicAnd7() {
		String src =
			"and {}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

	@Test
	public void testNAdicXor1() {
		String src =
			"xor {true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

	@Test
	public void testNAdicXor2() {
		String src =
			"xor {false}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());
	}	

	@Test
	public void testNAdicXor3() {
		String src =
			"xor {false, true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

	@Test
	public void testNAdicXor4() {
		String src =
			"xor {false, false}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());
	}	

	@Test
	public void testNAdicXor5() {
		String src =
			"xor {true, true}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());
	}	
	
	@Test
	public void testNAdicXor6() {
		String src =
			"xor {true, true, true}";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	
	
	@Test
	public void testNAdicXor7() {
		String src =
			"xor {}";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());
	}	
	
	@Test
	public void testNadicSum1() {
		String src =
			"SUM {1, 2, 3, 4}";
		testEquals("10", src);	
	}
	
	@Test
	public void testNadicSum2() {
		String src =
			"SUM {1.1, 2.1, 3.5, 4.4}";
		testEquals("11.1", src);	
	}

	@Test
	public void testNadicSum3() {
		String src =
			"SUM {5}";
		testEquals("5", src);	
	}
	
	@Test
	public void testNadicSum4() {
		String src =
			"SUM {}";
		testEquals("0", src);	
	}
	
	@Test
	public void testNadicAvg1() {
		String src =
			"AVG {1, 2, 3, 4}";
		testEquals("2.5", src);	
	}

	@Test
	public void testNadicAvg2() {
		String src =
			"AVG {4.3}";
		testEquals("4.3", src);	
	}

	@Test
	public void testNadicMax1() {
		String src =
			"MAX {1, 2, 6, 3, 4}";
		testEquals("6", src);	
	}

	@Test
	public void testNadicMax2() {
		String src =
			"MAX {4.3}";
		testEquals("4.3", src);	
	}
	
	@Test
	public void testNadicMin1() {
		String src =
			"MIN {1, 2, 6, -2, 3, 4}";
		testEquals("-2", src);	
	}

	@Test
	public void testNadicMin2() {
		String src =
			"MIN {4.3}";
		testEquals("4.3", src);	
	}

	@Test
	public void testNadicCount1() {
		String src =
			"COUNT {1, 2, 6, -2, 3, 4}";
		testEquals("6", src);	
	}

	@Test
	public void testNadicCount2() {
		String src =
			"COUNT {}";
		testEquals("0", src);	
	}

	@Test
	public void testExactly1() {
		String src =
			"exactly (0)";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

	@Test
	public void testExactly2() {
		String src =
			"exactly (0, false)";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}
	
	@Test
	public void testExactly3() {
		String src =
			"exactly (1, true)";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}
	
	@Test
	public void testExactly4() {
		String src =
			"exactly (1, true, false)";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

	@Test
	public void testExactly5() {
		String src =
			"exactly (2, true, false, true)";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}
	
	@Test
	public void testExactly6() {
		String src =
			"exactly (1)";
		assertValueEquals(ValueBoolean.select(generator, false), testEvaluate(src).getValue());
	}

	@Test
	public void testConcatenate1() {
		String src =
			"'blah' || 'zot'";
		testEquals("\"blahzot\"", src);			
	}
	
	@Test
	public void testConcatenate2() {
		String src =
			"1.23 || 'zot'";
		testEquals("\"1.23zot\"", src);			
	}
	
	@Test
	public void testTuple1() {
		String src =
			"BEGIN;" +
				"var a tuple {w char, x integer, y boolean, z rational};" +
				"a := tuple {w 'test', x 3, y true, z 2.5};" +
			"END;" +
			"a";
		testEquals("TUPLE {w \"test\", x 3, y true, z 2.5}", src);
	}

	@Test
	public void testTuple2() {
		String src =
			"BEGIN;" +
				"var a tuple {w char, y boolean, z rational, x integer};" +
				"a := tuple {x 3, w 'test', y true, z 2.5};" +
			"END;" +
			"a";
		testEquals("TUPLE {w \"test\", y true, z 2.5, x 3}", src);	
	}

	@Test
	public void testTupleEquality1() {
		String src = "TUPLE {x 'glub', y 3} = TUPLE {y 3, x 'glub'}";
		testEquals("true", src);		
	}
	
	@Test
	public void testTupleEquality2() {
		String src = "TUPLE {x 'glub', y 4} <> TUPLE {y 3, x 'glub'}";
		testEquals("true", src);		
	}
	
	@Test
	public void testTupleRename1() {
		String src =
			"BEGIN;" +
				"var a tuple {w char, y boolean, z rational, x integer};" +
				"a := tuple {x 3, w 'test', y true, z 2.5};" +
			"END;" +
			"a rename {w as woggle, z as zork}";
		testEquals("TUPLE {woggle \"test\", y true, zork 2.5, x 3}", src);	
	}

	@Test
	public void testTupleRename2() {
		String src =
			"BEGIN;" +
				"var a tuple {zot_w char, zot_y boolean, zot_z rational, zot_x integer};" +
				"a := tuple {zot_x 3, zot_w 'test', zot_y true, zot_z 2.5};" +
			"END;" +
			"a rename {prefix 'zot' as 'zap'}";
		testEquals("TUPLE {zap_w \"test\", zap_y true, zap_z 2.5, zap_x 3}", src);	
	}
	
	@Test
	public void testTupleRename3() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
			"END;" +
			"a rename {suffix 'zot' as 'zap'}";
		testEquals("TUPLE {w_zap \"test\", y_zap true, z_zap 2.5, x_zap 3}", src);	
	}

	@Test
	public void testTupleRename4() {
		String src =
			"BEGIN;" +
				"var a tuple {w char, y boolean, z rational, x integer};" +
				"a := tuple {x 3, w 'test', y true, z 2.5};" +
			"END;" +
			"a rename {}";
		testEquals("TUPLE {w \"test\", y true, z 2.5, x 3}", src);	
	}
	
	@Test
	public void testTupleProject1() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
			"END;" +
			"a {x_zot, y_zot}";
		testEquals("TUPLE {x_zot 3, y_zot true}", src);	
	}
	
	@Test
	public void testTupleProject2() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
			"END;" +
			"a {y_zot, x_zot}";
		testEquals("TUPLE {y_zot true, x_zot 3}", src);	
	}
	
	@Test
	public void testTupleProject3() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
			"END;" +
			"a {ALL BUT y_zot, x_zot}";
		testEquals("TUPLE {w_zot \"test\", z_zot 2.5}", src);	
	}
	
	@Test
	public void testTupleProject4() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
			"END;" +
			"a {ALL BUT}";
		testEquals("TUPLE {w_zot \"test\", y_zot true, z_zot 2.5, x_zot 3}", src);	
	}

	@Test
	public void testTupleProject5() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
			"END;" +
			"a {}";
		testEquals("TUPLE {}", src);	
	}

	@Test
	public void testTupleJoin1() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
				"var b tuple {w_zap char, y_zap boolean, z_zap rational, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a join b";
		testEquals("TUPLE {w_zot \"test\", y_zot true, z_zot 2.5, x_zot 3, w_zap \"test\", y_zap true, z_zap 2.5, x_zap 3}", src);
	}

	@Test
	public void testTupleJoin2() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zap boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5};" +
				"var b tuple {w_zap char, y_zap boolean, z_zap rational, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a join b";
		testEquals("TUPLE {w_zot \"test\", y_zap true, z_zot 2.5, x_zot 3, w_zap \"test\", z_zap 2.5, x_zap 3}", src);
	}
	
	@Test
	public void testTupleJoin3() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zap boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5};" +
				"var b tuple {};" +
				"b := tuple {};" +
			"END;" +
			"a join b";
		testEquals("TUPLE {w_zot \"test\", y_zap true, z_zot 2.5, x_zot 3}", src);
	}

	@Test
	public void testTupleUnion1() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
				"var b tuple {w_zap char, y_zap boolean, z_zap rational, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a union b";
		testEquals("TUPLE {w_zot \"test\", y_zot true, z_zot 2.5, x_zot 3, w_zap \"test\", y_zap true, z_zap 2.5, x_zap 3}", src);
	}

	@Test
	public void testTupleUnion2() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zap boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5};" +
				"var b tuple {w_zap char, z_zap rational, y_zap boolean, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a union b";
		testEquals("TUPLE {w_zot \"test\", y_zap true, z_zot 2.5, x_zot 3, w_zap \"test\", z_zap 2.5, x_zap 3}", src);
	}
	
	@Test
	public void testTupleExtend1() {
		String src = "extend tuple {x 3, y 2.5, z 'test'} : {a := 3 + 4, b := 'glub'}";
		testEquals("TUPLE {x 3, y 2.5, z \"test\", a 7, b \"glub\"}", src);
	}

	@Test
	public void testTupleExtend2() {
		String src = "extend tuple {x 3, y 2, z 6} : {a := x + z, b := x + y}";
		testEquals("TUPLE {x 3, y 2, z 6, a 9, b 5}", src);
	}
	
	@Test
	public void testTupleIntersect1() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zap boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5};" +
				"var b tuple {w_zap char, z_zap rational, y_zap boolean, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a intersect b";
		testEquals("TUPLE {y_zap true}", src);
	}

	@Test
	public void testTupleIntersect2() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
				"var b tuple {w_zap char, z_zap rational, y_zap boolean, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a intersect b";
		testEquals("TUPLE {}", src);
	}

	@Test
	public void testTupleMinus1() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zap boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5};" +
				"var b tuple {w_zap char, z_zap rational, y_zap boolean, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a minus b";
		testEquals("TUPLE {w_zot \"test\", z_zot 2.5, x_zot 3}", src);
	}

	@Test
	public void testTupleMinus2() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
				"var b tuple {w_zap char, z_zap rational, y_zap boolean, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a minus b";
		testEquals("TUPLE {w_zot \"test\", y_zot true, z_zot 2.5, x_zot 3}", src);
	}
	
	@Test
	public void testTupleCompose1() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
				"var b tuple {w_zap char, y_zap boolean, z_zap rational, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a compose b";
		testEquals("TUPLE {w_zot \"test\", y_zot true, z_zot 2.5, x_zot 3, w_zap \"test\", y_zap true, z_zap 2.5, x_zap 3}", src);
	}

	@Test
	public void testTupleCompose2() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zap boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5};" +
				"var b tuple {w_zap char, y_zap boolean, z_zap rational, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a compose b";
		testEquals("TUPLE {w_zot \"test\", z_zot 2.5, x_zot 3, w_zap \"test\", z_zap 2.5, x_zap 3}", src);
	}
	
	@Test
	public void testTupleDisjointUnion1() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zot boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5};" +
				"var b tuple {w_zap char, y_zap boolean, z_zap rational, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a d_union b";
		testEquals("TUPLE {w_zot \"test\", y_zot true, z_zot 2.5, x_zot 3, w_zap \"test\", y_zap true, z_zap 2.5, x_zap 3}", src);
	}

	@Test
	public void testTupleSemijoin1() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zap boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5};" +
				"var b tuple {w_zap char, y_zap boolean, z_zap rational, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a semijoin b";
		testEquals("TUPLE {y_zap true}", src);
	}
	
	@Test
	public void testTupleSemijoin2() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zap boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5};" +
				"var b tuple {w_zap char, y_zap boolean, z_zap rational, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a matching b";
		testEquals("TUPLE {y_zap true}", src);
	}

	@Test
	public void testTupleSemiminus1() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zap boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5};" +
				"var b tuple {w_zap char, y_zap boolean, z_zap rational, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a semiminus b";
		testEquals("TUPLE {w_zot \"test\", z_zot 2.5, x_zot 3}", src);
	}
	
	@Test
	public void testTupleSemiminus2() {
		String src =
			"BEGIN;" +
				"var a tuple {w_zot char, y_zap boolean, z_zot rational, x_zot integer};" +
				"a := tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5};" +
				"var b tuple {w_zap char, y_zap boolean, z_zap rational, x_zap integer};" +
				"b := tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5};" +
			"END;" +
			"a not matching b";
		testEquals("TUPLE {w_zot \"test\", z_zot 2.5, x_zot 3}", src);
	}

	@Test
	public void testTupleSemiminus3() {
		String src =
			"tuple {x_zot 3, y_zap true, w_zot 'test', z_zot 2.5} semiminus tuple {x_zap 3, w_zap 'test', y_zap true, z_zap 2.5}";
		testEquals("TUPLE {x_zot 3, w_zot \"test\", z_zot 2.5}", src);
	}
	
	@Test
	public void testTupleWrap1() {
		String src =
			"tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5} wrap {w_zot, y_zap} as x";
		testEquals("TUPLE {x_zot 3, z_zot 2.5, x TUPLE {w_zot \"test\", y_zap true}}", src);
	}
	
	@Test
	public void testTupleWrap3() {
		String src =
			"tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5} wrap {ALL BUT} as x";
		testEquals("TUPLE {x TUPLE {x_zot 3, w_zot \"test\", y_zap true, z_zot 2.5}}", src);
	}
	
	@Test
	public void testTupleWrap4() {
		String src =
			"tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5} wrap {ALL BUT w_zot} as x";
		testEquals("TUPLE {w_zot \"test\", x TUPLE {x_zot 3, y_zap true, z_zot 2.5}}", src);
	}
	
	@Test
	public void testTupleUnwrap1() {
		String src =
			"(TUPLE {x TUPLE {w_zot \"test\", y_zap true}, y TUPLE {z_zot 2.5, x_zot 3}} unwrap x) unwrap y";
		testEquals("TUPLE {w_zot \"test\", y_zap true, z_zot 2.5, x_zot 3}", src);
	}
	
	@Test
	public void testTupleAttributeFrom1() {
		String src =
			"x FROM TUPLE {x TUPLE {w_zot \"test\", y_zap true}, y TUPLE {z_zot 2.5, x_zot 3}}";
		testEquals("TUPLE {w_zot \"test\", y_zap true}", src);		
	}
	
	@Test
	public void testNadicUnion1() {
		String src =
			"UNION {tuple {x 3}}";
		testEquals("TUPLE {x 3}", src);				
	}
	
	@Test
	public void testNadicUnion2() {
		String src =
			"UNION {tuple {x 3}, tuple {y 4.5}}";
		testEquals("TUPLE {x 3, y 4.5}", src);				
	}
	
	@Test
	public void testNadicUnion3() {
		String src =
			"UNION {tuple {x 3}, tuple {y 4.5}, tuple {z \"test\"}}";
		testEquals("TUPLE {x 3, y 4.5, z \"test\"}", src);				
	}
	
	@Test
	public void testNadicXunion1() {
		String src =
			"XUNION {tuple {x 3}}";
		testEquals("TUPLE {x 3}", src);				
	}
	
	@Test
	public void testNadicXunion2() {
		String src =
			"XUNION {tuple {x 3}, tuple {y 4.5}}";
		testEquals("TUPLE {x 3, y 4.5}", src);				
	}
	
	@Test
	public void testNadicXunion3() {
		String src =
			"XUNION {tuple {a 1, x 3}, tuple {x 3, b 2}, tuple {x 3, z \"test\"}}";
		testEquals("TUPLE {a 1, x 3, b 2, z \"test\"}", src);
	}

	@Test
	public void testNadicDUnion1() {
		String src =
			"D_UNION {tuple {x 3}}";
		testEquals("TUPLE {x 3}", src);				
	}
	
	@Test
	public void testNadicDUnion2() {
		String src =
			"D_UNION {tuple {x 3}, tuple {y 4.5}}";
		testEquals("TUPLE {x 3, y 4.5}", src);				
	}
	
	@Test
	public void testNadicDUnion3() {
		String src =
			"D_UNION {tuple {x 3}, tuple {y 4.5}, tuple {z \"test\"}}";
		testEquals("TUPLE {x 3, y 4.5, z \"test\"}", src);				
	}

	@Test
	public void testNadicIntersect1() {
		String src =
			"INTERSECT {tuple {x 3, y 4}, tuple {x 3, z 7}, tuple {x 3, q 2.5}, tuple {x 3, p 1}}";
		testEquals("TUPLE {x 3}", src);				
	}

	@Test
	public void testNadicIntersect2() {
		String src =
			"INTERSECT {tuple {x 3, y 4}, tuple {p 1, x 3}, tuple {x 3, z 7}}";
		testEquals("TUPLE {x 3}", src);				
	}
	
	@Test
	public void testNadicJoin1() {
		String src =
			"JOIN {}";
		testEquals("RELATION {} {\n\tTUPLE {}\n}", src);				
	}
	
	@Test
	public void testNadicJoin2() {
		String src =
			"JOIN {tuple {x 3}}";
		testEquals("TUPLE {x 3}", src);				
	}
	
	@Test
	public void testNadicJoin3() {
		String src =
			"JOIN {tuple {x 3}, tuple {y 4.5}}";
		testEquals("TUPLE {x 3, y 4.5}", src);				
	}
	
	@Test
	public void testNadicJoin4() {
		String src =
			"JOIN {tuple {x 3}, tuple {y 4.5}, tuple {z \"test\"}}";
		testEquals("TUPLE {x 3, y 4.5, z \"test\"}", src);				
	}
	
	@Test
	public void testNadicTimes1() {
		String src =
			"TIMES {}";
		testEquals("RELATION {} {\n\tTUPLE {}\n}", src);				
	}
	
	@Test
	public void testNadicTimes2() {
		String src =
			"TIMES {tuple {x 3}}";
		testEquals("TUPLE {x 3}", src);				
	}
	
	@Test
	public void testNadicTimes3() {
		String src =
			"TIMES {tuple {x 3}, tuple {y 4.5}}";
		testEquals("TUPLE {x 3, y 4.5}", src);				
	}
	
	@Test
	public void testNadicTimes4() {
		String src =
			"TIMES {tuple {x 3}, tuple {y 4.5}, tuple {z \"test\"}}";
		testEquals("TUPLE {x 3, y 4.5, z \"test\"}", src);				
	}
	
	@Test
	public void testNadicCompose1() {
		String src =
			"COMPOSE {}";
		testEquals("RELATION {} {\n\tTUPLE {}\n}", src);			
	}
	
	@Test
	public void testNadicCompose2() {
		String src =
			"COMPOSE {tuple {x 3}}";
		testEquals("TUPLE {x 3}", src);				
	}
	
	@Test
	public void testNadicCompose3() {
		String src =
			"COMPOSE {tuple {x 3}, tuple {y 4.5}}";
		testEquals("TUPLE {x 3, y 4.5}", src);				
	}
	
	@Test
	public void testNadicCompose4() {
		String src =
			"COMPOSE {tuple {x 3}, tuple {y 4.5}, tuple {z \"test\"}}";
		testEquals("TUPLE {x 3, y 4.5, z \"test\"}", src);				
	}
	
	@Test
	public void testNadicOps1() {
		String src = "JOIN {} = DEE";
		testEquals("true", src);
	}
	
	@Test
	public void testNadicOps2() {
		String src = "TIMES {} = DEE";
		testEquals("true", src);
	}
	
	@Test
	public void testNadicOps3() {
		String src = "COMPOSE {} = DEE";
		testEquals("true", src);
	}
	
	@Test
	public void testNadicOps4() {
		String src = "XUNION {} = TUPLE {}";
		testEquals("true", src);
	}
	
	@Test
	public void testNadicOps5() {
		String src = "D_UNION {} = TUPLE {}";
		testEquals("true", src);
	}
	
	@Test
	public void testNadicOps6() {
		String src = "UNION {} = TUPLE {}";
		testEquals("true", src);
	}
		
	@Test
	public void testNadicOps7() {
		String src = "XUNION {} {} = DUM";
		testEquals("true", src);
	}
	
	@Test
	public void testNadicOps8() {
		String src = "D_UNION {} {} = DUM";
		testEquals("true", src);
	}
	
	@Test
	public void testNadicOps9() {
		String src = "UNION {} {} = DUM";
		testEquals("true", src);
	}

	@Test
	public void testNadicOps10() {
  		String src = "INTERSECT {}";
  		String expected = 
  			"RS0114: Expression list for n-adic INTERSECT cannot be empty.\n" +
  			"Line 1\n\n";
  		try {
  			testEquals(expected, src);
  		} catch (ExceptionSemantic es) {
  			assertEquals(expected, es.getMessage().substring(0, expected.length()));
  		}
	}
	
	@Test
	public void testNadicOps11() {
  		String src = "INTERSECT {} {}";
  		String expected = 
  			"RS0114: Expression list for n-adic INTERSECT cannot be empty.\n" +
  			"Line 1\n\n";
  		try {
  			testEquals(expected, src);
  		} catch (ExceptionSemantic es) {
  			assertEquals(expected, es.getMessage().substring(0, expected.length()));
  		}
	}
	
	@Test
	public void testRelationUpdateExpression1() {
		String src =
			"UPDATE relation {tuple {x 3, y 4.5, z \"test\"}, tuple {x 2, y 4.5, z \"test\"}} : {y := 2.2, z := \"glub\"}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 2.2, z \"glub\"},\n\tTUPLE {x 2, y 2.2, z \"glub\"}\n}", src);
	}
	
	@Test
	public void testRelationUpdateExpression2() {
		String src =
			"UPDATE relation {tuple {x 3, y 4.5, z \"test\"}, tuple {x 2, y 4.5, z \"test\"}} : {x := 2, z := \"glub\"}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 2, y 4.5, z \"glub\"}\n}", src);
	}
	
	@Test
	public void testTupleUpdateExpression1() {
		String src =
			"UPDATE tuple {x 3, y 4.5, z \"test\"} : {x := 2, z := \"glub\"}";
		testEquals("TUPLE {x 2, y 4.5, z \"glub\"}", src);
	}
	
	@Test
	public void testTupleUpdateExpression2() {
		String src =
			"UPDATE tuple {x 3, y 4.5, z \"test\"} : {x := x + 1}";
		testEquals("TUPLE {x 4, y 4.5, z \"test\"}", src);				
	}
	
	@Test
	public void testTupleUpdateStatement1() {
		String src =
			"BEGIN;" +
				"VAR a INIT(tuple {x 3, y 4.5, z \"test\"});" +
				"UPDATE a : {x := x + 1, z := \"glub\"};" +
			"END;" +
			"a";
		testEquals("TUPLE {x 4, y 4.5, z \"glub\"}", src);		
	}
	
	@Test
	public void testSameTypeAs1() {
		String src =
			"BEGIN;" +
		        "VAR a INIT(tuple {x 3, y 4.5, z \"test\"});" +
		        "VAR b INIT(tuple {q 5, y 4.5, z \"test\"});" +
		        "VAR c SAME_TYPE_AS (a INTERSECT b);" +
				"c := TUPLE {y 2.2, z \"glub\"};" +
			"END;" +
			"c";
		testEquals("TUPLE {y 2.2, z \"glub\"}", src);		
	}
	
	@Test
	public void testSameHeadingAs1() {
		String src =
			"BEGIN;" +
		        "VAR a INIT(tuple {x 3, y 4.5, z \"test\"});" +
		        "VAR b INIT(tuple {q 5, y 4.5, z \"test\"});" +
		        "VAR c TUPLE SAME_HEADING_AS (a INTERSECT b);" +
				"c := TUPLE {y 2.2, z \"glub\"};" +
			"END;" +
			"c";
		testEquals("TUPLE {y 2.2, z \"glub\"}", src);		
	}

	@Test
	public void testRelation1() {
		String src =
			"TABLE_DUM";
		testEquals("RELATION {} {\n}", src);				
	}
	
	@Test
	public void testRelation2() {
		String src =
			"TABLE_DEE";
		testEquals("RELATION {} {\n\tTUPLE {}\n}", src);				
	}
	
	@Test
	public void testRelation3() {
		String src =
			"BEGIN;" +
				"VAR a PRIVATE RELATION {w char, x integer, y boolean, z rational} KEY {ALL BUT};" +
				"a := RELATION {" +
				"  TUPLE {w 'test', x 3, y true, z 2.5}, " +
				"  TUPLE {w 'glub', x 5, y false, z 3.5}" +
				"};" +
			"END;" +
			"a";
		testEquals("RELATION {w CHARACTER, x INTEGER, y BOOLEAN, z RATIONAL} {\n\tTUPLE {w \"glub\", x 5, y false, z 3.5},\n\tTUPLE {w \"test\", x 3, y true, z 2.5}\n}", src);
	}

	@Test
	public void testRelation3a() {
		String src =
			"BEGIN;" +
				"VAR a PRIVATE REL {w char, x integer, y boolean, z rational} KEY {ALL BUT};" +
				"a := REL {" +
				"  TUP {w 'test', x 3, y true, z 2.5}, " +
				"  TUP {w 'glub', x 5, y false, z 3.5}" +
				"};" +
			"END;" +
			"a";
		testEquals("RELATION {w CHARACTER, x INTEGER, y BOOLEAN, z RATIONAL} {\n\tTUPLE {w \"glub\", x 5, y false, z 3.5},\n\tTUPLE {w \"test\", x 3, y true, z 2.5}\n}", src);
	}

	@Test
	public void testRelation4() {
		String src =
			"BEGIN;" +
				"VAR a PRIVATE RELATION {w char, x integer, y boolean, z rational} KEY {ALL BUT};" +
				"a := RELATION {" +
				"  TUPLE {w 'test', x 3, y true, z 2.5}, " +
				"  TUPLE {y false, z 3.5, x 5, w 'glub'}" +
				"};" +
			"END;" +
			"a";
		testEquals("RELATION {w CHARACTER, x INTEGER, y BOOLEAN, z RATIONAL} {\n\tTUPLE {w \"glub\", x 5, y false, z 3.5},\n\tTUPLE {w \"test\", x 3, y true, z 2.5}\n}", src);
	}

	@Test
	public void testRelation5() {
		String src =
			"BEGIN;" +
				"VAR a PRIVATE RELATION {w char, x integer, y boolean, z rational} KEY {ALL BUT};" +
				"a := RELATION {" +
				"  TUPLE {w 'test', x 3, y true, z 2.5}," +
				"  TUPLE {y false, z 3.5, x 5, w 'glub'}," +
				"  TUPLE {y false, z 3.5, x 5, w 'glub'}" +
				"};" +
			"END;" +
			"a";
		testEquals("RELATION {w CHARACTER, x INTEGER, y BOOLEAN, z RATIONAL} {\n\tTUPLE {w \"glub\", x 5, y false, z 3.5},\n\tTUPLE {w \"test\", x 3, y true, z 2.5}\n}", src);
	}

	@Test
	public void testRelation6() {
		String src =
			"BEGIN;" +
				"VAR a PRIVATE RELATION {w char, x integer, y boolean, z rational} KEY {ALL BUT};" +
				"a := RELATION {" +
				"  TUPLE {w 'test', x 3, y true, z 2.5}, " +
				"  TUPLE {y false, z 3.5, x 5, w 'glub'}," +
				"  TUPLE {z 3.5, w 'glub', y false, x 5}" +
				"};" +
			"END;" +
			"a";
		testEquals("RELATION {w CHARACTER, x INTEGER, y BOOLEAN, z RATIONAL} {\n\tTUPLE {w \"glub\", x 5, y false, z 3.5},\n\tTUPLE {w \"test\", x 3, y true, z 2.5}\n}", src);
	}

	@Test
	public void testRelation7() {
		String src =
			"BEGIN;" +
				"VAR a PRIVATE INIT (RELATION {w char, x integer, y boolean, z rational} " +
				"{" +
				"  TUPLE {w 'test', x 3, y true, z 2.5}, " +
				"  TUPLE {y false, z 3.5, x 5, w 'glub'}," +
				"  TUPLE {z 3.5, w 'glub', y false, x 5}" +
				"}) KEY {ALL BUT};" +
			"END;" +
			"a";
		testEquals("RELATION {w CHARACTER, x INTEGER, y BOOLEAN, z RATIONAL} {\n\tTUPLE {w \"glub\", x 5, y false, z 3.5},\n\tTUPLE {w \"test\", x 3, y true, z 2.5}\n}", src);
	}
	
	@Test
	public void testRelation8() {
		String src =
			"BEGIN;" +
				"VAR a PRIVATE INIT (RELATION {" +
				"  TUPLE {w 'test', x 3, y true, z 2.5}, " +
				"  TUPLE {y false, z 3.5, x 5, w 'glub'}," +
				"  TUPLE {z 3.5, w 'glub', y false, x 5}" +
				"}) KEY {ALL BUT};" +
			"END;" +
			"a";
		testEquals("RELATION {w CHARACTER, x INTEGER, y BOOLEAN, z RATIONAL} {\n\tTUPLE {w \"glub\", x 5, y false, z 3.5},\n\tTUPLE {w \"test\", x 3, y true, z 2.5}\n}", src);
	}
	
	@Test
	public void testRelation9() {
		String src = 
			"relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}, tuple {a 2, b 'y'}}, x_zot 3}," + 
				"tuple {x_zot 4, w_zot relation {tuple {b 'z', a 5}, tuple {b 'y', a 2}}}" + 
			"}";
		String result = "RELATION {w_zot RELATION {a INTEGER, b CHARACTER}, x_zot INTEGER} {" +
			"\n\tTUPLE {w_zot RELATION {a INTEGER, b CHARACTER} {" +
			"\n\tTUPLE {a 1, b \"x\"}," +
			"\n\tTUPLE {a 2, b \"y\"}" +
			"\n}, x_zot 3}," +
			"\n\tTUPLE {w_zot RELATION {a INTEGER, b CHARACTER} {" +
			"\n\tTUPLE {a 5, b \"z\"}," +
			"\n\tTUPLE {a 2, b \"y\"}" +
			"\n}, x_zot 4}" +
			"\n}";
		testEquals(result, src);				
	}
	
	@Test
	public void testRelation10() {
		String src = 
			"BEGIN;" +
			"var a private relation {w_zot relation {a integer, b char}, x_zot integer} key {all but};" +
			"a := relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}, tuple {b 'y', a 2}}, x_zot 3}," + 
				"tuple {x_zot 4, w_zot relation {tuple {b 'z', a 5}, tuple {b 'y', a 2}}}" + 
			"};" +
			"END;" +
			"a";
		String result = "RELATION {w_zot RELATION {a INTEGER, b CHARACTER}, x_zot INTEGER} {" +
			"\n\tTUPLE {w_zot RELATION {a INTEGER, b CHARACTER} {" +
			"\n\tTUPLE {a 1, b \"x\"}," +
			"\n\tTUPLE {a 2, b \"y\"}" +
			"\n}, x_zot 3}," +
			"\n\tTUPLE {w_zot RELATION {a INTEGER, b CHARACTER} {" +
			"\n\tTUPLE {a 5, b \"z\"}," +
			"\n\tTUPLE {a 2, b \"y\"}" +
			"\n}, x_zot 4}" +
			"\n}";
		testEquals(result, src);				
	}
	
	@Test
	public void testRelation11() {
		String src = 
			"relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}}, x_zot 3}," + 
				"tuple {x_zot 4, w_zot relation {tuple {b 'z', a 5}}}" + 
			"}";
		String result = "RELATION {w_zot RELATION {a INTEGER, b CHARACTER}, x_zot INTEGER} {" +
			"\n\tTUPLE {w_zot RELATION {a INTEGER, b CHARACTER} {" +
			"\n\tTUPLE {a 1, b \"x\"}" +
			"\n}, x_zot 3}," +
			"\n\tTUPLE {w_zot RELATION {a INTEGER, b CHARACTER} {" +
			"\n\tTUPLE {a 5, b \"z\"}" +
			"\n}, x_zot 4}" +
			"\n}";
		testEquals(result, src);				
	}
	
	@Test
	public void testRelation12() {
		String src = 
			"relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}}}," + 
				"tuple {w_zot relation {tuple {b 'z', a 5}}}" + 
			"}";
		String result = "RELATION {w_zot RELATION {a INTEGER, b CHARACTER}} {" +
			"\n\tTUPLE {w_zot RELATION {a INTEGER, b CHARACTER} {" +
			"\n\tTUPLE {a 1, b \"x\"}" +
			"\n}}," +
			"\n\tTUPLE {w_zot RELATION {a INTEGER, b CHARACTER} {" +
			"\n\tTUPLE {a 5, b \"z\"}" +
			"\n}}" +
			"\n}";
		testEquals(result, src);				
	}
	
	@Test
	public void testRelation13() {
		String src =
				"RELATION {w char, x integer, y boolean, z rational} " +
				"{" +
				"  TUPLE {w 'test', x 3, y true, z 2.5}, " +
				"  TUPLE {y false, z 3.5, x 5, w 'glub'}," +
				"  TUPLE {z 3.5, w 'glub', y false, x 5}" +
				"}";
		testEquals("RELATION {w CHARACTER, x INTEGER, y BOOLEAN, z RATIONAL} {\n\tTUPLE {w \"test\", x 3, y true, z 2.5},\n\tTUPLE {w \"glub\", x 5, y false, z 3.5}\n}", src);
	}
	
	@Test
	public void testSameHeadingAs2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {tuple {x 3, y 4.5, z \"test\"}}) KEY {ALL BUT};" +
		        "VAR c TUPLE SAME_HEADING_AS (a);" +
				"c := TUPLE {y 2.2, z \"glub\", x 2};" +
			"END;" +
			"c";
		testEquals("TUPLE {x 2, y 2.2, z \"glub\"}", src);		
	}

	@Test
	public void testRelationProject1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a {x}";
		testEquals("RELATION {x INTEGER} {\n\tTUPLE {x 3}\n}", src);
	}

	@Test
	public void testRelationProject2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {x 3, y 2.8, z \"glob\"}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a {x}";
		testEquals("RELATION {x INTEGER} {\n\tTUPLE {x 3}\n}", src);
	}

	@Test
	public void testRelationProject3() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a {x}";
		testEquals("RELATION {x INTEGER} {\n\tTUPLE {x 3}\n}", src);
	}

	@Test
	public void testRelationProject4() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a {ALL BUT}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {" + 
				"\n\tTUPLE {x 3, y 2.8, z \"glob\"}," + 
				"\n\tTUPLE {x 3, y 4.5, z \"test\"}" +
				"\n}", src);
	}

	@Test
	public void testRelationProject5() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a {ALL BUT x}";
		testEquals("RELATION {y RATIONAL, z CHARACTER} {" + 
				"\n\tTUPLE {y 2.8, z \"glob\"}," + 
				"\n\tTUPLE {y 4.5, z \"test\"}" +
				"\n}", src);
	}

	@Test
	public void testRelationUnion1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a UNION b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 2.8, z \"glob\"},\n\tTUPLE {x 3, y 4.5, z \"test\"}\n}", src);
	}

	@Test
	public void testRelationUnion2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a UNION b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"},\n\tTUPLE {x 6, y 2.8, z \"glob\"},\n\tTUPLE {x 4, y 2.8, z \"glob\"},\n\tTUPLE {x 5, y 4.5, z \"test\"}\n}", src);
	}

	@Test
	public void testRelationXunion1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a XUNION b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n}", src);
	}

	@Test
	public void testRelationXunion2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a XUNION b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"},\n\tTUPLE {x 5, y 4.5, z \"test\"}\n}", src);
	}
	
	@Test
	public void testRelationDUnion1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a D_UNION b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"},\n\tTUPLE {x 6, y 2.8, z \"glob\"},\n\tTUPLE {x 4, y 2.8, z \"glob\"},\n\tTUPLE {x 5, y 4.5, z \"test\"}\n}", src);
	}
	
	@Test
	public void testRelationIntersect1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a INTERSECT b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n}", src);
	}
	
	@Test
	public void testRelationIntersect2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"test\", x 3, y 4.5}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a INTERSECT b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"}\n}", src);
	}
	
	@Test
	public void testRelationMinus1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a MINUS b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"},\n\tTUPLE {x 6, y 2.8, z \"glob\"}\n}", src);
	}
	
	@Test
	public void testRelationMinus2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"test\", x 3, y 4.5}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a MINUS b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 6, y 2.8, z \"glob\"}\n}", src);
	}

	@Test
	public void testRelationIMinus1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a I_MINUS b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"}\n}", src);
	}
	
	@Test
	public void testRelationJoin1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5}," +
		        "       tuple {x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}," +
		        "       tuple {a 4.6, b 9}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a JOIN b";
		testEquals("RELATION {x INTEGER, y RATIONAL, a RATIONAL, b INTEGER} {\n\tTUPLE {x 3, y 4.5, a 4.5, b 7},\n\tTUPLE {x 3, y 4.5, a 4.6, b 9},\n\tTUPLE {x 3, y 4.5, a 4.8, b 8},\n\tTUPLE {x 6, y 2.8, a 4.5, b 7},\n\tTUPLE {x 6, y 2.8, a 4.6, b 9},\n\tTUPLE {x 6, y 2.8, a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationJoin2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a JOIN b";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationJoin3() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"b JOIN a";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationJoin4() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"b JOIN a";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationJoin5() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a JOIN b";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationJoin6() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, a 4.5, b \"test\"}," +
		        "       tuple {b \"zot\", x 3, a 4.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a JOIN b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER, a RATIONAL, b CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\", a 4.8, b \"zot\"}\n}", src);
	}

	@Test
	public void testRelationTimes1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5}," +
		        "       tuple {x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}," +
		        "       tuple {a 4.6, b 9}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a TIMES b";
		testEquals("RELATION {x INTEGER, y RATIONAL, a RATIONAL, b INTEGER} {\n\tTUPLE {x 3, y 4.5, a 4.5, b 7},\n\tTUPLE {x 3, y 4.5, a 4.6, b 9},\n\tTUPLE {x 3, y 4.5, a 4.8, b 8},\n\tTUPLE {x 6, y 2.8, a 4.5, b 7},\n\tTUPLE {x 6, y 2.8, a 4.6, b 9},\n\tTUPLE {x 6, y 2.8, a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationTimes2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a TIMES b";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationTimes3() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"b TIMES a";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationTimes4() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"b TIMES a";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationTimes5() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a TIMES b";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}
	
	@Test
	public void testRelationCompose1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5}," +
		        "       tuple {x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}," +
		        "       tuple {a 4.6, b 9}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a COMPOSE b";
		testEquals("RELATION {x INTEGER, y RATIONAL, a RATIONAL, b INTEGER} {\n\tTUPLE {x 3, y 4.5, a 4.5, b 7},\n\tTUPLE {x 3, y 4.5, a 4.6, b 9},\n\tTUPLE {x 3, y 4.5, a 4.8, b 8},\n\tTUPLE {x 6, y 2.8, a 4.5, b 7},\n\tTUPLE {x 6, y 2.8, a 4.6, b 9},\n\tTUPLE {x 6, y 2.8, a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationCompose2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a COMPOSE b";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationCompose3() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"b COMPOSE a";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationCompose4() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"b COMPOSE a";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationCompose5() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a COMPOSE b";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationCompose6() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, a 4.5, b \"test\"}," +
		        "       tuple {b \"zot\", x 3, a 4.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a COMPOSE b";
		testEquals("RELATION {y RATIONAL, z CHARACTER, a RATIONAL, b CHARACTER} {\n\tTUPLE {y 4.5, z \"test\", a 4.8, b \"zot\"}\n}", src);
	}
	
	@Test
	public void testRelationSemijoin1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5}," +
		        "       tuple {x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}," +
		        "       tuple {a 4.6, b 9}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a MATCHING b";
		testEquals("RELATION {x INTEGER, y RATIONAL} {\n\tTUPLE {x 3, y 4.5},\n\tTUPLE {x 6, y 2.8}\n}", src);
	}

	@Test
	public void testRelationSemijoin2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a MATCHING b";
		testEquals("RELATION {} {\n}", src);
	}

	@Test
	public void testRelationSemijoin3() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"b MATCHING a";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationSemijoin4() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"b MATCHING a";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationSemijoin5() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a MATCHING b";
		testEquals("RELATION {} {\n\tTUPLE {}\n}", src);
	}

	@Test
	public void testRelationSemijoin6() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, a 4.5, b \"test\"}," +
		        "       tuple {b \"zot\", x 3, a 4.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a MATCHING b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"}\n}", src);
	}

	@Test
	public void testRelationSemiminus1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5}," +
		        "       tuple {x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}," +
		        "       tuple {a 4.6, b 9}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a NOT MATCHING b";
		testEquals("RELATION {x INTEGER, y RATIONAL} {\n}", src);
	}

	@Test
	public void testRelationSemiminus2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a NOT MATCHING b";
		testEquals("RELATION {} {\n}", src);
	}

	@Test
	public void testRelationSemiminus3() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"b NOT MATCHING a";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationSemiminus4() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"b NOT MATCHING a";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationSemiminus5() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a NOT MATCHING b";
		testEquals("RELATION {} {\n}", src);
	}

	@Test
	public void testRelationSemiminus6() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, a 4.5, b \"test\"}," +
		        "       tuple {b \"zot\", x 3, a 4.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"a NOT MATCHING b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 6, y 2.8, z \"glob\"}\n}", src);
	}
	
	@Test
	public void testRelationExtend1() {
		String src = "extend relation {tuple {x 3, y 2.5, z 'test'}} : {a := 3 + 4, b := 'glub'}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER, a INTEGER, b CHARACTER} {\n\tTUPLE {x 3, y 2.5, z \"test\", a 7, b \"glub\"}\n}", src);
	}

	@Test
	public void testRelationExtend2() {
		String src = "extend relation {tuple {x 3, y 2, z 6}} : {a := x + z, b := x + y}";
		testEquals("RELATION {x INTEGER, y INTEGER, z INTEGER, a INTEGER, b INTEGER} {\n\tTUPLE {x 3, y 2, z 6, a 9, b 5}\n}", src);
	}

	@Test
	public void testRelationExtend3() {
		String src = "extend relation {tuple {x 3, y 2.5, z 'test'}, tuple {y 7.1, z 'zot', x 5}} : {a := 3 + 4, b := 'glub'}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER, a INTEGER, b CHARACTER} {\n\tTUPLE {x 3, y 2.5, z \"test\", a 7, b \"glub\"},\n\tTUPLE {x 5, y 7.1, z \"zot\", a 7, b \"glub\"}\n}", src);
	}

	@Test
	public void testRelationExtend4() {
		String src = "extend relation {tuple {x 3, y 2, z 6}, tuple {y 7, z 1, x 5}} : {a := x + z, b := x + y}";
		testEquals("RELATION {x INTEGER, y INTEGER, z INTEGER, a INTEGER, b INTEGER} {\n\tTUPLE {x 3, y 2, z 6, a 9, b 5},\n\tTUPLE {x 5, y 7, z 1, a 6, b 12}\n}", src);
	}
	
	@Test
	public void testRelationWrap1() {
		String src =
			"relation {tuple {x_zot 3, w_zot 'test', y_zap true, z_zot 2.5}} wrap {w_zot, y_zap} as x";
		testEquals("RELATION {x_zot INTEGER, z_zot RATIONAL, x TUPLE {w_zot CHARACTER, y_zap BOOLEAN}} {\n\tTUPLE {x_zot 3, z_zot 2.5, x TUPLE {w_zot \"test\", y_zap true}}\n}", src);
	}
	
	@Test
	public void testRelationUnwrap1() {
		String src =
			"(RELATION {TUPLE {x TUPLE {w_zot \"test\", y_zap true}, y TUPLE {z_zot 2.5, x_zot 3}}} unwrap x) unwrap y";
		testEquals("RELATION {w_zot CHARACTER, y_zap BOOLEAN, z_zot RATIONAL, x_zot INTEGER} {\n\tTUPLE {w_zot \"test\", y_zap true, z_zot 2.5, x_zot 3}\n}", src);
	}

	@Test
	public void testRelationFrom() {
		String src =
			"TUPLE FROM RELATION {TUPLE {z_zot 2.5, x_zot 3}}";
		testEquals("TUPLE {z_zot 2.5, x_zot 3}", src);		
	}
	
	@Test
	public void testTupleIn1() {
		String src = "TUPLE {x 'glub', y 3} IN RELATION {" +
		" TUPLE {y 3, x 'glub'}," + 
		" TUPLE {x 'glob', y 7}" + 
		"}";
		testEquals("true", src);
	}

	@Test
	public void testTupleIn2() {
		String src = "TUPLE {x 'glub', y 3} IN RELATION {" +
		" TUPLE {y 4, x 'glub'}," + 
		" TUPLE {x 'glob', y 7}" + 
		"}";
		testEquals("false", src);
	}
	
	@Test
	public void testTupleIn3() {
		String src = "TUPLE {y 3, x 'glub'} IN RELATION {" +
		" TUPLE {x 'glub', y 3}," + 
		" TUPLE {x 'glob', y 7}" + 
		"}";
		testEquals("true", src);
	}
	
	@Test
	public void testTupleIn4() {
		String src = "TUPLE {Flump \"Dave\", Poople 3, Blimp true} IN RELATION {" +
		 " TUPLE {Blimp true, Flump \"Dave\", Poople 3}," +
		 " TUPLE {Blimp true, Flump \"Calvin\", Poople 7}," +
		 " TUPLE {Blimp false, Flump \"Indi\", Poople 8}" +
		 "}";
		testEquals("true", src);
	}

	
	@Test
	public void testRelationWhere1() {
		String src =
			"RELATION {TUPLE {z_zot 2.5, x_zot 3}, TUPLE {z_zot 2.6, x_zot 5}} WHERE x_zot = 3";
		testEquals("RELATION {z_zot RATIONAL, x_zot INTEGER} {\n\tTUPLE {z_zot 2.5, x_zot 3}\n}", src);		
	}

	@Test
	public void testRelationWhere2() {
		String src =
			"RELATION {TUPLE {z_zot 2.5, x_zot 3}, TUPLE {z_zot 2.6, x_zot 5}} WHERE x_zot = 3 AND z_zot > 2.0";
		testEquals("RELATION {z_zot RATIONAL, x_zot INTEGER} {\n\tTUPLE {z_zot 2.5, x_zot 3}\n}", src);		
	}

	@Test
	public void testRelationWhere3() {
		String src =
			"RELATION {TUPLE {z_zot 2.5, x_zot 3}, TUPLE {z_zot 2.6, x_zot 5}} WHERE x_zot = 3 AND z_zot < 2.0";
		testEquals("RELATION {z_zot RATIONAL, x_zot INTEGER} {\n}", src);		
	}

	@Test
	public void testWith1() {
		String src =
			"WITH (a := RELATION {TUPLE {z_zot 2.5, x_zot 3}}, b := RELATION {TUPLE {z_zot 2.6, x_zot 5}}) : a union b";
		testEquals("RELATION {z_zot RATIONAL, x_zot INTEGER} {\n\tTUPLE {z_zot 2.5, x_zot 3},\n\tTUPLE {z_zot 2.6, x_zot 5}\n}", src);		
	}
	
	@Test
	public void testWith2() {
		String src = "WITH (a := 3, b := 5) : a + b";
		String expectedResult = "8";
		testEquals(expectedResult, src);
	}
	
	@Test
	public void testRelComp1() {
		String src = "RELATION {" +
        "  TUPLE {q 300, s 's2', p 'p1'}," +
        "  TUPLE {s 's2', p 'p2', q 400}," +
        "  TUPLE {s 's3', p 'p2', q 200}} = " +
		"RELATION {" +
        "  TUPLE {p 'p2', s 's2', q 400}," +
        "  TUPLE {p 'p1', s 's2', q 300}," +
        "  TUPLE {s 's3', p 'p2', q 200}}";
		testEquals("true", src);
	}
	
	@Test
	public void testRelComp2() {
		String src = "RELATION {" +
        "  TUPLE {q 300, s 's2', p 'p1'}," +
        "  TUPLE {s 's2', p 'p2', q 400}," +
        "  TUPLE {s 's3', p 'p2', q 200}} <> " +
		"RELATION {" +
        "  TUPLE {p 'p2', s 's2', q 400}," +
        "  TUPLE {p 'p1', s 's2', q 300}," +
        "  TUPLE {s 's3', p 'p2', q 200}}";
		testEquals("false", src);
	}
	
	@Test
	public void testRelComp3() {
		String src = "RELATION {" +
        "  TUPLE {q 300, s 's2', p 'p1'}," +
        "  TUPLE {s 's2', p 'p2', q 400}," +
        "  TUPLE {s 's3', p 'p2', q 200}} < " +
		"RELATION {" +
        "  TUPLE {p 'p2', s 's2', q 400}," +
        "  TUPLE {p 'p1', s 's2', q 300}," +
        "  TUPLE {s 's3', p 'p2', q 200}}";
		testEquals("false", src);
	}
	
	@Test
	public void testRelComp4() {
		String src = "RELATION {" +
        "  TUPLE {q 300, s 's2', p 'p1'}," +
        "  TUPLE {s 's3', p 'p2', q 200}} < " +
		"RELATION {" +
        "  TUPLE {p 'p2', s 's2', q 400}," +
        "  TUPLE {p 'p1', s 's2', q 300}," +
        "  TUPLE {s 's3', p 'p2', q 200}}";
		testEquals("true", src);
	}
	
	@Test
	public void testRelComp4a() {
		String src = "RELATION {" +
        "  TUPLE {q 300, s 's2', p 'p1'}," +
        "  TUPLE {s 's3', p 'p2', q 200}} ⊂ " +
		"RELATION {" +
        "  TUPLE {p 'p2', s 's2', q 400}," +
        "  TUPLE {p 'p1', s 's2', q 300}," +
        "  TUPLE {s 's3', p 'p2', q 200}}";
		testEquals("true", src);
	}
	
	@Test
	public void testRelComp5() {
		String src = "RELATION {" +
        "  TUPLE {q 300, s 's2', p 'p1'}," +
        "  TUPLE {s 's2', p 'p2', q 400}," +
        "  TUPLE {s 's3', p 'p2', q 200}} <= " +
		"RELATION {" +
        "  TUPLE {p 'p2', s 's2', q 400}," +
        "  TUPLE {p 'p1', s 's2', q 300}," +
        "  TUPLE {s 's3', p 'p2', q 200}}";
		testEquals("true", src);
	}
	
	@Test
	public void testRelComp5a() {
		String src = "RELATION {" +
        "  TUPLE {q 300, s 's2', p 'p1'}," +
        "  TUPLE {s 's2', p 'p2', q 400}," +
        "  TUPLE {s 's3', p 'p2', q 200}} ⊆ " +
		"RELATION {" +
        "  TUPLE {p 'p2', s 's2', q 400}," +
        "  TUPLE {p 'p1', s 's2', q 300}," +
        "  TUPLE {s 's3', p 'p2', q 200}}";
		testEquals("true", src);
	}
	
	@Test
	public void testGroup1() {
		String src = "RELATION {" +
        "  TUPLE {s 's2', p 'p1', q 300}," +
        "  TUPLE {s 's2', p 'p2', q 400}," +
        "  TUPLE {s 's3', p 'p2', q 200}} GROUP {p, q} AS pq";
		testEquals("RELATION {s CHARACTER, pq RELATION {p CHARACTER, q INTEGER}} {\n\t" +
			"TUPLE {s \"s3\", pq RELATION {p CHARACTER, q INTEGER} {\n\t" +
			"TUPLE {p \"p2\", q 200}\n" +
			"}},\n\t" +
			"TUPLE {s \"s2\", pq RELATION {p CHARACTER, q INTEGER} {\n\t" +
			"TUPLE {p \"p1\", q 300},\n\t" +
			"TUPLE {p \"p2\", q 400}\n" +
			"}}\n" +
			"}", src);
	}

	@Test
	public void testGroup2() {
		String src = "RELATION {" +
        "  TUPLE {s 's2', p 'p1', q 300}," +
        "  TUPLE {s 's2', p 'p2', q 400}," +
        "  TUPLE {s 's3', p 'p2', q 200}} GROUP {ALL BUT s} AS pq";
		testEquals("RELATION {s CHARACTER, pq RELATION {p CHARACTER, q INTEGER}} {\n\t" + 
				"TUPLE {s \"s3\", pq RELATION {p CHARACTER, q INTEGER} {\n\tTUPLE {p \"p2\", q 200}\n}},\n\t" + 
				"TUPLE {s \"s2\", pq RELATION {p CHARACTER, q INTEGER} {\n\tTUPLE {p \"p1\", q 300},\n\tTUPLE {p \"p2\", q 400}\n}}\n" +
					"}", src);
	}

	@Test
	public void testGroup3() {
		String src = "RELATION {" +
        "  TUPLE {s 's2', p 'p1', q 300}," +
        "  TUPLE {s 's2', p 'p2', q 400}," +
        "  TUPLE {s 's3', p 'p2', q 200}} GROUP {ALL BUT} AS pq";
		testEquals("RELATION {pq RELATION {s CHARACTER, p CHARACTER, q INTEGER}} {\n\tTUPLE {" + 
				"pq RELATION {s CHARACTER, p CHARACTER, q INTEGER} {\n\tTUPLE {s \"s2\", p \"p1\", q 300}," + 
				             "\n\tTUPLE {s \"s2\", p \"p2\", q 400}," +
				             "\n\tTUPLE {s \"s3\", p \"p2\", q 200}\n}}" + 
					"\n}", src);
	}

	@Test
	public void testUnGroup1() {
		String src = "(RELATION {" +
        "  TUPLE {s 's2', p 'p1', q 300}," +
        "  TUPLE {s 's2', p 'p2', q 400}," +
        "  TUPLE {s 's3', p 'p2', q 200}} GROUP {p, q} AS pq) UNGROUP pq";
		testEquals("RELATION {s CHARACTER, p CHARACTER, q INTEGER} {" +
				"\n\tTUPLE {s \"s3\", p \"p2\", q 200}," +
				"\n\tTUPLE {s \"s2\", p \"p1\", q 300}," +
				"\n\tTUPLE {s \"s2\", p \"p2\", q 400}\n}", src);
	}
	
	@Test
	public void testRelationRename1() {
		String src =
			"BEGIN;" +
				"var a private relation {w char, y boolean, z rational, x integer} key {all but};" +
				"a := relation {tuple {x 3, w 'test', y true, z 2.5}};" +
			"END;" +
			"a rename {w as woggle, z as zork}";
		testEquals("RELATION {woggle CHARACTER, y BOOLEAN, zork RATIONAL, x INTEGER} {\n\tTUPLE {woggle \"test\", y true, zork 2.5, x 3}\n}", src);	
	}

	@Test
	public void testRelationRename2() {
		String src =
			"BEGIN;" +
				"var a private relation {zot_w char, zot_y boolean, zot_z rational, zot_x integer} key {all but};" +
				"a := relation {tuple {zot_x 3, zot_w 'test', zot_y true, zot_z 2.5}};" +
			"END;" +
			"a rename {prefix 'zot' as 'zap'}";
		testEquals("RELATION {zap_w CHARACTER, zap_y BOOLEAN, zap_z RATIONAL, zap_x INTEGER} {\n\tTUPLE {zap_w \"test\", zap_y true, zap_z 2.5, zap_x 3}\n}", src);	
	}
	
	@Test
	public void testRelationRename3() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, y_zot boolean, z_zot rational, x_zot integer} key {all but};" +
				"a := relation {tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5}};" +
			"END;" +
			"a rename {suffix 'zot' as 'zap'}";
		testEquals("RELATION {w_zap CHARACTER, y_zap BOOLEAN, z_zap RATIONAL, x_zap INTEGER} {\n\tTUPLE {w_zap \"test\", y_zap true, z_zap 2.5, x_zap 3}\n}", src);	
	}
	
	@Test
	public void testRelationIsEmpty1() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, y_zot boolean, z_zot rational, x_zot integer} key {all but};" +
				"a := relation {tuple {x_zot 3, w_zot 'test', y_zot true, z_zot 2.5}};" +
			"END;" +
			"IS_EMPTY(a)";
		testEquals("false", src);	
	}
	
	@Test
	public void testRelationIsEmpty2() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, y_zot boolean, z_zot rational, x_zot integer} key {all but};" +
			"END;" +
			"IS_EMPTY(a)";
		testEquals("true", src);	
	}
	
	@Test
	public void testRelationCount1() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, y_zot boolean, z_zot rational, x_zot integer} key {w_zot};" +
			"END;" +
			"COUNT(a)";
		testEquals("0", src);	
	}
		
	@Test
	public void testRelationCount2() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, x_zot integer} key {w_zot};" +
				"a := relation {tuple {w_zot 'blah', x_zot 2}, tuple {x_zot 3, w_zot 'zog'}};" + 
			"END;" +
			"COUNT(a)";
		testEquals("2", src);	
	}
		
	@Test
	public void testRelationSum1() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, y_zot boolean, z_zot rational, x_zot integer} key {w_zot};" +
			"END;" +
			"SUM(a, x_zot)";
		testEquals("0", src);	
	}
	
	@Test
	public void testRelationSum2() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, x_zot integer} key {w_zot};" +
				"a := relation {tuple {w_zot 'blah', x_zot 2}, tuple {x_zot 3, w_zot 'zog'}};" + 
			"END;" +
			"SUM(a, x_zot)";
		testEquals("5", src);	
	}
	
	@Test
	public void testRelationSum2a() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, x_zot integer} key {w_zot};" +
				"a := relation {tuple {w_zot 'blah', x_zot 2}, tuple {x_zot 3, w_zot 'zog'}};" + 
			"END;" +
			"SUM(a, x_zot + 1)";
		testEquals("7", src);	
	}

	@Test
	public void testRelationSum3() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, x_zot rational} key {w_zot};" +
				"a := relation {tuple {w_zot 'blah', x_zot 2.4}, tuple {x_zot 3.1, w_zot 'zog'}};" + 
			"END;" +
			"SUM(a, x_zot)";
		testEquals("5.5", src);	
	}

	@Test
	public void testRelationAvg1() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, x_zot integer} key {w_zot};" +
				"a := relation {tuple {w_zot 'blah', x_zot 2}, tuple {x_zot 3, w_zot 'zog'}};" + 
			"END;" +
			"AVG(a, x_zot)";
		testEquals("2.5", src);	
	}

	@Test
	public void testRelationAvg1a() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, x_zot integer} key {w_zot};" +
				"a := relation {tuple {w_zot 'blah', x_zot 2}, tuple {x_zot 3, w_zot 'zog'}};" + 
			"END;" +
			"AVG(a, x_zot + 1)";
		testEquals("3.5", src);	
	}

	@Test
	public void testRelationMax1() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, x_zot integer} key {w_zot};" +
				"a := relation {tuple {w_zot 'blah', x_zot 2}, tuple {x_zot 3, w_zot 'zog'}};" + 
			"END;" +
			"MAX(a, x_zot)";
		testEquals("3", src);	
	}

	@Test
	public void testRelationMin1() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot char, x_zot integer} key {w_zot};" +
				"a := relation {tuple {w_zot 'blah', x_zot 2}, tuple {x_zot 3, w_zot 'zog'}};" + 
			"END;" +
			"MIN(a, x_zot)";
		testEquals("2", src);	
	}

	@Test
	public void testRelationAnd1() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot true, x_zot 2}, tuple {x_zot 3, w_zot true}};" + 
			"END;" +
			"AND(a, w_zot)";
		testEquals("true", src);	
	}

	@Test
	public void testRelationAnd2() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot false, x_zot 2}, tuple {x_zot 3, w_zot true}};" + 
			"END;" +
			"AND(a, w_zot)";
		testEquals("false", src);	
	}

	@Test
	public void testRelationAnd3() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot false, x_zot 2}, tuple {x_zot 3, w_zot false}};" + 
			"END;" +
			"AND(a, w_zot)";
		testEquals("false", src);	
	}

	@Test
	public void testRelationAnd4() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
			"END;" +
			"AND(a, w_zot)";
		testEquals("true", src);	
	}
	
	@Test
	public void testRelationOr1() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot true, x_zot 2}, tuple {x_zot 3, w_zot true}};" + 
			"END;" +
			"OR(a, w_zot)";
		testEquals("true", src);	
	}

	@Test
	public void testRelationOr2() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot false, x_zot 2}, tuple {x_zot 3, w_zot true}};" + 
			"END;" +
			"OR(a, w_zot)";
		testEquals("true", src);	
	}

	@Test
	public void testRelationOr3() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot false, x_zot 2}, tuple {x_zot 3, w_zot false}};" + 
			"END;" +
			"OR(a, w_zot)";
		testEquals("false", src);	
	}

	@Test
	public void testRelationOr4() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
			"END;" +
			"OR(a, w_zot)";
		testEquals("false", src);	
	}

	@Test
	public void testRelationXor1() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot true, x_zot 2}, tuple {x_zot 3, w_zot true}};" + 
			"END;" +
			"XOR(a, w_zot)";
		testEquals("false", src);	
	}

	@Test
	public void testRelationXor2() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot false, x_zot 2}, tuple {x_zot 3, w_zot true}};" + 
			"END;" +
			"XOR(a, w_zot)";
		testEquals("true", src);	
	}

	@Test
	public void testRelationXor3() {
		String src =
			"BEGIN;" +
				"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
				"a := relation {tuple {w_zot false, x_zot 2}, tuple {x_zot 3, w_zot false}};" + 
			"END;" +
			"XOR(a, w_zot)";
		testEquals("false", src);	
	}

	@Test
	public void testRelationXor4() {
		String src =
			"BEGIN;" +
			"var a private relation {w_zot boolean, x_zot integer} key {x_zot};" +
		"END;" +
		"XOR(a, w_zot)";
		testEquals("false", src);	
	}
	
	@Test
	public void testRelationAggregateUnion1() {
		String src = 
			"BEGIN;" +
			"var a private relation {w_zot relation {a integer, b char}, x_zot integer} key {x_zot};" +
			"a := relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}, tuple {a 2, b 'y'}}, x_zot 3}," + 
				"tuple {w_zot relation {tuple {a 5, b 'z'}, tuple {a 2, b 'y'}}, x_zot 4}" + 
			"};" +
			"END;" +
			"UNION(a, w_zot)";
		testEquals("RELATION {a INTEGER, b CHARACTER} {\n\tTUPLE {a 1, b \"x\"},\n\tTUPLE {a 2, b \"y\"},\n\tTUPLE {a 5, b \"z\"}\n}", src);				
	}

	@Test
	public void testRelationAggregateUnion2() {
		String src = 
			"BEGIN;" +
			"var a private relation {w_zot relation {a integer, b char}, x_zot integer} key {x_zot};" +
			"a := relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}, tuple {a 2, b 'y'}}, x_zot 3}," + 
				"tuple {x_zot 4, w_zot relation {tuple {b 'z', a 5}, tuple {b 'y', a 2}}}" + 
			"};" +
			"END;" +
			"UNION(a, w_zot)";
		testEquals("RELATION {a INTEGER, b CHARACTER} {\n\tTUPLE {a 1, b \"x\"},\n\tTUPLE {a 2, b \"y\"},\n\tTUPLE {a 5, b \"z\"}\n}", src);				
	}
	
	@Test
	public void testRelationAggregateXunion1() {
		String src = 
			"BEGIN;" +
			"var a private relation {w_zot relation {a integer, b char}, x_zot integer} key {x_zot};" +
			"a := relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}, tuple {a 2, b 'y'}}, x_zot 3}," + 
				"tuple {w_zot relation {tuple {a 5, b 'z'}, tuple {a 2, b 'y'}}, x_zot 4}" + 
			"};" +
			"END;" +
			"XUNION(a, w_zot)";
		testEquals("RELATION {a INTEGER, b CHARACTER} {\n\tTUPLE {a 1, b \"x\"},\n\tTUPLE {a 5, b \"z\"}\n}", src);				
	}

	@Test
	public void testRelationAggregateXunion2() {
		String src = 
			"BEGIN;" +
			"var a private relation {w_zot relation {a integer, b char}, x_zot integer} key {x_zot};" +
			"a := relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}, tuple {a 2, b 'y'}}, x_zot 3}," + 
				"tuple {x_zot 4, w_zot relation {tuple {b 'z', a 5}, tuple {b 'y', a 2}}}" + 
			"};" +
			"END;" +
			"XUNION(a, w_zot)";
		testEquals("RELATION {a INTEGER, b CHARACTER} {\n\tTUPLE {a 1, b \"x\"},\n\tTUPLE {a 5, b \"z\"}\n}", src);				
	}
	
	@Test
	public void testRelationAggregateDUnion1() {
		String src = 
			"BEGIN;" +
			"var a private relation {w_zot relation {a integer, b char}, x_zot integer} key {x_zot};" +
			"a := relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}, tuple {a 2, b 'y'}}, x_zot 3}," + 
				"tuple {w_zot relation {tuple {a 5, b 'z'}}, x_zot 4}" + 
			"};" +
			"END;" +
			"D_UNION(a, w_zot)";
		String expectedResult = 
			"RELATION {a INTEGER, b CHARACTER} {" +
			"\n\tTUPLE {a 1, b \"x\"}," +
			"\n\tTUPLE {a 2, b \"y\"}," +
			"\n\tTUPLE {a 5, b \"z\"}" +
			"\n}";
		testEquals(expectedResult, src);				
	}

	@Test
	public void testRelationAggregateDUnion2() {
		String src = 
			"BEGIN;" +
			"var a private relation {w_zot relation {a integer, b char}, x_zot integer} key {x_zot};" +
			"a := relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}, tuple {a 2, b 'y'}}, x_zot 3}," + 
				"tuple {x_zot 4, w_zot relation {tuple {b 'z', a 5}}}" + 
			"};" +
			"END;" +
			"D_UNION(a, w_zot)";
		testEquals("RELATION {a INTEGER, b CHARACTER} {\n\tTUPLE {a 1, b \"x\"},\n\tTUPLE {a 2, b \"y\"},\n\tTUPLE {a 5, b \"z\"}\n}", src);				
	}
	
	@Test
	public void testRelationAggregateIntersect() {
		String src = 
			"BEGIN;" +
			"var a private relation {w_zot relation {a integer, b char}, x_zot integer} key {x_zot};" +
			"a := relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}, tuple {a 2, b 'y'}}, x_zot 3}," + 
				"tuple {w_zot relation {tuple {a 5, b 'z'}, tuple {a 2, b 'y'}}, x_zot 4}" + 
			"};" +
			"END;" +
			"INTERSECT(a, w_zot)";
		testEquals("RELATION {a INTEGER, b CHARACTER} {\n\tTUPLE {a 2, b \"y\"}\n}", src);				
	}

	@Test
	public void testRelationAggregateIntersect2() {
		String src = 
			"BEGIN;" +
			"var a private relation {w_zot relation {a integer, b char}, x_zot integer} key {x_zot};" +
			"a := relation {" + 
				"tuple {w_zot relation {tuple {a 1, b 'x'}, tuple {a 2, b 'y'}}, x_zot 3}," + 
				"tuple {x_zot 4, w_zot relation {tuple {b 'z', a 5}, tuple {b 'y', a 2}}}" + 
			"};" +
			"END;" +
			"INTERSECT(a, w_zot)";
		testEquals("RELATION {a INTEGER, b CHARACTER} {\n\tTUPLE {a 2, b \"y\"}\n}", src);				
	}

	@Test
	public void testAggregateExactly1() {
		String src = 
			"BEGIN;" +
				"var a private relation {x char, y boolean} key {x};" +
			"END;" +
			"EXACTLY(0, a, y)";
		testEquals("true", src);				
	}

	@Test
	public void testAggregateExactly2() {
		String src = 
			"BEGIN;" +
			"var a private relation {x char, y boolean} key {x};" +
			"a := relation {" + 
				"tuple {y false, x 'blah'}" +
			"};" +
			"END;" +
			"EXACTLY(0, a, y)";
		testEquals("true", src);				
	}
	
	@Test
	public void testAggregateExactly3() {
		String src = 
			"BEGIN;" +
			"var a private relation {x char, y boolean} key {x};" +
			"a := relation {" + 
				"tuple {y true, x 'blah'}" +
			"};" +
			"END;" +
			"EXACTLY(0, a, y)";
		testEquals("false", src);				
	}
	
	@Test
	public void testAggregateExactly4() {
		String src = 
			"BEGIN;" +
			"var a private relation {x char, y boolean} key {x};" +
			"a := relation {" + 
				"tuple {y true, x 'blah'}," +
				"tuple {y false, x 'blat'}" +
			"};" +
			"END;" +
			"EXACTLY(1, a, y)";
		testEquals("true", src);				
	}

	@Test
	public void testAggregateExactly5() {
		String src = 
			"BEGIN;" +
			"var a private relation {x char, y boolean} key {x};" +
			"a := relation {" + 
				"tuple {y true, x 'blah'}," +
				"tuple {x 'blat', y false}," +
				"tuple {y true, x 'zot'}" +
			"};" +
			"END;" +
			"EXACTLY(2, a, y)";
		testEquals("true", src);				
	}

	@Test
	public void testRelationNadicUnion1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"UNION {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 2.8, z \"glob\"},\n\tTUPLE {x 3, y 4.5, z \"test\"}\n}", src);
	}

	@Test
	public void testRelationNadicUnion2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"UNION {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"},\n\tTUPLE {x 6, y 2.8, z \"glob\"},\n\tTUPLE {x 4, y 2.8, z \"glob\"},\n\tTUPLE {x 5, y 4.5, z \"test\"}\n}", src);
	}


	@Test
	public void testRelationNadicXunion1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"XUNION {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n}", src);
	}

	@Test
	public void testRelationNadicXunion2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"XUNION {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 6, y 2.8, z \"glob\"},\n\tTUPLE {x 4, y 2.8, z \"glob\"}\n}", src);
	}
	
	@Test
	public void testRelationNadicDUnion1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"D_UNION {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"},\n\tTUPLE {x 6, y 2.8, z \"glob\"},\n\tTUPLE {x 4, y 2.8, z \"glob\"},\n\tTUPLE {x 5, y 4.5, z \"test\"}\n}", src);
	}
	
	@Test
	public void testRelationNadicIntersect1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"INTERSECT {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n}", src);
	}
	
	@Test
	public void testRelationNadicIntersect2() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 3, y 4.5, z \"test\"},\n" +
		        "       tuple {z \"glob\", x 6, y 2.8}\n" +
		        "}) KEY {ALL BUT};\n" +
		        "VAR b PRIVATE INIT(relation {\n" +
		        "       tuple {x 5, y 4.5, z \"test\"},\n" +
		        "       tuple {z \"test\", x 3, y 4.5}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"INTERSECT {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"}\n}", src);
	}

	@Test
	public void testRelationNadicJoin1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5}," +
		        "       tuple {x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}," +
		        "       tuple {a 4.6, b 9}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"JOIN {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, a RATIONAL, b INTEGER} {\n\tTUPLE {x 3, y 4.5, a 4.5, b 7},\n\tTUPLE {x 3, y 4.5, a 4.6, b 9},\n\tTUPLE {x 3, y 4.5, a 4.8, b 8},\n\tTUPLE {x 6, y 2.8, a 4.5, b 7},\n\tTUPLE {x 6, y 2.8, a 4.6, b 9},\n\tTUPLE {x 6, y 2.8, a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationNadicJoin2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY{ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"JOIN {a, b}";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationNadicJoin3() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY{ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"JOIN {a, b}";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationNadicJoin4() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY{ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"JOIN {a, b}";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationNadicJoin5() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY{ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"JOIN {a, b}";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationNadicJoin6() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, a 4.5, b \"test\"}," +
		        "       tuple {b \"zot\", x 3, a 4.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"JOIN {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER, a RATIONAL, b CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\", a 4.8, b \"zot\"}\n}", src);
	}

	@Test
	public void testRelationNadicCompose1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5}," +
		        "       tuple {x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}," +
		        "       tuple {a 4.6, b 9}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"COMPOSE {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, a RATIONAL, b INTEGER} {\n\tTUPLE {x 3, y 4.5, a 4.5, b 7},\n\tTUPLE {x 3, y 4.5, a 4.6, b 9},\n\tTUPLE {x 3, y 4.5, a 4.8, b 8},\n\tTUPLE {x 6, y 2.8, a 4.5, b 7},\n\tTUPLE {x 6, y 2.8, a 4.6, b 9},\n\tTUPLE {x 6, y 2.8, a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationNadicCompose2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY{ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"COMPOSE {a, b}";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationNadicCompose3() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DUM) KEY{ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"COMPOSE {a, b}";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n}", src);
	}

	@Test
	public void testRelationNadicCompose4() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY{ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"COMPOSE {a, b}";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationNadicCompose5() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(TABLE_DEE) KEY{ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {a 4.5, b 7}," +
		        "       tuple {a 4.8, b 8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"COMPOSE {a, b}";
		testEquals("RELATION {a RATIONAL, b INTEGER} {\n\tTUPLE {a 4.5, b 7},\n\tTUPLE {a 4.8, b 8}\n}", src);
	}

	@Test
	public void testRelationNadicCompose6() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, a 4.5, b \"test\"}," +
		        "       tuple {b \"zot\", x 3, a 4.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"COMPOSE {a, b}";
		testEquals("RELATION {y RATIONAL, z CHARACTER, a RATIONAL, b CHARACTER} {\n\tTUPLE {y 4.5, z \"test\", a 4.8, b \"zot\"}\n}", src);
	}
	
	@Test
	public void testRelationNadicHeadingUnion1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"UNION {x integer, y rational, z char} {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 2.8, z \"glob\"},\n\tTUPLE {x 3, y 4.5, z \"test\"}\n}", src);
	}

	@Test
	public void testRelationNadicHeadingUnion2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"UNION {x integer, y rational, z char} {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"},\n\tTUPLE {x 6, y 2.8, z \"glob\"},\n\tTUPLE {x 4, y 2.8, z \"glob\"},\n\tTUPLE {x 5, y 4.5, z \"test\"}\n}", src);
	}

	@Test
	public void testRelationNadicHeadingXunion1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 3, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"XUNION {x integer, y rational, z char} {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n}", src);
	}

	@Test
	public void testRelationNadicHeadingXunion2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"XUNION {x integer, y rational, z char} {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"},\n\tTUPLE {x 6, y 2.8, z \"glob\"},\n\tTUPLE {x 4, y 2.8, z \"glob\"},\n\tTUPLE {x 5, y 4.5, z \"test\"}\n}", src);
	}
	
	@Test
	public void testRelationNadicHeadingDUnion1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"D_UNION {x integer, y rational, z char} {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"},\n\tTUPLE {x 6, y 2.8, z \"glob\"},\n\tTUPLE {x 4, y 2.8, z \"glob\"},\n\tTUPLE {x 5, y 4.5, z \"test\"}\n}", src);
	}
	
	@Test
	public void testRelationNadicHeadingIntersect1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {" +
		        "       tuple {x 3, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 6, y 2.8}" +
		        "}) KEY {ALL BUT};" +
		        "VAR b PRIVATE INIT(relation {" +
		        "       tuple {x 5, y 4.5, z \"test\"}," +
		        "       tuple {z \"glob\", x 4, y 2.8}" +
		        "}) KEY {ALL BUT};" +
			"END;" +
			"INTERSECT {x integer, y rational, z char} {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n}", src);
	}
	
	@Test
	public void testRelationNadicHeadingIntersect2() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 3, y 4.5, z \"test\"},\n" +
		        "       tuple {z \"glob\", x 6, y 2.8}\n" +
		        "}) KEY {ALL BUT};\n" +
		        "VAR b PRIVATE INIT(relation {\n" +
		        "       tuple {x 5, y 4.5, z \"test\"},\n" +
		        "       tuple {z \"test\", x 3, y 4.5}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"INTERSECT {x integer, y rational, z char} {a, b}";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 3, y 4.5, z \"test\"}\n}", src);
	}
	
	@Test public void testCast01() {assertValueEquals(ValueBoolean.select(generator, false), testEvaluate("CAST_AS_BOOLEAN(0)").getValue());}		
	@Test public void testCast02() {assertValueEquals(ValueBoolean.select(generator, true), testEvaluate("CAST_AS_BOOLEAN(1)").getValue());}
	@Test public void testCast03() {assertValueEquals(ValueBoolean.select(generator, false), testEvaluate("CAST_AS_BOOLEAN(0.0)").getValue());}		
	@Test public void testCast04() {assertValueEquals(ValueBoolean.select(generator, true), testEvaluate("CAST_AS_BOOLEAN(1.2)").getValue());}		
	@Test public void testCast05() {assertValueEquals(ValueBoolean.select(generator, true), testEvaluate("CAST_AS_BOOLEAN(true)").getValue());}		
	@Test public void testCast06() {assertValueEquals(ValueBoolean.select(generator, false), testEvaluate("CAST_AS_BOOLEAN(false)").getValue());}		
	@Test public void testCast07() {assertValueEquals(ValueBoolean.select(generator, true), testEvaluate("CAST_AS_BOOLEAN('true')").getValue());}		
	@Test public void testCast08() {assertValueEquals(ValueBoolean.select(generator, false), testEvaluate("CAST_AS_BOOLEAN('false')").getValue());}		
	
	@Test public void testCast09() {assertValueEquals(ValueInteger.select(generator, 0), testEvaluate("CAST_AS_INTEGER(0)").getValue());}		
	@Test public void testCast10() {assertValueEquals(ValueInteger.select(generator, 1), testEvaluate("CAST_AS_INTEGER(1)").getValue());}		
	@Test public void testCast11() {assertValueEquals(ValueInteger.select(generator, 0), testEvaluate("CAST_AS_INTEGER(0.0)").getValue());}		
	@Test public void testCast12() {assertValueEquals(ValueInteger.select(generator, 1), testEvaluate("CAST_AS_INTEGER(1.2)").getValue());}		
	@Test public void testCast13() {assertValueEquals(ValueInteger.select(generator, 1), testEvaluate("CAST_AS_INTEGER(true)").getValue());}		
	@Test public void testCast14() {assertValueEquals(ValueInteger.select(generator, 0), testEvaluate("CAST_AS_INTEGER(false)").getValue());}		
	@Test public void testCast15() {assertValueEquals(ValueInteger.select(generator, 0), testEvaluate("CAST_AS_INTEGER('0')").getValue());}		
	@Test public void testCast16() {assertValueEquals(ValueInteger.select(generator, 1), testEvaluate("CAST_AS_INTEGER('1')").getValue());}	
	@Test public void testCast17() {assertValueEquals(ValueInteger.select(generator, 0), testEvaluate("CAST_AS_INTEGER('0.0')").getValue());}		
	@Test public void testCast18() {assertValueEquals(ValueInteger.select(generator, 1), testEvaluate("CAST_AS_INTEGER('1.2')").getValue());}

	@Test public void testCast19() {assertValueEquals(ValueRational.select(generator, 0), testEvaluate("CAST_AS_RATIONAL(0)").getValue());}		
	@Test public void testCast20() {assertValueEquals(ValueRational.select(generator, 1), testEvaluate("CAST_AS_RATIONAL(1)").getValue());}	
	@Test public void testCast21() {assertValueEquals(ValueRational.select(generator, 1.2), testEvaluate("CAST_AS_RATIONAL(1.2)").getValue());}		
	@Test public void testCast22() {assertValueEquals(ValueRational.select(generator, 1), testEvaluate("CAST_AS_RATIONAL(true)").getValue());}		
	@Test public void testCast23() {assertValueEquals(ValueRational.select(generator, 0), testEvaluate("CAST_AS_RATIONAL(false)").getValue());}				
	@Test public void testCast24() {assertValueEquals(ValueRational.select(generator, 0), testEvaluate("CAST_AS_RATIONAL('0')").getValue());}		
	@Test public void testCast25() {assertValueEquals(ValueRational.select(generator, 1), testEvaluate("CAST_AS_RATIONAL('1')").getValue());}		
	@Test public void testCast26() {assertValueEquals(ValueRational.select(generator, 0.0), testEvaluate("CAST_AS_RATIONAL('0.0')").getValue());}		
	@Test public void testCast27() {assertValueEquals(ValueRational.select(generator, 1.2), testEvaluate("CAST_AS_RATIONAL('1.2')").getValue());}		
	
	@Test public void testCast28() {assertValueEquals(ValueCharacter.select(generator, "0"), testEvaluate("CAST_AS_CHAR(0)").getValue());}		
	@Test public void testCast29() {assertValueEquals(ValueCharacter.select(generator, "1"), testEvaluate("CAST_AS_CHAR(1)").getValue());}		
	@Test public void testCast30() {assertValueEquals(ValueCharacter.select(generator, "0.0"), testEvaluate("CAST_AS_CHAR(0.0)").getValue());}		
	@Test public void testCast31() {assertValueEquals(ValueCharacter.select(generator, "1.2"), testEvaluate("CAST_AS_CHAR(1.2)").getValue());}		
	@Test public void testCast32() {assertValueEquals(ValueCharacter.select(generator, "true"), testEvaluate("CAST_AS_CHAR(true)").getValue());}	
	@Test public void testCast33() {assertValueEquals(ValueCharacter.select(generator, "false"), testEvaluate("CAST_AS_CHAR(false)").getValue());}	

	@Test
	public void testSummarizeSimulatedCount() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"WITH (r1 := a,\n" +
			"      r2 := a {z}) :\n" +
			"(EXTEND r2 : {Y := r1 JOIN RELATION {TUPLE {z z}},\n" +
			"      CountOfx := COUNT(Y)})\n" +
			"  {ALL BUT Y}" +
			"";
		testEquals("RELATION {z CHARACTER, CountOfx INTEGER} {\n\tTUPLE {z \"test\", CountOfx 4},\n\tTUPLE {z \"glub\", CountOfx 2},\n\tTUPLE {z \"zot\", CountOfx 1}\n}", src);		
	}
	
	@Test
	public void testSummarizeSimulatedCountDistinct() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"WITH (r1 := a,\n" +
			"      r2 := a {z}) :\n" +
			"(EXTEND r2 : {Y := r1 JOIN RELATION {TUPLE {z z}},\n" +
			"       CountOfx := COUNT((EXTEND Y : {X := x}) {X})})\n" +
			"  {ALL BUT Y}" +
			"";
		testEquals("RELATION {z CHARACTER, CountOfx INTEGER} {\n\tTUPLE {z \"test\", CountOfx 4},\n\tTUPLE {z \"glub\", CountOfx 2},\n\tTUPLE {z \"zot\", CountOfx 1}\n}", src);		
	}
	
	@Test
	public void testSummarizeSimulatedSum() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"WITH (r1 := a,\n" +
			"      r2 := a {z}) :\n" +
			"(EXTEND r2 : {Y := r1 JOIN RELATION {TUPLE {z z}},\n" +
			"      totalOfx := SUM((EXTEND Y : {X := x}), X)})\n" +
			"  {ALL BUT Y}" +
			"";
		testEquals("RELATION {z CHARACTER, totalOfx INTEGER} {\n\tTUPLE {z \"test\", totalOfx 15},\n\tTUPLE {z \"glub\", totalOfx 7},\n\tTUPLE {z \"zot\", totalOfx 6}\n}", src);		
	}
	
	@Test
	public void testSummarizeSimulatedSumDistinct() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"WITH (r1 := a,\n" +
			"      r2 := a {z}) :\n" +
			"(EXTEND r2 : {Y := r1 JOIN RELATION {TUPLE {z z}},\n" +
			"       totalOfx := SUM((EXTEND Y : {X := x}) {X}, X)})\n" +
			"  {ALL BUT Y}" +
			"";
		testEquals("RELATION {z CHARACTER, totalOfx INTEGER} {\n\tTUPLE {z \"test\", totalOfx 15},\n\tTUPLE {z \"glub\", totalOfx 7},\n\tTUPLE {z \"zot\", totalOfx 6}\n}", src);		
	}
	
	@Test
	public void testSummarizeSimulatedAvg() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"WITH (r1 := a,\n" +
			"      r2 := a {z}) :\n" +
			"(EXTEND r2 : {Y := r1 JOIN RELATION {TUPLE {z z}},\n" +
			"      averageOfx := AVG((EXTEND Y : {X := x}), X)})\n" +
			"  {ALL BUT Y}" +
			"";
		testEquals("RELATION {z CHARACTER, averageOfx RATIONAL} {\n\tTUPLE {z \"test\", averageOfx 3.75},\n\tTUPLE {z \"glub\", averageOfx 3.5},\n\tTUPLE {z \"zot\", averageOfx 6.0}\n}", src);		
	}
	
	@Test
	public void testSummarizeSimulatedAvgDistinct() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"WITH (r1 := a,\n" +
			"      r2 := a {z}) :\n" +
			"(EXTEND r2 : {Y := r1 JOIN RELATION {TUPLE {z z}},\n" +
			"      averageOfx := AVG((EXTEND Y : {X := x}) {X}, X)})\n" +
			"  {ALL BUT Y}" +
			"";
		testEquals("RELATION {z CHARACTER, averageOfx RATIONAL} {\n\tTUPLE {z \"test\", averageOfx 3.75},\n\tTUPLE {z \"glub\", averageOfx 3.5},\n\tTUPLE {z \"zot\", averageOfx 6.0}\n}", src);		
	}
	
	@Test
	public void testSummarizeSimulatedMin() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"WITH (r1 := a,\n" +
			"      r2 := a {z}) :\n" +
			"(EXTEND r2 : {Y := r1 JOIN RELATION {TUPLE {z z}},\n" +
			"      minOfx := MIN((EXTEND Y : {X := x}), X)})\n" +
			"  {ALL BUT Y}" +
			"";
		testEquals("RELATION {z CHARACTER, minOfx INTEGER} {\n\tTUPLE {z \"test\", minOfx 1},\n\tTUPLE {z \"glub\", minOfx 3},\n\tTUPLE {z \"zot\", minOfx 6}\n}", src);		
	}
	
	@Test
	public void testSummarizeSimulatedMax() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"WITH (r1 := a,\n" +
			"      r2 := a {z}) :\n" +
			"(EXTEND r2 : {Y := r1 JOIN RELATION {TUPLE {z z}},\n" +
			"      maxOfx := MAX((EXTEND Y : {X := x}), X)})\n" +
			"  {ALL BUT Y}" +
			"";
		testEquals("RELATION {z CHARACTER, maxOfx INTEGER} {\n\tTUPLE {z \"test\", maxOfx 7},\n\tTUPLE {z \"glub\", maxOfx 4},\n\tTUPLE {z \"zot\", maxOfx 6}\n}", src);		
	}

	@Test
	public void testSummarizeCount1() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a PER (a {z}) : {CountOfx := COUNT()}";
		testEquals("RELATION {z CHARACTER, CountOfx INTEGER} {\n\tTUPLE {z \"test\", CountOfx 4},\n\tTUPLE {z \"glub\", CountOfx 2},\n\tTUPLE {z \"zot\", CountOfx 1}\n}", src);		
	}
	
	@Test
	public void testSummarizeCount2() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a BY {z} : {CountOfx := COUNT()}";
		testEquals("RELATION {z CHARACTER, CountOfx INTEGER} {\n\tTUPLE {z \"test\", CountOfx 4},\n\tTUPLE {z \"glub\", CountOfx 2},\n\tTUPLE {z \"zot\", CountOfx 1}\n}", src);		
	}
	
	@Test
	public void testSummarizeCountDistinct() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a PER (a {z}) : {CountOfx := COUNTD(x)}";
		testEquals("RELATION {z CHARACTER, CountOfx INTEGER} {\n\tTUPLE {z \"test\", CountOfx 4},\n\tTUPLE {z \"glub\", CountOfx 2},\n\tTUPLE {z \"zot\", CountOfx 1}\n}", src);		
	}
	
	@Test
	public void testSummarizeSum1() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a PER (a {z}) : {totalOfx := SUM(x)}";
		testEquals("RELATION {z CHARACTER, totalOfx INTEGER} {\n\tTUPLE {z \"test\", totalOfx 15},\n\tTUPLE {z \"glub\", totalOfx 7},\n\tTUPLE {z \"zot\", totalOfx 6}\n}", src);		
	}
	
	@Test
	public void testSummarizeSum2() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a BY {z} : {totalOfx := SUM(x + 1)}";
		testEquals("RELATION {z CHARACTER, totalOfx INTEGER} {\n\tTUPLE {z \"test\", totalOfx 19},\n\tTUPLE {z \"glub\", totalOfx 9},\n\tTUPLE {z \"zot\", totalOfx 7}\n}", src);		
	}
	
	@Test
	public void testSummarizeSum3() {
		String src =
			"BEGIN;\n" +
	        "VAR a PRIVATE INIT(relation {\n" +
	        "       tuple {x 1, y 4.5, z \"test\"},\n" +
	        "       tuple {x 2, y 2.5, z \"test\"},\n" +
	        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
	        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
	        "       tuple {x 5, y 5.2, z \"test\"},\n" +
	        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
	        "       tuple {x 7, y 4.5, z \"test\"}\n" +
	        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a : {totalOfx := SUM(x)}";
		testEquals("RELATION {totalOfx INTEGER} {\n\tTUPLE {totalOfx 28}\n}", src);		
	}
	
	@Test
	public void testSummarizeSumDistinct() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a PER (a {z}) : {totalOfx := SUMD(x)}";
		testEquals("RELATION {z CHARACTER, totalOfx INTEGER} {\n\tTUPLE {z \"test\", totalOfx 15},\n\tTUPLE {z \"glub\", totalOfx 7},\n\tTUPLE {z \"zot\", totalOfx 6}\n}", src);		
	}
	
	@Test
	public void testSummarizeAvg() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a PER (a {z}) : {averageOfx := AVG(x)}";
		testEquals("RELATION {z CHARACTER, averageOfx RATIONAL} {\n\tTUPLE {z \"test\", averageOfx 3.75},\n\tTUPLE {z \"glub\", averageOfx 3.5},\n\tTUPLE {z \"zot\", averageOfx 6.0}\n}", src);		
	}
	
	@Test
	public void testSummarizeAvgDistinct() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a PER (a {z}) : {averageOfx := AVGD(x)}";
		testEquals("RELATION {z CHARACTER, averageOfx RATIONAL} {\n\tTUPLE {z \"test\", averageOfx 3.75},\n\tTUPLE {z \"glub\", averageOfx 3.5},\n\tTUPLE {z \"zot\", averageOfx 6.0}\n}", src);		
	}
	
	@Test
	public void testSummarizeMin() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a PER (a {z}) : {minOfx := MIN(x)}";
		testEquals("RELATION {z CHARACTER, minOfx INTEGER} {\n\tTUPLE {z \"test\", minOfx 1},\n\tTUPLE {z \"glub\", minOfx 3},\n\tTUPLE {z \"zot\", minOfx 6}\n}", src);		
	}
	
	@Test
	public void testSummarizeMax() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a PER (a {z}) : {maxOfx := MAX(x)}";
		testEquals("RELATION {z CHARACTER, maxOfx INTEGER} {\n\tTUPLE {z \"test\", maxOfx 7},\n\tTUPLE {z \"glub\", maxOfx 4},\n\tTUPLE {z \"zot\", maxOfx 6}\n}", src);		
	}
	
	@Test
	public void testSummarizeExactly() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a : {isThereThreeLessThanFour := EXACTLY(3, x < 4)}";
		testEquals("RELATION {isThereThreeLessThanFour BOOLEAN} {\n\tTUPLE {isThereThreeLessThanFour true}\n}", src);		
	}

	@Test
	public void testSummarizeAnd() {
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
			"SUMMARIZE a BY {z} : {andOfx := AND(x)}";
		testEquals("RELATION {z CHARACTER, andOfx BOOLEAN} {\n\tTUPLE {z \"falsefalse\", andOfx false},\n\tTUPLE {z \"truefalse\", andOfx false},\n\tTUPLE {z \"truetrue\", andOfx true}\n}", src);		
	}

	@Test
	public void testSummarizeOr() {
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
			"SUMMARIZE a BY {z} : {orOfx := OR(x)}";
		testEquals("RELATION {z CHARACTER, orOfx BOOLEAN} {\n\tTUPLE {z \"falsefalse\", orOfx false},\n\tTUPLE {z \"truefalse\", orOfx true},\n\tTUPLE {z \"truetrue\", orOfx true}\n}", src);		
	}

	@Test
	public void testSummarizeXor() {
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
			"SUMMARIZE a BY {z} : {xorOfx := XOR(x)}";
		testEquals("RELATION {z CHARACTER, xorOfx BOOLEAN} {\n\tTUPLE {z \"falsefalse\", xorOfx false},\n\tTUPLE {z \"truefalse\", xorOfx true},\n\tTUPLE {z \"truetrue\", xorOfx false}\n}", src);		
	}

	@Test
	public void testSummarizeUnion() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x relation{tuple{x 1, y 1.2}}, z \"falsefalse\"},\n" +
		        "       tuple {x relation{tuple{x 2, y 1.2}}, z \"falsefalse\"},\n" +
		        "       tuple {x relation{tuple{x 3, y 1.3}}, z \"truefalse\"},\n" +
		        "       tuple {x relation{tuple{x 2, y 1.4}}, z \"truefalse\"},\n" +
		        "       tuple {x relation{tuple{x 1, y 1.5}}, z \"truetrue\"},\n" +
		        "       tuple {x relation{tuple{x 3, y 1.1}}, z \"truetrue\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a BY {z} : {unionOfx := UNION(x)}";
		String result = "RELATION {z CHARACTER, unionOfx RELATION {x INTEGER, y RATIONAL}} {" +
			"\n\tTUPLE {z \"falsefalse\", unionOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 1, y 1.2}," +
			"\n\tTUPLE {x 2, y 1.2}" +
			"\n}}," +
			"\n\tTUPLE {z \"truefalse\", unionOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 3, y 1.3}," +
			"\n\tTUPLE {x 2, y 1.4}" +
			"\n}}," +
			"\n\tTUPLE {z \"truetrue\", unionOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 1, y 1.5}," +
			"\n\tTUPLE {x 3, y 1.1}" +
			"\n}}" +
			"\n}";
		testEquals(result, src);		
	}

	@Test
	public void testSummarizeXunion() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x relation{tuple{x 1, y 1.2}}, z \"falsefalse\"},\n" +
		        "       tuple {x relation{tuple{x 2, y 1.2}}, z \"falsefalse\"},\n" +
		        "       tuple {x relation{tuple{x 3, y 1.3}}, z \"truefalse\"},\n" +
		        "       tuple {x relation{tuple{x 2, y 1.4}}, z \"truefalse\"},\n" +
		        "       tuple {x relation{tuple{x 1, y 1.5}}, z \"truetrue\"},\n" +
		        "       tuple {x relation{tuple{x 3, y 1.1}}, z \"truetrue\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a BY {z} : {unionOfx := XUNION(x)}";
		String result = "RELATION {z CHARACTER, unionOfx RELATION {x INTEGER, y RATIONAL}} {" +
			"\n\tTUPLE {z \"falsefalse\", unionOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 1, y 1.2}," +
			"\n\tTUPLE {x 2, y 1.2}" +
			"\n}}," +
			"\n\tTUPLE {z \"truefalse\", unionOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 3, y 1.3}," +
			"\n\tTUPLE {x 2, y 1.4}" +
			"\n}}," +
			"\n\tTUPLE {z \"truetrue\", unionOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 1, y 1.5}," +
			"\n\tTUPLE {x 3, y 1.1}" +
			"\n}}" +
			"\n}";
		testEquals(result, src);		
	}
	
	@Test
	public void testSummarizeIntersect() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x relation{tuple{x 1, y 1.2}}, z \"falsefalse\"},\n" +
		        "       tuple {x relation{tuple{x 1, y 1.2}}, z \"falsefalse\"},\n" +
		        "       tuple {x relation{tuple{x 3, y 1.3}}, z \"truefalse\"},\n" +
		        "       tuple {x relation{tuple{x 2, y 1.4}}, z \"truefalse\"},\n" +
		        "       tuple {x relation{tuple{x 1, y 1.5}}, z \"truetrue\"},\n" +
		        "       tuple {x relation{tuple{x 3, y 1.1}}, z \"truetrue\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a BY {z} : {intersectOfx := INTERSECT(x)}";
		String result = "RELATION {z CHARACTER, intersectOfx RELATION {x INTEGER, y RATIONAL}} {" +
			"\n\tTUPLE {z \"falsefalse\", intersectOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 1, y 1.2}" +
			"\n}}," +
			"\n\tTUPLE {z \"truefalse\", intersectOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n}}," +
			"\n\tTUPLE {z \"truetrue\", intersectOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n}}" +
			"\n}";
		testEquals(result, src);		
	}
	
	// TODO - check this -- should it throw an exception for tuples with z = 'falsefalse'?
	@Test
	public void testSummarizeDUnion() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x relation{tuple{x 1, y 1.2}}, z \"falsefalse\"},\n" +
		        "       tuple {x relation{tuple{x 1, y 1.2}}, z \"falsefalse\"},\n" +
		        "       tuple {x relation{tuple{x 3, y 1.3}}, z \"truefalse\"},\n" +
		        "       tuple {x relation{tuple{x 2, y 1.4}}, z \"truefalse\"},\n" +
		        "       tuple {x relation{tuple{x 1, y 1.5}}, z \"truetrue\"},\n" +
		        "       tuple {x relation{tuple{x 3, y 1.1}}, z \"truetrue\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a BY {z} : {unionOfx := D_UNION(x)}";
		String result = "RELATION {z CHARACTER, unionOfx RELATION {x INTEGER, y RATIONAL}} {" +
			"\n\tTUPLE {z \"falsefalse\", unionOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 1, y 1.2}" +
			"\n}}," +
			"\n\tTUPLE {z \"truefalse\", unionOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 3, y 1.3}," +
			"\n\tTUPLE {x 2, y 1.4}" +
			"\n}}," +
			"\n\tTUPLE {z \"truetrue\", unionOfx RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 1, y 1.5}," +
			"\n\tTUPLE {x 3, y 1.1}" +
			"\n}}" +
			"\n}";
		testEquals(result, src);		
	}

	@Test
	public void testSmallDivideSimulated() {
		String src =
			"BEGIN;" +
			  "VAR r1 PRIVATE INIT(relation {" +
			     "tuple {A1 1}," +
			     "tuple {A1 2}," +
			     "tuple {A1 3}," +
			     "tuple {A1 4}," +
			     "tuple {A1 5}," +
			     "tuple {A1 6}," +
			     "tuple {A1 7}" +
			  "}) KEY {ALL BUT};" +
			  "VAR r2 PRIVATE INIT(relation {" +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"glub\"}," +
			     "tuple {A2 \"glub\"}," +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"zot\"}," +
			     "tuple {A2 \"test\"}" +
			  "}) KEY {ALL BUT};" +
			  "VAR r3 PRIVATE INIT(relation {" +
			     "tuple {A1 1, A2 \"test\"}," +
			     "tuple {A1 2, A2 \"test\"}," +
			     "tuple {A1 3, A2 \"glub\"}," +
			     "tuple {A1 4, A2 \"glub\"}," +
			     "tuple {A1 5, A2 \"test\"}," +
			     "tuple {A1 6, A2 \"zot\"}," +
			     "tuple {A1 7, A2 \"test\"}" +
			  "}) KEY {ALL BUT};" +
			"END;" +
			"r1 {A1}   MINUS   ((r1 {A1} JOIN r2 {A2}) MINUS r3 {A1, A2}) {A1}";	
		testEquals("RELATION {A1 INTEGER} {\n}", src);
	}

	@Test
	public void testGreatDivideSimulated() {
		String src =
			"BEGIN;" +
			  "VAR r1 PRIVATE INIT(relation {" +
			     "tuple {A1 1}," +
			     "tuple {A1 2}," +
			     "tuple {A1 3}," +
			     "tuple {A1 4}," +
			     "tuple {A1 5}," +
			     "tuple {A1 6}," +
			     "tuple {A1 7}" +
			  "}) KEY {ALL BUT};" +
			  "VAR r2 PRIVATE INIT(relation {" +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"glub\"}," +
			     "tuple {A2 \"glub\"}," +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"zot\"}," +
			     "tuple {A2 \"test\"}" +
			  "}) KEY {ALL BUT};" +
			  "VAR r3 PRIVATE INIT(relation {" +
			     "tuple {A1 1, A3 4.5}," +
			     "tuple {A1 2, A3 2.5}," +
			     "tuple {A1 3, A3 3.2}," +
			     "tuple {A1 4, A3 4.5}," +
			     "tuple {A1 5, A3 5.2}," +
			     "tuple {A1 6, A3 3.1}," +
			     "tuple {A1 7, A3 4.5}" +
			  "}) KEY {ALL BUT};" +
			  "VAR r4 PRIVATE INIT(relation {" +
			     "tuple {A3 4.5, A2 \"test\"}," +
			     "tuple {A3 2.5, A2 \"test\"}," +
			     "tuple {A3 3.2, A2 \"glub\"}," +
			     "tuple {A3 4.5, A2 \"glub\"}," +
			     "tuple {A3 5.2, A2 \"test\"}," +
			     "tuple {A3 3.1, A2 \"zot\"}," +
			     "tuple {A3 4.5, A2 \"test\"}" +
			  "}) KEY {ALL BUT};" +
			"END;" +
			"WITH (r1p := r1 {A1}, r4p := r4 {A2, A3}) :" +
			"(r1p JOIN r2 {A2})  MINUS  ((r1p JOIN r4p) MINUS (r3 {A1, A3} JOIN r4p)) {A1, A2}";			
		testEquals("RELATION {A1 INTEGER, A2 CHARACTER} {\n\tTUPLE {A1 6, A2 \"zot\"}\n}", src);
	}
	
	@Test
	public void testSmallDivide() {
		String src =
			"BEGIN;" +
			  "VAR r1 PRIVATE INIT(relation {" +
			     "tuple {A1 1}," +
			     "tuple {A1 2}," +
			     "tuple {A1 3}," +
			     "tuple {A1 4}," +
			     "tuple {A1 5}," +
			     "tuple {A1 6}," +
			     "tuple {A1 7}" +
			  "}) KEY {ALL BUT};" +
			  "VAR r2 PRIVATE INIT(relation {" +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"glub\"}," +
			     "tuple {A2 \"glub\"}," +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"zot\"}," +
			     "tuple {A2 \"test\"}" +
			  "}) KEY {ALL BUT};" +
			  "VAR r3 PRIVATE INIT(relation {" +
			     "tuple {A1 1, A2 \"test\"}," +
			     "tuple {A1 2, A2 \"test\"}," +
			     "tuple {A1 3, A2 \"glub\"}," +
			     "tuple {A1 4, A2 \"glub\"}," +
			     "tuple {A1 5, A2 \"test\"}," +
			     "tuple {A1 6, A2 \"zot\"}," +
			     "tuple {A1 7, A2 \"test\"}" +
			  "}) KEY {ALL BUT};" +
			"END;" +
			"r1 DIVIDEBY r2 PER (r3)";	
		testEquals("RELATION {A1 INTEGER} {\n}", src);
	}

	@Test
	public void testGreatDivide() {
		String src =
			"BEGIN;" +
			  "VAR r1 PRIVATE INIT(relation {" +
			     "tuple {A1 1}," +
			     "tuple {A1 2}," +
			     "tuple {A1 3}," +
			     "tuple {A1 4}," +
			     "tuple {A1 5}," +
			     "tuple {A1 6}," +
			     "tuple {A1 7}" +
			  "}) KEY {ALL BUT};" +
			  "VAR r2 PRIVATE INIT(relation {" +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"glub\"}," +
			     "tuple {A2 \"glub\"}," +
			     "tuple {A2 \"test\"}," +
			     "tuple {A2 \"zot\"}," +
			     "tuple {A2 \"test\"}" +
			  "}) KEY {ALL BUT};" +
			  "VAR r3 PRIVATE INIT(relation {" +
			     "tuple {A1 1, A3 4.5}," +
			     "tuple {A1 2, A3 2.5}," +
			     "tuple {A1 3, A3 3.2}," +
			     "tuple {A1 4, A3 4.5}," +
			     "tuple {A1 5, A3 5.2}," +
			     "tuple {A1 6, A3 3.1}," +
			     "tuple {A1 7, A3 4.5}" +
			  "}) KEY {ALL BUT};" +
			  "VAR r4 PRIVATE INIT(relation {" +
			     "tuple {A3 4.5, A2 \"test\"}," +
			     "tuple {A3 2.5, A2 \"test\"}," +
			     "tuple {A3 3.2, A2 \"glub\"}," +
			     "tuple {A3 4.5, A2 \"glub\"}," +
			     "tuple {A3 5.2, A2 \"test\"}," +
			     "tuple {A3 3.1, A2 \"zot\"}," +
			     "tuple {A3 4.5, A2 \"test\"}" +
			  "}) KEY {ALL BUT};" +
			"END;" +
			"r1 DIVIDEBY r2 PER (r3, r4)";			
		testEquals("RELATION {A1 INTEGER, A2 CHARACTER} {\n\tTUPLE {A1 6, A2 \"zot\"}\n}", src);
	}
	
	@Test
	public void testArray0() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
		        "VAR ar ARRAY TUPLE {y RATIONAL, z CHARACTER, x INTEGER};\n" +
		        "LOAD ar FROM a ORDER();" +
			"END;" +
			"ar";
		String expected = "ARRAY {y RATIONAL, z CHARACTER, x INTEGER} {" +
	        "\n\tTUPLE {y 4.5, z \"test\", x 1}," +
	        "\n\tTUPLE {y 2.5, z \"test\", x 2}," +
	        "\n\tTUPLE {y 3.2, z \"glub\", x 3}," +
	        "\n\tTUPLE {y 4.5, z \"glub\", x 4}," +
	        "\n\tTUPLE {y 5.2, z \"test\", x 5}," +
	        "\n\tTUPLE {y 3.1, z \"zot\", x 6}," +
	        "\n\tTUPLE {y 4.5, z \"test\", x 7}" +
	        "\n}";
		testEquals(expected, src);
	}
	
	@Test
	public void testArray1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
		        "VAR ar ARRAY TUPLE SAME_HEADING_AS (a);\n" +
		        "LOAD ar FROM a ORDER();" +
			"END;" +
			"ar";
		testEquals("ARRAY {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 1, y 4.5, z \"test\"},\n\tTUPLE {x 2, y 2.5, z \"test\"},\n\tTUPLE {x 3, y 3.2, z \"glub\"},\n\tTUPLE {x 4, y 4.5, z \"glub\"},\n\tTUPLE {x 5, y 5.2, z \"test\"},\n\tTUPLE {x 6, y 3.1, z \"zot\"},\n\tTUPLE {x 7, y 4.5, z \"test\"}\n}", src);
	}
	
	@Test
	public void testArray2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
		        "VAR ar ARRAY TUPLE SAME_HEADING_AS (a);\n" +
		        "LOAD ar FROM a ORDER();" +
			"END;" +
			"COUNT(ar)";
		testEquals("7", src);
	}

	@Test
	public void testArray3() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
		        "VAR ar ARRAY TUPLE SAME_HEADING_AS (a);\n" +
		        "LOAD ar FROM a ORDER();" +
			"END;" +
			"ar[3]";
		testEquals("TUPLE {x 4, y 4.5, z \"glub\"}", src);
	}

	@Test
	public void testArray4() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
		        "VAR ar ARRAY TUPLE SAME_HEADING_AS (a);\n" +
		        "LOAD ar FROM a ORDER();\n" +
		        "VAR b PRIVATE SAME_TYPE_AS (a) KEY {ALL BUT};\n" +
		        "LOAD b FROM ar;" +
			"END;" +
			"b";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 1, y 4.5, z \"test\"},\n\tTUPLE {x 2, y 2.5, z \"test\"},\n\tTUPLE {x 3, y 3.2, z \"glub\"},\n\tTUPLE {x 4, y 4.5, z \"glub\"},\n\tTUPLE {x 5, y 5.2, z \"test\"},\n\tTUPLE {x 6, y 3.1, z \"zot\"},\n\tTUPLE {x 7, y 4.5, z \"test\"}\n}", src);
	}

	@Test
	public void testArray5() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"}\n" +
		        "}) KEY {ALL BUT};\n" +
		        "VAR ar ARRAY TUPLE SAME_HEADING_AS (a);\n" +
		        "LOAD ar FROM a ORDER (ASC y, DESC z);" +
			"END;" +
			"ar";
		testEquals("ARRAY {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 2, y 2.5, z \"test\"},\n\tTUPLE {x 6, y 3.1, z \"zot\"},\n\tTUPLE {x 3, y 3.2, z \"glub\"},\n\tTUPLE {x 1, y 4.5, z \"test\"},\n\tTUPLE {x 7, y 4.5, z \"test\"},\n\tTUPLE {x 4, y 4.5, z \"glub\"},\n\tTUPLE {x 5, y 5.2, z \"test\"}\n}", src);
	}
	
	@Test
	public void testArray6() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
		        "VAR ar ARRAY TUPLE {y RATIONAL, z CHAR, x INTEGER};\n" +
		        "LOAD ar FROM a ORDER(ASC y, DESC z);" +
			"END;" +
			"ar";
		testEquals("ARRAY {y RATIONAL, z CHARACTER, x INTEGER} {\n\tTUPLE {y 2.5, z \"test\", x 2},\n\tTUPLE {y 3.1, z \"zot\", x 6},\n\tTUPLE {y 3.2, z \"glub\", x 3},\n\tTUPLE {y 4.5, z \"test\", x 1},\n\tTUPLE {y 4.5, z \"test\", x 7},\n\tTUPLE {y 4.5, z \"glub\", x 4},\n\tTUPLE {y 5.2, z \"test\", x 5}\n}", src);
	}
	
	@Test
	public void testArray7() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
		        "VAR ar ARRAY TUPLE {y RATIONAL, x INTEGER, z CHAR};\n" +
		        "LOAD ar FROM a ORDER();\n" +
		        "VAR b PRIVATE relation {z CHAR, y RATIONAL, x INTEGER} KEY {ALL BUT};\n" +
		        "LOAD b FROM ar;" +
			"END;" +
			"b";
		String expectedResult = 
			"RELATION {z CHARACTER, y RATIONAL, x INTEGER} {" +
				"\n\tTUPLE {z \"glub\", y 3.2, x 3}," +
				"\n\tTUPLE {z \"glub\", y 4.5, x 4}," +
				"\n\tTUPLE {z \"test\", y 2.5, x 2}," +
				"\n\tTUPLE {z \"test\", y 4.5, x 1}," +
				"\n\tTUPLE {z \"test\", y 4.5, x 7}," +
				"\n\tTUPLE {z \"test\", y 5.2, x 5}," +
				"\n\tTUPLE {z \"zot\", y 3.1, x 6}" +
				"\n}";
		testEquals(expectedResult, src);
	}
		
	@Test
	public void testMultipleAssignment1() {
		String src = 
			"BEGIN;" +
				"VAR a INTEGER INIT(1);" +
				"VAR b INTEGER INIT(2);" +
				"VAR c INTEGER INIT(3);" +
				"a := b + 1, b := a + 1, c := a + b;" +
			"END;" +
			"a + b + c";
		testEquals("8", src);
	}
	
	@Test
	public void testMultipleAssignment2() {
		String src = 
			"BEGIN;" +
				"VAR a INTEGER INIT(1);" +
				"VAR b INTEGER INIT(2);" +
				"VAR c INTEGER INIT(3);" +
				"c := a + b, a := b + 1, b := a + 1;" +
			"END;" +
			"a + b + c";
		testEquals("8", src);
	}
	
	@Test
	public void testMultipleAssignment3() {
		String src = 
			"BEGIN;" +
				"VAR a INTEGER INIT(1);" +
				"VAR b INTEGER INIT(2);" +
				"VAR c INTEGER INIT(3);" +
				"b := a + 1, c := a + b, a := b + 1;" +
			"END;" +
			"a + b + c";
		testEquals("8", src);
	}
	
	@Test
	public void testRelvar34() {
		String src =
				"BEGIN;" +
				" var items private relation {id integer, name character} init(" + 
				"    relation {" + 
				"      tuple {id 1, name 'hi'}," + 
	            "      tuple {id 2, name 'lo'}," + 
	            "      tuple {id 3, name 'do'}" +
	            "    }) key {id};" +
	            " var result private relation {r relation same_heading_as (items)} key {r};" +
	            " var inner private relation same_heading_as (items) key {id};" +
	            " insert inner relation {tuple from items where id = 1};" +
	            " insert result relation {tuple {r inner}};" +
	            "END;" +
	            "result";
		String expected = "RELATION {r RELATION {id INTEGER, name CHARACTER}} {" +
			"\n\tTUPLE {r RELATION {id INTEGER, name CHARACTER} {" +
			"\n\tTUPLE {id 1, name \"hi\"}" +
			"\n}}" +
			"\n}";
		testEquals(expected, src);
	}

	@Test
	public void testRelvar35() {
		String src =
				"BEGIN;" +
				"   var x private relation {a integer, b relation {c integer}}" + 
		        "     init (relation {tuple {a 1, b relation {tuple {c 2}}}}) " + 
		        "     key {a};" +
		 		"   update x where a = 1 : {b := update b : {c := 33}};" +
				"END;" +
				"x";
		String expected = "RELATION {a INTEGER, b RELATION {c INTEGER}} {" +
				"\n\tTUPLE {a 1, b RELATION {c INTEGER} {" +
				"\n\tTUPLE {c 33}" +
				"\n}}" +
				"\n}";
		testEquals(expected, src);
	}
	
	@Test
	public void testComplexExpression2() {
		String src =
			"begin;" +
			"  var myvar private relation {x INTEGER, y CHARACTER, z RATIONAL} key{x};" +
			"  myvar := RELATION {x INTEGER, y CHARACTER, z RATIONAL} {" +
			"	  TUPLE {x 1, y \"zot\", z 3.4}," +
			"	  TUPLE {x 2, y \"zap\", z 3.5}," +
			"	  TUPLE {x 3, y \"zot\", z 3.4}" +
			"  };" +
		    "end;" +
		    "extend myvar : {a := 5, b := 3.2, c := tuple {x 1, y 2.3}}";
		String expected =
			"RELATION {x INTEGER, y CHARACTER, z RATIONAL, a INTEGER, b RATIONAL, c TUPLE {x INTEGER, y RATIONAL}} {" +
			"\n\tTUPLE {x 1, y \"zot\", z 3.4, a 5, b 3.2, c TUPLE {x 1, y 2.3}}," +
			"\n\tTUPLE {x 2, y \"zap\", z 3.5, a 5, b 3.2, c TUPLE {x 1, y 2.3}}," +
			"\n\tTUPLE {x 3, y \"zot\", z 3.4, a 5, b 3.2, c TUPLE {x 1, y 2.3}}" +
			"\n}";			
		testEquals(expected, src);
	}
	
	@Test
	public void testRelvarInsert5() {
		String src =
			"begin;" +
			"  var myvar1 private relation {x integer, y rational} key {x};" +
			"  var myvar2 private relation {y rational, x integer} key {x};" +
			"  myvar2 := relation {tuple {x 1, y 2.3}, tuple {y 3.2, x 2}};" +
			"  insert myvar1 (myvar2 where x >= 2);" +
		    "end;" +
		    "myvar1";
		String expected = "RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 2, y 3.2}" +
			"\n}";
		testEquals(expected, src);				
	}
	
	@Test
	public void testRelvarInsert6() {
		String src =
			"begin;" +
			"  var myvar1 private relation {x integer, y rational} key {x};" +
			"  var myvar2 private relation {y rational, x integer} key {x};" +
			"  myvar2 := relation {tuple {x 1, y 2.3}, tuple {y 3.2, x 2}};" +
			"  insert myvar1 update myvar2 : {x := x * 2, y := y * 10.0};" +
		    "end;" +
		    "myvar1";
		String expected = "RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 2, y 23.0}," +
			"\n\tTUPLE {x 4, y 32.0}" +
			"\n}";
		testEquals(expected, src);				
	}
	
	@Test
	public void testRelvarInsert7() {
		String src =
			"begin;" +
			"  var myvar1 private relation {y rational, x integer} key {x};" +
			"  var myvar2 private relation {x integer, y rational} key {x};" +
			"  myvar2 := relation {tuple {y 2.3, x 1}, tuple {x 2, y 3.2}};" +
			"  myvar1 := myvar2;" +
		    "end;" +
		    "myvar1";
		String expected = "RELATION {y RATIONAL, x INTEGER} {" +
			"\n\tTUPLE {y 2.3, x 1}," +
			"\n\tTUPLE {y 3.2, x 2}" +
			"\n}";
		testEquals(expected, src);				
	}

	@Test
	public void testRelvarInsert9() {
		String src =
			"begin;" +
			"  var myvar1 private relation {x integer} key {x};" +
			"  var myvar2 private relation {x integer} key {x};" +
			"  myvar2 := relation {tuple {x 1}, tuple {x 2}};" +
			"  insert myvar1 (myvar2);" +
		    "end;" +
		    "myvar1";
		String expected = "RELATION {x INTEGER} {" +
			"\n\tTUPLE {x 1}," +
			"\n\tTUPLE {x 2}" +
			"\n}";
		testEquals(expected, src);				
	}

	@Test
	public void testRelvarInsert10() {
		String src =
			"begin;" +
			"  var myvar1 private relation {x integer} key {x};" +
			"  var myvar2 private relation {x integer} key {x};" +
			"  myvar2 := relation {tuple {x 1}, tuple {x 2}};" +
		    "end;" +
		    "update myvar2 : {x := x * 2}";
		String expected = "RELATION {x INTEGER} {" +
			"\n\tTUPLE {x 2}," +
			"\n\tTUPLE {x 4}" +
			"\n}";
		testEquals(expected, src);				
	}
		
	@Test
	public void testRelvarInsert11() {
		String src =
			"begin;" +
			"  var myvar1 private relation {x integer} key {x};" +
			"  var myvar2 private relation {x integer} key {x};" +
			"  myvar2 := relation {tuple {x 1}, tuple {x 2}};" +
			"  insert myvar1 update myvar2 : {x := x * 2};" +
		    "end;" +
		    "myvar1";
		String expected = "RELATION {x INTEGER} {" +
			"\n\tTUPLE {x 2}," +
			"\n\tTUPLE {x 4}" +
			"\n}";
		testEquals(expected, src);				
	}

  	@Test
  	public void testPossrepInteger1() {
  		String src = 
  			"THE_VALUE(THE_VALUE(1))"; 
  		String expected = "1";
  		testEquals(expected, src);  		
  	}
  	
  	@Test
  	public void testPossrepInteger2() {
  		String src = 
  			"THE_VALUE(1)"; 
  		String expected = "1";
  		testEquals(expected, src);  		
  	}
  	
  	@Test
  	public void testPossrepInteger3() {
  		String src = 
  			"THE_VALUE(INTEGER(1))"; 
  		String expected = "1";
  		testEquals(expected, src);  		
  	}
  	
  	@Test
  	public void testPossrepInteger4() {
  		String src = 
  			"INTEGER(INTEGER(1))"; 
  		String expected = "1";
  		testEquals(expected, src);  		
  	}
  	
  	@Test
  	public void testPossrepInteger5() {
  		String src = 
  			"THE_VALUE(INTEGER(1))"; 
  		String expected = "1";
  		testEquals(expected, src);  		
  	}
  	
  	@Test
  	public void testPossrepInteger6() {
  		String src = 
  			"INTEGER(1)"; 
  		String expected = "1";
  		testEquals(expected, src);  		
  	}
  	
  	@Test
  	public void testPossrepInteger7() {
  		String src = 
  			"THE_VALUE(1)"; 
  		String expected = "1";
  		testEquals(expected, src);  		
  	}

  	@Test
  	public void testPossrepIntegerBuiltin01() {
  		String src = "MAX(RELATION {TUPLE {x 1}, TUPLE {x 2}}, x)";
  		String expected = "2";
  		testEquals(expected, src);
  	}
  
  	@Test
  	public void testPossrepIntegerBuiltin02() {
  		String src = "SUM(RELATION {TUPLE {x 1}, TUPLE {x 2}}, x)";
  		String expected = "3";
  		testEquals(expected, src);
  	}
  	 	
}
