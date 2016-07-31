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
					new Type[] {new TypeArray(new TypeTuple(new Heading()))}, 
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
				new OperatorDefinitionNativeFunction("SUM_INTEGER", 
						new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
						TypeInteger.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								ValueRelation relation = (ValueRelation)arguments[0];
								int attributeIndex = (int)arguments[1].longValue();
								return relation.sumInteger(attributeIndex);
							}
						}
				)
			);
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("SUM_RATIONAL", 
						new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
						TypeRational.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								ValueRelation relation = (ValueRelation)arguments[0];
								int attributeIndex = (int)arguments[1].longValue();
								return relation.sumRational(attributeIndex);
							}
						}
				)
			);
	}
	
	private void avg(RelDatabase database) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("AVG_INTEGER", 
						new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
						TypeRational.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								ValueRelation relation = (ValueRelation)arguments[0];
								int attributeIndex = (int)arguments[1].longValue();
								return relation.avgInteger(attributeIndex);
							}
						}
				)
			);
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("AVG_RATIONAL", 
						new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
						TypeRational.getInstance(), 
						new NativeFunction() {
							public Value evaluate(Value arguments[]) {
								ValueRelation relation = (ValueRelation)arguments[0];
								int attributeIndex = (int)arguments[1].longValue();
								return relation.avgRational(attributeIndex);
							}
						}
				)
			);
	}

	private void max(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("MAX", 
					new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
					TypeRational.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueRelation relation = (ValueRelation)arguments[0];
							int attributeIndex = (int)arguments[1].longValue();
							return relation.max(attributeIndex);
						}
					}
			)
		);
	}
	
	private void min(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("MIN", 
					new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
					TypeRational.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueRelation relation = (ValueRelation)arguments[0];
							int attributeIndex = (int)arguments[1].longValue();
							return relation.min(attributeIndex);
						}
					}
			)
		);
	}
	
	private void and(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("AND", 
					new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueRelation relation = (ValueRelation)arguments[0];
							int attributeIndex = (int)arguments[1].longValue();
							return relation.and(attributeIndex);
						}
					}
			)
		);
	}
	
	private void or(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("OR", 
					new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueRelation relation = (ValueRelation)arguments[0];
							int attributeIndex = (int)arguments[1].longValue();
							return relation.or(attributeIndex);
						}
					}
			)
		);
	}
	
	private void xor(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("XOR", 
					new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
					TypeBoolean.getInstance(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueRelation relation = (ValueRelation)arguments[0];
							int attributeIndex = (int)arguments[1].longValue();
							return relation.xor(attributeIndex);
						}
					}
			)
		);
	}
	
	private void union(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("UNION", 
					new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
					TypeRelation.getEmptyRelationType(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueRelation relation = (ValueRelation)arguments[0];
							int attributeIndex = (int)arguments[1].longValue();
							return relation.union(attributeIndex);
						}
					}
			)
		);
	}
	
	private void xunion(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("XUNION", 
					new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
					TypeRelation.getEmptyRelationType(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueRelation relation = (ValueRelation)arguments[0];
							int attributeIndex = (int)arguments[1].longValue();
							return relation.xunion(attributeIndex);
						}
					}
			)
		);
	}
	
	private void d_union(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("D_UNION", 
					new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
					TypeRelation.getEmptyRelationType(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueRelation relation = (ValueRelation)arguments[0];
							int attributeIndex = (int)arguments[1].longValue();
							return relation.d_union(attributeIndex);
						}
					}
			)
		);
	}
	
	private void intersect(RelDatabase database) {
		database.defineBuiltinOperator(
			new OperatorDefinitionNativeFunction("INTERSECT", 
					new Type[] {TypeRelation.getEmptyRelationType(), TypeInteger.getInstance()}, 
					TypeRelation.getEmptyRelationType(), 
					new NativeFunction() {
						public Value evaluate(Value arguments[]) {
							ValueRelation relation = (ValueRelation)arguments[0];
							int attributeIndex = (int)arguments[1].longValue();
							return relation.intersect(attributeIndex);
						}
					}
			)
		);
	}
	
	private void exactly(RelDatabase database) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("EXACTLY", 
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
	
	private void equals(RelDatabase database, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.EQUALS, 
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
		equals(database, new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		equals(database, new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		equals(database, new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}
	
	private void notequals(RelDatabase database, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.NOTEQUALS, 
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
		notequals(database, new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		notequals(database, new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		notequals(database, new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}
	
	private void greaterthanequals(RelDatabase database, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.GREATERTHANOREQUALS, 
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
		
	private void superset(RelDatabase database, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.GREATERTHANOREQUALS, 
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
		superset(database, new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		greaterthanequals(database, new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		greaterthanequals(database, new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}
	
	private void lessthanequals(RelDatabase database, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.LESSTHANOREQUALS, 
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
	
	private void subset(RelDatabase database, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.LESSTHANOREQUALS, 
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
		subset(database, new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		lessthanequals(database, new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		lessthanequals(database, new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}
	
	private void greaterthan(RelDatabase database, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.GREATERTHAN, 
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
	
	private void propersuperset(RelDatabase database, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.GREATERTHAN, 
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
		propersuperset(database, new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		greaterthan(database, new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		greaterthan(database, new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}
	
	private void lessthan(RelDatabase database, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.LESSTHAN, 
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
	
	private void propersubset(RelDatabase database, Type[] parameters) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction(BuiltinTypeBuilder.LESSTHAN, 
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
		propersubset(database, new Type[] {TypeRelation.getEmptyRelationType(), TypeRelation.getEmptyRelationType()});
		lessthan(database, new Type[] {TypeTuple.getEmptyTupleType(), TypeTuple.getEmptyTupleType()});
		lessthan(database, new Type[] {TypeAlpha.getEmptyAlphaType(), TypeAlpha.getEmptyAlphaType()});
	}

	private void getuniquenumber(final RelDatabase database) {
		database.defineBuiltinOperator(
				new OperatorDefinitionNativeFunction("GET_UNIQUE_NUMBER", 
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
	}
	
	public static void buildOperators(RelDatabase database) {
		new BuiltinOperators(database);
	}
	
}
