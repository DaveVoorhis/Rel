package org.reldb.rel.v0.storage;

import org.reldb.rel.v0.generator.*;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.types.builtin.TypeBoolean;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;
import org.reldb.rel.v0.types.builtin.TypeRational;
import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.*;

/** Class for defining built-in operators. */
public class BuiltinOperators {
	
	private Generator generator;
	
	private void is_empty(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("IS_EMPTY", 
					"// True if relation r is empty\n" +
					"IS_EMPTY(r RELATION{*}) RETURNS BOOLEAN",
					new Type[] {TypeRelation.getEmptyRelationType()}, 
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueRelation relation = (ValueRelation)arguments[0];
							return relation.is_empty();
						}
					}
			)
		);
	}
	
	private void countRelation(final RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("COUNT", 
					"// Return cardinality of relation r\n" +
					"COUNT(r RELATION{*}) RETURNS INTEGER",
					new Type[] {TypeRelation.getEmptyRelationType()}, 
					TypeInteger.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueRelation relation = (ValueRelation)arguments[0];
							return ValueInteger.select(generator, relation.getCardinality());
						}
					}
			)
		);
	}
	
	private void countArray(final RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("COUNT", 
					"// Return cardinality of ARRAY r\n" +
					"COUNT(r ARRAY OF TUPLE {*}) RETURNS INTEGER",
					new Type[] {TypeArray.getEmptyArrayType()}, 
					TypeInteger.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueArray array = (ValueArray)arguments[0];
							return ValueInteger.select(generator, array.getCount());
						}
					}
			)
		);
	}
	
	private void sum(RelDatabase database) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("AGGREGATE_SUM_INTEGER", 
						"// INTEGER sum of r\n" +
						"AGGREGATE_SUM_INTEGER(r ARRAY OF TUPLE {AGGREGAND INT, AGGREGATION_SERIAL INT}) RETURNS INTEGER",
						new Type[] {TypeArray.getEmptyArrayType()},
						TypeInteger.getInstance(),
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								ValueArray array = (ValueArray)arguments[0];
								return array.sumInteger(0);
							}
						}
				)
			);
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("AGGREGATE_SUM_RATIONAL", 
						"// RATIONAL sum of r\n" +
						"AGGREGATE_SUM_RATIONAL(r ARRAY OF TUPLE {AGGREGAND RATIONAL, AGGREGATION_SERIAL INT}) RETURNS RATIONAL",
						new Type[] {TypeArray.getEmptyArrayType()}, 
						TypeRational.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								ValueArray array = (ValueArray)arguments[0];
								return array.sumRational(0);
							}
						}
				)
			);
	}
	
	private void avg(RelDatabase database) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("AGGREGATE_AVG_INTEGER", 
						"// Arithmetic mean of r\n" +
						"AGGREGATE_AVG_INTEGER(r ARRAY OF TUPLE {AGGREGAND INT, AGGREGATION_SERIAL INT}) RETURNS RATIONAL",
						new Type[] {TypeArray.getEmptyArrayType()}, 
						TypeRational.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								ValueArray array = (ValueArray)arguments[0];
								return array.avgInteger(0);
							}
						}
				)
			);
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("AGGREGATE_AVG_RATIONAL", 
						"// Arithmetic mean of r\n" +
						"AGGREGATE_AVG_INTEGER(r ARRAY OF TUPLE {AGGREGAND RATIONAL, AGGREGATION_SERIAL INT}) RETURNS RATIONAL",
						new Type[] {TypeArray.getEmptyArrayType()}, 
						TypeRational.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								ValueArray array = (ValueArray)arguments[0];
								return array.avgRational(0);
							}
						}
				)
			);
	}

	private void max(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("AGGREGATE_MAX", 
					"// Maximum of r\n" +
					"AGGREGATE_MAX(r ARRAY OF TUPLE {AGGREGAND ALPHA, AGGREGATION_SERIAL INT}) RETURNS ALPHA",
					new Type[] {TypeArray.getEmptyArrayType()}, 
					TypeRational.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueArray array = (ValueArray)arguments[0];
							return array.max(0);
						}
					}
			)
		);
	}
	
	private void min(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("AGGREGATE_MIN", 
					"// Minimum of r\n" +
					"AGGREGATE_MIN(r ARRAY OF TUPLE {AGGREGAND ALPHA, AGGREGATION_SERIAL INT}) RETURNS ALPHA",
					new Type[] {TypeArray.getEmptyArrayType()}, 
					TypeRational.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueArray array = (ValueArray)arguments[0];
							return array.min(0);
						}
					}
			)
		);
	}
	
	private void and(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("AGGREGATE_AND", 
					"// Logical AND of r\n" +
					"AGGREGATE_AND(r ARRAY OF TUPLE {AGGREGAND BOOLEAN, AGGREGATION_SERIAL INT}) RETURNS BOOLEAN",
					new Type[] {TypeArray.getEmptyArrayType()}, 
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueArray array = (ValueArray)arguments[0];
							return array.and(0);
						}
					}
			)
		);
	}
	
	private void or(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("AGGREGATE_OR", 
					"// Logical OR of r\n" +
					"AGGREGATE_OR(r ARRAY OF TUPLE {AGGREGAND BOOLEAN, AGGREGATION_SERIAL INT}) RETURNS BOOLEAN",
					new Type[] {TypeArray.getEmptyArrayType()}, 
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueArray array = (ValueArray)arguments[0];
							return array.or(0);
						}
					}
			)
		);
	}
	
	private void xor(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("AGGREGATE_XOR", 
					"// Logical exclusive-OR of r\n" +
					"AGGREGATE_XOR(r ARRAY OF TUPLE {AGGREGAND BOOLEAN, AGGREGATION_SERIAL INT}) RETURNS BOOLEAN",
					new Type[] {TypeArray.getEmptyArrayType()}, 
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueArray array = (ValueArray)arguments[0];
							return array.xor(0);
						}
					}
			)
		);
	}

	private void equiv(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("AGGREGATE_EQUIV", 
					"// Logical EQUIV (aka '=') of r\n" +
					"AGGREGATE_EQUIV(r ARRAY OF TUPLE {AGGREGAND BOOLEAN, AGGREGATION_SERIAL INT}) RETURNS BOOLEAN",
					new Type[] {TypeArray.getEmptyArrayType()}, 
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueArray array = (ValueArray)arguments[0];
							return array.equiv(0);
						}
					}
			)
		);
	}

	private void union(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("AGGREGATE_UNION", 
					"// UNION of r\n" +
					"AGGREGATE_UNION(r ARRAY OF TUPLE {AGGREGAND RELATION {*}, AGGREGATION_SERIAL INT}) RETURNS RELATION {*}",
					new Type[] {TypeArray.getEmptyArrayType()}, 
					TypeRelation.getEmptyRelationType(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueArray array = (ValueArray)arguments[0];
							return array.union(0);
						}
					}
			)
		);
	}
	
	private void xunion(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("AGGREGATE_XUNION", 
					"// exclusive-UNION of r\n" +
					"AGGREGATE_XUNION(r ARRAY OF TUPLE {AGGREGAND RELATION {*}, AGGREGATION_SERIAL INT}) RETURNS RELATION {*}",
					new Type[] {TypeArray.getEmptyArrayType()}, 
					TypeRelation.getEmptyRelationType(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueArray array = (ValueArray)arguments[0];
							return array.xunion(0);
						}
					}
			)
		);
	}
	
	private void d_union(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("AGGREGATE_D_UNION", 
					"// disjoint-UNION of r\n" +
					"AGGREGATE_DUNION(r ARRAY OF TUPLE {AGGREGAND RELATION {*}, AGGREGATION_SERIAL INT}) RETURNS RELATION {*}",
					new Type[] {TypeArray.getEmptyArrayType()}, 
					TypeRelation.getEmptyRelationType(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueArray array = (ValueArray)arguments[0];
							return array.d_union(0);
						}
					}
			)
		);
	}
	
	private void intersect(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("AGGREGATE_INTERSECT", 
					"// INTERSECT of r\n" +
					"AGGREGATE_INTERSECT(r ARRAY OF TUPLE {AGGREGAND RELATION {*}, AGGREGATION_SERIAL INT}) RETURNS RELATION {*}",
					new Type[] {TypeArray.getEmptyArrayType()}, 
					TypeRelation.getEmptyRelationType(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueArray array = (ValueArray)arguments[0];
							return array.intersect(0);
						}
					}
			)
		);
	}
	
	// TODO - rewrite to work on ARRAYs
	private void exactly(RelDatabase database) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("EXACTLY", 
						"// Return true if COUNT of true equals n\n" +
						"EXACTLY(r ARRAY OF TUPLE {AGGREGAND BOOLEAN, AGGREGATION_SERIAL INT}, idx INTEGER, n INTEGER) RETURNS BOOLEAN",
						new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance(), TypeInteger.getInstance()}, 
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								ValueRelation relation = (ValueRelation)arguments[0];
								int attributeIndex = (int)arguments[1].longValue();
								long nCount = arguments[2].longValue();
								return relation.exactly(nCount, attributeIndex);
							}
						}
				)
			);
	}

	private static Type[] primitiveTypes = new Type[] {
		TypeBoolean.getInstance(),
		TypeInteger.getInstance(),
		TypeRational.getInstance(),
		TypeCharacter.getInstance()
	};
	
	private void cast_as_boolean(RelDatabase database) {
		for (Type type: primitiveTypes) {
			database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("CAST_AS_BOOLEAN", 
						"// Return n as BOOLEAN\n" +
						"CAST_AS_BOOLEAN(n " + type.getSignature() + ") RETURNS BOOLEAN",
						new Type[] {type}, 
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueBoolean.select(generator, arguments[0].booleanValue());
							}
						}
				)
			);
		}
	}

	private void cast_as_integer(RelDatabase database) {
		for (Type type: primitiveTypes) {
			database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("CAST_AS_INTEGER", 
						"// Return n as INTEGER\n" +
						"CAST_AS_INTEGER(n " + type.getSignature() + ") RETURNS INTEGER",
						new Type[] {type}, 
						TypeInteger.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueInteger.select(generator, arguments[0].longValue());
							}
						}
				)
			);
		}		
	}
	
	private void cast_as_rational(RelDatabase database) {
		for (Type type: primitiveTypes) {
			database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("CAST_AS_RATIONAL", 
						"// Return n as RATIONAL\n" +
						"CAST_AS_RATIONAL(n " + type.getSignature() + ") RETURNS RATIONAL",
						new Type[] {type}, 
						TypeRational.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueRational.select(generator, arguments[0].doubleValue());
							}
						}
				)
			);
		}				
	}
	
	private void cast_as_char(RelDatabase database) {
		for (Type type: primitiveTypes) {
			database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("CAST_AS_CHAR", 
						"// Return n as CHAR\n" +
						"CAST_AS_CHAR(n " + type.getSignature() + ") RETURNS CHAR",
						new Type[] {type}, 
						TypeCharacter.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueCharacter.select(generator, arguments[0].stringValue());
							}
						}
				)
			);
		}						
	}
	
	private void equals(RelDatabase database, String docs, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.EQUALS, 
						docs,
						parameters, 
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueBoolean.select(generator, arguments[0].compareTo(arguments[1])==0);
							}
						}
				)
			);		
	}
	
	private void equals(RelDatabase database) {
		equals(database, 
				"// Return true if p equals q\n" +
				"OP_EQUALS(p RELATION {*}, q RELATION {*}) RETURNS BOOLEAN", 
				new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		equals(database, 
				"// Return true if p equals q\n" +
				"OP_EQUALS(p TUPLE {*}, q TUPLE {*}) RETURNS BOOLEAN",
				new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		equals(database, 
				"// Return true if p equals q\n" +
				"OP_EQUALS(p ALPHA, q ALPHA) RETURNS BOOLEAN",
				new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}
	
	private void notequals(RelDatabase database, String docs, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.NOTEQUALS, 
						docs,
						parameters, 
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueBoolean.select(generator, arguments[0].compareTo(arguments[1])!=0);
							}
						}
				)
			);		
	}
	
	private void notequals(RelDatabase database) {
		notequals(database, 
				"// Return true if p is not equal to q\n" +
				"OP_NOTEQUALS(p RELATION {*}, q RELATION {*}) RETURNS BOOLEAN",
				new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		notequals(database, 
				"// Return true if p is not equal to q\n" +
				"OP_NOTEQUALS(p TUPLE {*}, q TUPLE {*}) RETURNS BOOLEAN",
				new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		notequals(database, 
				"// Return true if p is not equal to q\n" +
				"OP_NOTEQUALS(p ALPHA, q ALPHA) RETURNS BOOLEAN",
				new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}
	
	private void greaterthanequals(RelDatabase database, String docs, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.GREATERTHANOREQUALS, 
						docs,
						parameters, 
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueBoolean.select(generator, arguments[0].compareTo(arguments[1])>=0);
							}
						}
				)
			);		
	}
		
	private void superset(String opName, RelDatabase database, String docs, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(opName, 
						docs,
						parameters, 
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueBoolean.select(generator, ((ValueRelation)arguments[0]).isSupersetOf((ValueRelation)arguments[1]));
							}
						}
				)
			);		
	}
	
	private void greaterthanequals(RelDatabase database) {
		superset(BuiltinTypeBuilder.GREATERTHANOREQUALS, database, 
				"// Return true if p is greater than or equal to q\n" +
				"OP_GREATERTHANOREQUALS(p RELATION {*}, q RELATION {*}) RETURNS BOOLEAN",
				new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		superset(BuiltinTypeBuilder.SUPERSETOREQUAL, database, 
				"// Return true if p is a superset of or equal to q\n" +
				"OP_SUPERSETOREQUAL(p RELATION {*}, q RELATION {*}) RETURNS BOOLEAN",
				new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		greaterthanequals(database,
				"// Return true if p is greater than or equal to q\n" +
				"OP_GREATERTHANOREQUALS(p TUPLE {*}, q TUPLE {*}) RETURNS BOOLEAN",
				new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		greaterthanequals(database, 
				"// Return true if p is greater than or equal to q\n" +
				"OP_GREATERTHANOREQUALS(p ALPHA, q ALPHA) RETURNS BOOLEAN",
				new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}
	
	private void lessthanequals(RelDatabase database, String docs, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.LESSTHANOREQUALS, 
						docs,
						parameters,
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueBoolean.select(generator, arguments[0].compareTo(arguments[1])<=0);
							}
						}
				)
			);		
	}
	
	private void subset(String opName, RelDatabase database, String docs, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(opName, 
						docs,
						parameters,
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueBoolean.select(generator, ((ValueRelation)arguments[0]).isSubsetOf((ValueRelation)arguments[1]));
							}
						}
				)
			);		
	}
	
	private void lessthanequals(RelDatabase database) {
		subset(BuiltinTypeBuilder.LESSTHANOREQUALS, database, 
				"// Return true if p is less than or equal to q\n" +
				"OP_LESSTHANOREQUALS(p RELATION {*}, q RELATION {*}) RETURNS BOOLEAN",
				new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		subset(BuiltinTypeBuilder.SUBSETOREQUAL, database, 
				"// Return true if p is a subset of or equal to q\n" +
				"OP_SUBSETOREQUAL(p RELATION {*}, q RELATION {*}) RETURNS BOOLEAN",
				new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		lessthanequals(database, 
				"// Return true if p is less than or equal to q\n" +
				"OP_LESSTHANOREQUALS(p TUPLE {*}, q TUPLE {*}) RETURNS BOOLEAN",
				new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		lessthanequals(database, 
				"// Return true if p is less than or equal to q\n" +
				"OP_LESSTHANOREQUALS(p ALPHA, q ALPHA) RETURNS BOOLEAN",
				new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}
	
	private void greaterthan(RelDatabase database, String docs, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.GREATERTHAN, 
						docs,
						parameters, 
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueBoolean.select(generator, arguments[0].compareTo(arguments[1])>0);
							}
						}
				)
			);		
	}
	
	private void propersuperset(String opName, RelDatabase database, String docs, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(opName, 
						docs,
						parameters, 
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueBoolean.select(generator, ((ValueRelation)arguments[0]).isProperSupersetOf((ValueRelation)arguments[1]));
							}
						}
				)
			);		
	}

	private void greaterthan(RelDatabase database) {
		propersuperset(BuiltinTypeBuilder.GREATERTHAN, database, 
				"// Return true if p is greater than q\n" +
				"OP_GREATERTHAN(p RELATION {*}, q RELATION {*}) RETURNS BOOLEAN",
				new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		propersuperset(BuiltinTypeBuilder.SUPERSET, database, 
				"// Return true if p is a proper superset of q\n" +
				"OP_SUPERSET(p RELATION {*}, q RELATION {*}) RETURNS BOOLEAN",
				new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		greaterthan(database, 
				"// Return true if p is greater than q\n" +
				"OP_GREATERTHAN(p TUPLE {*}, q TUPLE {*}) RETURNS BOOLEAN",
				new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		greaterthan(database, 
				"// Return true if p is greater than q\n" +
				"OP_GREATERTHAN(p ALPHA, q ALPHA) RETURNS BOOLEAN",
				new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}
	
	private void lessthan(RelDatabase database, String docs, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.LESSTHAN, 
						docs,
						parameters,
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueBoolean.select(generator, arguments[0].compareTo(arguments[1])<0);
							}
						}
				)
			);		
	}
	
	private void propersubset(String opName, RelDatabase database, String docs, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(opName, 
						docs,
						parameters,
						TypeBoolean.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueBoolean.select(generator, ((ValueRelation)arguments[0]).isProperSubsetOf((ValueRelation)arguments[1]));
							}
						}
				)
			);		
	}
	
	private void lessthan(RelDatabase database) {
		propersubset(BuiltinTypeBuilder.LESSTHAN, database, 
				"// Return true if p is less than q\n" +
				"OP_LESSTHAN(p RELATION {*}, q RELATION {*}) RETURNS BOOLEAN",
				new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		propersubset(BuiltinTypeBuilder.SUBSET, database, 
				"// Return true if p is a proper subset of q\n" +
				"OP_SUBSET(p RELATION {*}, q RELATION {*}) RETURNS BOOLEAN",
				new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		lessthan(database, 
				"// Return true if p is less than q\n" +
				"OP_LESSTHAN(p TUPLE {*}, q TUPLE {*}) RETURNS BOOLEAN",
				new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		lessthan(database, 
				"// Return true if p is less than q\n" +
				"OP_LESSTHAN(p TUPLE {*}, q TUPLE {*}) RETURNS BOOLEAN",
				new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}

	private void getuniquenumber(final RelDatabase database) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("GET_UNIQUE_NUMBER", 
						"// Obtain a unique number\n" +
						"GET_UNIQUE_NUMBER() RETURNS INTEGER",
						new Type[] {}, 
						TypeInteger.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueInteger.select(generator, database.getUniqueID());
							}
						}
				)
			);		
	}
	
	private void setuniquenumber(final RelDatabase database) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeProcedure("SET_UNIQUE_NUMBER", 
						"// Set the next unique number. Must be greater than the current next unique number.\n" +
						"SET_UNIQUE_NUMBER(n INTEGER)",
						new Type[] {TypeInteger.getInstance()}, 
						new NativeProcedure() {
							public void execute(Value arguments[]) {
								database.setUniqueID(((ValueInteger)arguments[0]).longValue());
							}
						}
				)
			);
	}
	
	private void sequence2(final RelDatabase database, Type[] parameters) {
		Heading resultHeading = new Heading();
		resultHeading.add("N", TypeInteger.getInstance());
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("SEQUENCE",
						"// Return a RELATION {n INTEGER} where n ranges from start to end.\n" +
						"SEQUENCE(start INTEGER, end INTEGER) RETURNS RELATION {n INTEGER}",
						parameters,
						(new TypeRelation(resultHeading)), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueRelation.sequence(generator, (ValueInteger)arguments[0], (ValueInteger)arguments[1]);
							}
						}
				)
			);
	}

	private void sequence3(final RelDatabase database, Type[] parameters) {
		Heading resultHeading = new Heading();
		resultHeading.add("N", TypeInteger.getInstance());
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("SEQUENCE",
						"// Return a RELATION {n INTEGER} where n ranges from start to end, stepping by step.\n" +
						"SEQUENCE(start INTEGER, end INTEGER, step INTEGER) RETURNS RELATION {n INTEGER}",
						parameters,
						(new TypeRelation(resultHeading)), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueRelation.sequence(generator, (ValueInteger)arguments[0], (ValueInteger)arguments[1], (ValueInteger)arguments[2]);
							}
						}
				)
			);
	}
	
	private void sequence(RelDatabase database) {
		sequence2(database, new Type[] {TypeInteger.getInstance(), TypeInteger.getInstance()});
		sequence3(database, new Type[] {TypeInteger.getInstance(), TypeInteger.getInstance(), TypeInteger.getInstance()});
	}
	
	private void quote(RelDatabase database) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("QUOTE",
						"// Return s with special characters quoted\n" +
						"QUOTE(s CHAR) RETURNS CHAR",
						new Type[] {TypeCharacter.getInstance()},
						TypeCharacter.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueCharacter.select(generator, StringUtils.quote(arguments[0].stringValue()));
							}
						}
				)
			);
	}
	
	private void unquote(RelDatabase database) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("UNQUOTE",
						"// Return s with quoted special characters unquoted\n" +
						"UNQUOTE(s CHAR) RETURNS CHAR",
						new Type[] {TypeCharacter.getInstance()},
						TypeCharacter.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								return ValueCharacter.select(generator, StringUtils.unquote(arguments[0].stringValue()));
							}
						}
				)
			);		
	}
	
	/** Assorted CHAR operators, largely based on Java String methods. */

	private void is_digits(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("IS_DIGITS",
					"// Return TRUE if s is all numeric digits\n" +
					"IS_DIGITS(s CHAR) RETURNS BOOLEAN",
					new Type[] {TypeCharacter.getInstance()},
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							String sbuf = arguments[0].stringValue();
							for (int i=0; i<sbuf.length(); i++)
								if (!Character.isDigit(sbuf.charAt(i)))
									return ValueBoolean.select(generator, false);
							return ValueBoolean.select(generator, true);
						}
					}
			)
		);		
	}

	private void length(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("LENGTH",
					"// Return the length of s\n" +
					"LENGTH(s CHAR) RETURNS INTEGER",
					new Type[] {TypeCharacter.getInstance()},
					TypeInteger.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							return ValueInteger.select(generator, arguments[0].stringValue().length());
						}
					}
			)
		);
	}

	private void substring2(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("SUBSTRING",
					"// Return the 0-based substring of s, starting from beginIndex\n" +
					"SUBSTRING(s CHAR, beginIndex INTEGER) RETURNS CHAR",
					new Type[] {TypeCharacter.getInstance(), TypeInteger.getInstance()},
					TypeCharacter.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Substring, 0 based
							return ValueCharacter.select(generator, arguments[0].stringValue().substring((int)arguments[1].longValue()));
						}
					}
			)
		);		
	}
	
	private void substring3(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("SUBSTRING",
					"// Return the 0-based substring of s, starting from beginIndex and extending to endIndex - 1\n" +
					"SUBSTRING(s CHAR, beginIndex INTEGER, endIndex INTEGER) RETURNS CHAR",
					new Type[] {TypeCharacter.getInstance(), TypeInteger.getInstance(), TypeInteger.getInstance()},
					TypeCharacter.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Substring, 0 based
							return ValueCharacter.select(generator, arguments[0].stringValue().substring((int)arguments[1].longValue(), (int)arguments[2].longValue()));
						}
					}
			)
		);		
	}
	
	private void compare_to(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("COMPARE_TO",
					"// Perform the lexicographic comparison of p and q, to return the value 0 if p is equal to q;\n" +
					"// a value less than 0 if p is lexicographically less than q;\n" +
					"// and a value greater than 0 if p is lexicographically greater than q.\n" +
					"COMPARE_TO(p CHAR, q CHAR) RETURNS INTEGER",
					new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance()},
					TypeInteger.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Compares two strings lexicographically.
							return ValueInteger.select(generator, arguments[0].stringValue().compareTo(arguments[1].stringValue()));
						}
					}
			)
		);		
	}
	
	private void compare_to_ignore_case(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("COMPARE_TO_IGNORE_CASE",
					"// Perform the case-insensitive lexicographic comparison of p and q, to return the value 0 if p is equal to q;\n" +
					"// a value less than 0 if p is lexicographically less than q;\n" +
					"// and a value greater than 0 if p is lexicographically greater than q.\n" +
					"COMPARE_TO_IGNORE_CASE(p CHAR, q CHAR) RETURNS INTEGER",
					new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance()},
					TypeInteger.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Compares two strings lexicographically.
							return ValueInteger.select(generator, arguments[0].stringValue().compareToIgnoreCase(arguments[1].stringValue()));
						}
					}
			)
		);		
	}

	private void ends_with(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("ENDS_WITH",
					"// Return true if p ends with suffix q\n" +
					"ENDS_WITH(p CHAR, q CHAR) RETURNS BOOLEAN",
					new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance()},
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							return ValueBoolean.select(generator, arguments[0].stringValue().endsWith(arguments[1].stringValue()));
						}
					}
			)
		);		
	}

	private void equals_ignore_case(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("EQUALS_IGNORE_CASE",
					"// Return true if p equals q, ignoring case\n" +
					"EQUALS_IGNORE_CASE(p CHAR, q CHAR) RETURNS BOOLEAN",
					new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance()},
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Compares this String to another String, ignoring case considerations.
							return ValueBoolean.select(generator, arguments[0].stringValue().equalsIgnoreCase(arguments[1].stringValue()));
						}
					}
			)
		);		
	}

	private void index_of2(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("INDEX_OF",
					"// Return the 0-based index of the first occurrence of needle in haystack. Return -1 if not found.\n" +
					"INDEX_OF(CHAR haystack, CHAR needle) RETURNS INTEGER",
					new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance()},
					TypeInteger.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Returns the index within this string of the first occurrence of the specified substring.
							return ValueInteger.select(generator, arguments[0].stringValue().indexOf(arguments[1].stringValue()));
						}
					}
			)
		);		
	}
	
	private void index_of3(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("INDEX_OF",
					"// Return the 0-based index of the first occurrence of needle in haystack, starting at index. Return -1 if not found.\n" +
					"INDEX_OF(CHAR haystack, CHAR needle, INTEGER index) RETURNS INTEGER",
					new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance(), TypeInteger.getInstance()},
					TypeInteger.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Returns the index within this string of the first occurrence of the 
							// specified substring, starting at the specified index.
							return ValueInteger.select(generator, arguments[0].stringValue().indexOf(arguments[1].stringValue(), (int)arguments[2].longValue()));
						}
					}
			)
		);		
	}

	private void last_index_of2(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("LAST_INDEX_OF",
					"// Return the 0-based index of the last occurrence of needle in haystack. Return -1 if not found.\n" +
					"LAST_INDEX_OF(CHAR haystack, CHAR needle) RETURNS INTEGER",
					new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance()},
					TypeInteger.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Returns the index within this string of the last occurrence of the specified substring.
							return ValueInteger.select(generator, arguments[0].stringValue().lastIndexOf(arguments[1].stringValue()));
						}
					}
			)
		);		
	}
	
	private void last_index_of3(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("LAST_INDEX_OF",
					"// Return the 0-based index of the last occurrence of needle in haystack, starting at index. Return -1 if not found.\n" +
					"LAST_INDEX_OF(CHAR haystack, CHAR needle, INTEGER index) RETURNS INTEGER",
					new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance(), TypeInteger.getInstance()},
					TypeInteger.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Returns the index within this string of the last occurrence of the 
							// specified substring, starting at the specified index.
							return ValueInteger.select(generator, arguments[0].stringValue().lastIndexOf(arguments[1].stringValue(), (int)arguments[2].longValue()));
						}
					}
			)
		);		
	}
	
	private void matches(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("MATCHES",
					"// Return TRUE if p matches the regular expression regexp.\n" +
					"MATCHES(CHAR p, CHAR regexp) RETURNS BOOLEAN",
					new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance()},
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Tells whether or not this string matches the given regular expression.
							return ValueBoolean.select(generator, arguments[0].stringValue().matches(arguments[1].stringValue()));
						}
					}
			)
		);		
	}

	private void region_matches(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("REGION_MATCHES",
					"// Return TRUE if two string regions are equal.\n" +
					"MATCHES(CHAR p, BOOLEAN ignoreCase, INTEGER offsetP, CHAR q, INTEGER offsetQ, INTEGER length) RETURNS BOOLEAN",
					new Type[] {TypeCharacter.getInstance(), TypeBoolean.getInstance(), TypeInteger.getInstance(), TypeCharacter.getInstance(), TypeInteger.getInstance(), TypeInteger.getInstance()},
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Tests if two string regions are equal.
							return ValueBoolean.select(generator, 
									arguments[0].stringValue().regionMatches(arguments[1].booleanValue(),
									(int)arguments[2].longValue(),
									arguments[3].stringValue(),
									(int)arguments[4].longValue(),
									(int)arguments[5].longValue()));
						}
					}
			)
		);		
	}
	
	private void replace_all(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("REPLACE_ALL",
					"// Return p with each substring of p that matches regexp replaced with q.\n" +
					"REPLACE_ALL(CHAR p, CHAR regexp, CHAR q) RETURNS CHAR",
					new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance(), TypeCharacter.getInstance()},
					TypeCharacter.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							// Replaces each substring of this string that matches the given regular expression with the given replacement.
							return ValueCharacter.select(generator, arguments[0].stringValue().replaceAll(arguments[1].stringValue(), arguments[2].stringValue()));
						}
					}
			)
		);		
	}

	private void replace_first(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("REPLACE_FIRST",
				"// Return p with the first substring of p that matches regexp replaced with q.\n" +
				"REPLACE_FIRST(CHAR p, CHAR regexp, CHAR q) RETURNS CHAR",
				new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance(), TypeCharacter.getInstance()},
				TypeCharacter.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Replaces the first substring of this string that matches the given regular expression with the given replacement.
						return ValueCharacter.select(generator, arguments[0].stringValue().replaceFirst(arguments[1].stringValue(), arguments[2].stringValue()));
					}
				}
			)
		);		
	}

	private void split2(RelDatabase database) {
		Heading arrayHeading = new Heading();
		arrayHeading.add("str", TypeCharacter.getInstance());
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("SPLIT",
				"// Split p around matches of the regexp.\n" +
				"SPLIT(CHAR p, CHAR regexp) RETURNS ARRAY OF TUPLE {str CHAR}",
				new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance()},
				new TypeArray(arrayHeading), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						ValueArray array = new ValueArray(generator);
						String[] ss = arguments[0].stringValue().split(arguments[1].stringValue());
						for (int i=0; i<ss.length; i++) {
							Value[] tupleValues = new Value[] {ValueCharacter.select(generator, ss[i])};
							ValueTuple tuple = new ValueTuple(generator, tupleValues);
							array.append(tuple);
						}
						return array;
					}
				}
			)
		);		
	}

	private void split3(RelDatabase database) {
		Heading arrayHeading = new Heading();
		arrayHeading.add("str", TypeCharacter.getInstance());
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("SPLIT",
				"// Split p around matches of the regexp up to n times.\n" +
				"SPLIT(CHAR p, CHAR regexp, n INTEGER) RETURNS ARRAY OF TUPLE {str CHAR}",
				new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance(), TypeInteger.getInstance()},
				new TypeArray(arrayHeading), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						ValueArray array = new ValueArray(generator);
						String[] ss = arguments[0].stringValue().split(arguments[1].stringValue(), (int)arguments[2].longValue());
						for (int i=0; i<ss.length; i++) {
							Value[] tupleValues = new Value[] {ValueCharacter.select(generator, ss[i])};
							ValueTuple tuple = new ValueTuple(generator, tupleValues);
							array.append(tuple);
						}
						return array;
					}
				}
			)
		);		
	}

	private void starts_with2(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("STARTS_WITH",
				"// Return TRUE if p starts with prefix q.\n" +
				"STARTS_WITH(CHAR p, CHAR q) RETURNS BOOLEAN",
				new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance()},
				TypeBoolean.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Tests if this string starts with the specified prefix.
						return ValueBoolean.select(generator, arguments[0].stringValue().startsWith(arguments[1].stringValue()));
					}
				}
			)
		);		
	}

	private void starts_with3(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("STARTS_WITH",
				"// Return TRUE if p starts with prefix q, starting at index.\n" +
				"STARTS_WITH(CHAR p, CHAR q, INTEGER index) RETURNS BOOLEAN",
				new Type[] {TypeCharacter.getInstance(), TypeCharacter.getInstance(), TypeInteger.getInstance()},
				TypeBoolean.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Tests if this string starts with the specified prefix beginning at a specified index.
						return ValueBoolean.select(generator, arguments[0].stringValue().startsWith(arguments[1].stringValue(), (int)arguments[2].longValue()));
					}
				}
			)
		);		
	}
	
	private void to_lower_case(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("TO_LOWER_CASE",
				"// Return p converted to lower case.\n" +
				"TO_LOWER_CASE(CHAR p) RETURNS CHAR",
				new Type[] {TypeCharacter.getInstance()},
				TypeCharacter.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						return ValueCharacter.select(generator, arguments[0].stringValue().toLowerCase());
					}
				}
			)
		);		
	}

	private void to_upper_case(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("TO_UPPER_CASE",
				"// Return p converted to upper case.\n" +
				"TO_UPPER_CASE(CHAR p) RETURNS CHAR",
				new Type[] {TypeCharacter.getInstance()},
				TypeCharacter.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						return ValueCharacter.select(generator, arguments[0].stringValue().toUpperCase());
					}
				}
			)
		);
	}

	private void trim(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("TRIM",
				"// Return p with leading and trailing whitespace removed\n" +
				"TRIM(p CHAR) RETURNS CHAR",
				new Type[] {TypeCharacter.getInstance()},
				TypeCharacter.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						return ValueCharacter.select(generator, arguments[0].stringValue().trim());
					}
				}
			)
		);
	}

	/** Math operators.  These are essentially a wrapper around the Java Math package. */

	private void e(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("E",
				"// Return the RATIONAL value that is closer than any other to e, the base of the natural logarithms.\n" +
				"E() RETURNS RATIONAL",
				new Type[] {},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// The RATIONAL value that is closer than any other to e, the base of
						// the natural logarithms.
						return ValueRational.select(generator, Math.E);
					}
				}
			)
		);
	}

	private void pi(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("PI",
				"// Return the RATIONAL value that is closer than any other to pi, the ratio of the circumference of a circle to its diameter\n" +
				"PI() RETURNS RATIONAL",
				new Type[] {},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// The RATIONAL value that is closer than any other to pi, the ratio of
						// the circumference of a circle to its diameter.
						return ValueRational.select(generator, Math.PI);
					}
				}
			)
		);
	}

	private void absRational(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("ABS",
				"// Return the absolute value of p\n" +
				"ABS(p RATIONAL) RETURNS RATIONAL",	
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the absolute value of a RATIONAL value.
						return ValueRational.select(generator, Math.abs(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void absInteger(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("ABS",
				"// Return the absolute value of p\n" +
				"ABS(p INTEGER) RETURNS INTEGER",	
				new Type[] {TypeInteger.getInstance()},
				TypeInteger.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the absolute value of an INTEGER value.
						return ValueInteger.select(generator, Math.abs(arguments[0].longValue()));
					}
				}
			)
		);
	}

	private void acos(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("ACOS",
				"// Return the arc cosine of an angle p, in the range of 0.0 through pi\n" +
				"ACOS(p INTEGER) RETURNS RATIONAL",	
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the arc cosine of an angle, in the range of 0.0 through pi.
						return ValueRational.select(generator, Math.acos(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void asin(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("ASIN",
				"// Return the arc sine of an angle p, in the range of -pi/2 through pi/2\n" +
				"ASIN(p INTEGER) RETURNS RATIONAL",	
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						//  Returns the arc sine of an angle, in the range of -pi/2 through pi/2.
						return ValueRational.select(generator, Math.asin(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void atan(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("ATAN",
				"// Return the arc tangent of an angle p, in the range of -pi/2 through pi/2\n" +
				"ATAN(p INTEGER) RETURNS RATIONAL",	
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the arc tangent of an angle, in the range of -pi/2 through pi/2.
							return ValueRational.select(generator, Math.atan(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void atan2(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("ATAN2",
				"// Returns the angle theta from the conversion of rectangular coordinates (x, y) to polar coordinates (r, theta).\n" +
				"// The phase theta is computed as an arc tangent of y/x in the range of -pi to pi.\n" +
				"ATAN2(x RATIONAL, y RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance(), TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Converts rectangular coordinates (x, y) to polar (r, theta).
						return ValueRational.select(generator, Math.atan2(arguments[1].doubleValue(), arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void ceil(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("CEIL",
				"// Returns the smallest (closest to negative infinity) RATIONAL value that is not less than the argument\n" +
				"// and is equal to a mathematical integer.\n" +
				"CEIL(p RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the smallest (closest to negative infinity) RATIONAL value
						// that is not less than the argument and is equal to a mathematical
						// integer.
						return ValueRational.select(generator, Math.ceil(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void cos(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("COS",
				"// Returns the trigonometric cosine of an angle\n" +
				"COS(p RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the trigonometric cosine of an angle.
						return ValueRational.select(generator, Math.cos(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void exp(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("EXP",
				"// Returns Euler's number e raised to the power of a RATIONAL value.\n" +
				"EXP(p RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns Euler's number e raised to the power of a RATIONAL value.
						return ValueRational.select(generator, Math.exp(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void floor(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("FLOOR",
				"// Returns the largest (closest to positive infinity) RATIONAL value that is not greater than the argument\n" +
				"// and is equal to a mathematical integer.\n" +
				"FLOOR(p RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the largest (closest to positive infinity) RATIONAL value that
						// is not greater than the argument and is equal to a mathematical
						// integer.
							return ValueRational.select(generator, Math.floor(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void remainder(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("REMAINDER",
				"// Computes the remainder operation on two arguments as prescribed by the IEEE 754 standard.\n" +
				"REMAINDER(p RATIONAL, q RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance(), TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Computes the remainder operation on two arguments as prescribed by the IEEE 754 standard.
						return ValueRational.select(generator, Math.IEEEremainder(arguments[0].doubleValue(), arguments[1].doubleValue()));
					}
				}
			)
		);
	}

	private void log(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("LOG",
				"// Returns the natural logarithm (base e) of a RATIONAL value.\n" +
				"LOG(p RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the natural logarithm (base e) of a RATIONAL value.
						return ValueRational.select(generator, Math.log(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void maximumRational(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("MAXIMUM",
				"// Returns the greater of two RATIONAL values.\n" +
				"MAXIMUM(p RATIONAL, q RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance(), TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the greater of two RATIONAL values.
						return ValueRational.select(generator, Math.max(arguments[0].doubleValue(), arguments[1].doubleValue()));
					}
				}
			)
		);
	}

	private void maximumInteger(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("MAXIMUM",
				"// Returns the greater of two INTEGER values.\n" +
				"MAXIMUM(p INTEGER, q INTEGER) RETURNS INTEGER",
				new Type[] {TypeInteger.getInstance(), TypeInteger.getInstance()},
				TypeInteger.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the greater of two INTEGER values.
						return ValueInteger.select(generator, Math.max(arguments[0].longValue(), arguments[1].longValue()));
					}
				}
			)
		);
	}

	private void minimumRational(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("MINIMUM",
				"// Returns the smaller of two RATIONAL values.\n" +
				"MINIMUM(p RATIONAL, q RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance(), TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the smaller of two RATIONAL values.
						return ValueRational.select(generator, Math.min(arguments[0].doubleValue(), arguments[1].doubleValue()));
					}
				}
			)
		);
	}

	private void minimumInteger(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("MINIMUM",
				"// Returns the smaller of two INTEGER values.\n" +
				"MINIMUM(p INTEGER, q INTEGER) RETURNS INTEGER",
				new Type[] {TypeInteger.getInstance(), TypeInteger.getInstance()},
				TypeInteger.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the smaller of two INTEGER values.
						return ValueInteger.select(generator, Math.min(arguments[0].longValue(), arguments[1].longValue()));
					}
				}
			)
		);
	}

	private void pow(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("POW",
				"// Returns the value of the first argument raised to the power of the second argument.\n" +
				"POW(p RATIONAL, q RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance(), TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the value of the first argument raised to the power of the second argument.
						return ValueRational.select(generator, Math.pow(arguments[0].doubleValue(), arguments[1].doubleValue()));
					}
				}
			)
		);
	}

	private void random(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("RANDOM",
				"// Returns a pseudo-random RATIONAL value with a positive sign, greater than or equal to 0.0 and less than 1.0.\n" +
				"RANDOM() RETURNS RATIONAL",
				new Type[] {},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns a RATIONAL value with a positive sign, greater than or equal to 0.0 and less than 1.0.
						return ValueRational.select(generator, Math.random());
					}
				}
			)
		);
	}

	private void rint(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("RINT",
				"// Returns the RATIONAL value that is closest in value to the argument and is equal to a mathematical integer.\n" +
				"RINT(p RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the RATIONAL value that is closest in value to the argument and is equal to a mathematical integer.
						return ValueRational.select(generator, Math.rint(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void round(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("ROUND",
				"// Returns the closest INTEGER to the argument.\n" +
				"ROUND(p RATIONAL) RETURNS INTEGER",
				new Type[] {TypeRational.getInstance()},
				TypeInteger.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the closest INTEGER to the argument.
						return ValueInteger.select(generator, Math.round(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void sin(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("SIN",
				"// Returns the trigonometric sine of an angle\n" +
				"SIN(p RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the trigonometric sine of an angle.
						return ValueRational.select(generator, Math.sin(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void sqrt(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("SQRT",
				"// Returns the correctly rounded positive square root of a RATIONAL value.\n" +
				"SQRT(p RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the correctly rounded positive square root of a RATIONAL value.
						return ValueRational.select(generator, Math.sqrt(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void tan(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("TAN",
				"// Returns the trigonometric tangent of an angle\n" +
				"TAN(p RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Returns the trigonometric tangent of an angle.
						return ValueRational.select(generator, Math.tan(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void to_degrees(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("TO_DEGREES",
				"// Converts an angle measured in radians to an approximately equivalent angle measured in degrees.\n" +
				"TO_DEGREES(p RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Converts an angle measured in radians to an approximately equivalent angle measured in degrees.
						return ValueRational.select(generator, Math.toDegrees(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	private void to_radians(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("TO_RADIANS",
				"// Converts an angle measured in degrees to an approximately equivalent angle measured in radians.\n" +
				"TO_RADIANS(p RATIONAL) RETURNS RATIONAL",
				new Type[] {TypeRational.getInstance()},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Converts an angle measured in degrees to an approximately equivalent angle measured in radians.
						return ValueRational.select(generator, Math.toRadians(arguments[0].doubleValue()));
					}
				}
			)
		);
	}

	/** Maxima and minima. */

	private void max_integer(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("MAX_INTEGER",
				"// Largest positive integer\n" +
				"MAX_INTEGER() RETURNS INTEGER",
				new Type[] {},
				TypeInteger.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Largest positive integer
						return ValueInteger.select(generator, Long.MAX_VALUE);
					}
				}
			)
		);
	}

	private void min_integer(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("MIN_INTEGER",
				"// Largest negative integer\n" +
				"MIN_INTEGER() RETURNS INTEGER",
				new Type[] {},
				TypeInteger.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Largest negative integer
						return ValueInteger.select(generator, Long.MIN_VALUE);
					}
				}
			)
		);
	}

	private void max_rational(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("MAX_RATIONAL",
				"// Largest positive RATIONAL\n" +
				"MAX_RATIONAL() RETURNS RATIONAL",
				new Type[] {},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Largest positive rational
						return ValueRational.select(generator, Double.MAX_VALUE);
					}
				}
			)
		);
	}

	private void min_rational(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("MIN_RATIONAL",
				"// Largest negative RATIONAL\n" +
				"MIN_RATIONAL() RETURNS RATIONAL",
				new Type[] {},
				TypeRational.getInstance(), 
				new NativeFunction() {
					public Value evaluate(Value arguments[]) {
						// Smallest positive rational
						return ValueRational.select(generator, Double.MIN_VALUE);
					}
				}
			)
		);
	}
	
	private BuiltinOperators(RelDatabase database) {
		generator = new Generator(database, System.out);
		is_empty(database);
		countRelation(database);
		countArray(database);
		sum(database);
		avg(database);
		max(database);
		min(database);
		and(database);
		or(database);
		xor(database);
		equiv(database);
		union(database);
		xunion(database);
		d_union(database);
		intersect(database);
		exactly(database);
		cast_as_boolean(database);
		cast_as_integer(database);
		cast_as_rational(database);
		cast_as_char(database);
		equals(database);
		notequals(database);
		lessthanequals(database);
		greaterthanequals(database);
		lessthan(database);
		greaterthan(database);
		getuniquenumber(database);
		setuniquenumber(database);
		sequence(database);
		quote(database);
		unquote(database);
		is_digits(database);
		length(database);
		substring2(database);
		substring3(database);
		compare_to(database);
		compare_to_ignore_case(database);
		ends_with(database);
		equals_ignore_case(database);
		index_of2(database);
		index_of3(database);
		last_index_of2(database);
		last_index_of3(database);
		matches(database);
		region_matches(database);
		replace_all(database);
		replace_first(database);
		split2(database);
		split3(database);
		starts_with2(database);
		starts_with3(database);
		to_lower_case(database);
		to_upper_case(database);
		trim(database);
		e(database);
		pi(database);
		absRational(database);
		absInteger(database);
		acos(database);
		asin(database);
		atan(database);
		atan2(database);
		ceil(database);
		cos(database);
		exp(database);
		floor(database);
		remainder(database);
		log(database);
		maximumRational(database);
		maximumInteger(database);
		minimumRational(database);
		minimumInteger(database);
		pow(database);
		random(database);
		rint(database);
		round(database);
		sin(database);
		sqrt(database);
		tan(database);
		to_degrees(database);
		to_radians(database);
		max_integer(database);
		min_integer(database);
		max_rational(database);
		min_rational(database);
	}
	
	public static void buildOperators(RelDatabase database) {
		new BuiltinOperators(database);
	}
	
}
