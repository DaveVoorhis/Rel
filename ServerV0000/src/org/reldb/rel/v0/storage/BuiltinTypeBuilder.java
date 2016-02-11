package org.reldb.rel.v0.storage;

import java.util.LinkedList;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.generator.OperatorSignature;
import org.reldb.rel.v0.generator.References;
import org.reldb.rel.v0.interpreter.Interpreter;
import org.reldb.rel.v0.storage.catalog.Catalog;
import org.reldb.rel.v0.storage.catalog.RelvarTypes;
import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.types.TypeAlpha;
import org.reldb.rel.v0.types.builtin.TypeBoolean;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;
import org.reldb.rel.v0.types.builtin.TypeRational;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.Value;
import org.reldb.rel.v0.values.ValueBoolean;
import org.reldb.rel.v0.values.ValueCharacter;
import org.reldb.rel.v0.values.ValueInteger;
import org.reldb.rel.v0.values.ValueRational;
import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.values.ValueTuple;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.NativeFunction;
import org.reldb.rel.v0.vm.VirtualMachine;

public class BuiltinTypeBuilder {
	public final static String EQUALS = "OP_EQUALS";
	public final static String NOTEQUALS = "OP_NOTEQUALS";
	public final static String GREATERTHAN = "OP_GREATERTHAN";
	public final static String LESSTHAN = "OP_LESSTHAN";
	public final static String GREATERTHANOREQUALS = "OP_GREATERTHANOREQUALS";
	public final static String LESSTHANOREQUALS = "OP_LESSTHANOREQUALS";
	public final static String MAX = "OP_MAX";
	public final static String MIN = "OP_MIN";
	public final static String XOR = "OP_XOR";
	public final static String OR = "OP_OR";
	public final static String AND = "OP_AND";
	public final static String NOT = "OP_NOT";
	public final static String PLUS = "OP_PLUS";
	public final static String MINUS = "OP_MINUS";
	public final static String TIMES = "OP_TIMES";
	public final static String DIVIDE = "OP_DIVIDE";
	public final static String MODULO = "OP_MODULO";
	public final static String UNARY_PLUS = "OP_UNARY_PLUS";
	public final static String UNARY_MINUS = "OP_UNARY_MINUS";

	private static LinkedList<String> subTypes = new LinkedList<String>();
	
	private RelDatabase database;
	private Generator generator;
	private TypeAlpha type;
	private boolean typeExists;

	private static void loadSubtypes(RelDatabase database) {
		Interpreter interpreter = new Interpreter(database, System.out);
		Generator generator = interpreter.getGenerator();
		for (String typeName: subTypes)
    		database.loadType(generator, typeName);
	}
	
	public BuiltinTypeBuilder(RelDatabase database, TypeAlpha type) {
		this.database = database;
		this.type = type;
		typeExists = false;
		Interpreter interpreter = new Interpreter(database, System.out);
		generator = interpreter.getGenerator();
		generator.beginCompilation();
    	String typeName = type.getTypeName();
		if (database.isTypeExists(generator, typeName)) {
			generator.beginTypeRetrieval();
			generator.createTypeBuiltin(type);
			generator.endTypeRetrieval();
	    	RelvarTypes typesRelvar = (RelvarTypes)database.openGlobalRelvar(Catalog.relvarTypes);
	    	ValueTuple typeTuple = typesRelvar.getTupleForKey(generator, typeName);
	    	ValueRelation subTypeNames = (ValueRelation)typeTuple.getValues()[5];
	    	for (TupleIterator it = subTypeNames.iterator(); it.hasNext();)
	    		subTypes.add(it.next().getValues()[0].stringValue());
	    	typeExists = true;
		} else {
			generator.createTypeBuiltin(type);
		}
	}
	
	public void checkTypeOperatorUnary(String name, TypeAlpha returnType, final NativeFunction operator) {
		String typeName = type.getTypeName();
		References referenceToType = new References();
		referenceToType.addReferenceToType(type.getTypeName());
		OperatorSignature signature = new OperatorSignature(name);
		signature.addParameter("%p0", type);
		signature.setReturnType(returnType);
		if (typeExists)
			generator.beginTypeRetrieval();
		generator.new OperatorAssociatedWithType(typeName, "Rel", signature, referenceToType) {
			public Value evaluate(Value[] arguments) {
				return operator.evaluate(arguments);
			}
		};
		if (typeExists)
			generator.endTypeRetrieval();
	}
	
	public void checkTypeOperatorBinary(String name, Type returnType, final NativeFunction operator) {
		String typeName = type.getTypeName();
		References referenceToType = new References();
		referenceToType.addReferenceToType(type.getTypeName());
		OperatorSignature signature = new OperatorSignature(name);
		signature.addParameter("%p0", type);
		signature.addParameter("%p1", type);
		signature.setReturnType(returnType);
		if (typeExists)
			generator.beginTypeRetrieval();
		generator.new OperatorAssociatedWithType(typeName, "Rel", signature, referenceToType) {
			public Value evaluate(Value[] arguments) {
				return operator.evaluate(arguments);
			}
		};
		if (typeExists)
			generator.endTypeRetrieval();
	}
    
    public void go() {
		generator.endCompilation().call(new Context(generator, new VirtualMachine(generator, database, System.out)));        	
    }

	public static void buildTypes(RelDatabase relDatabase) {
		final BuiltinTypeBuilder typeBuilderBoolean = new BuiltinTypeBuilder(relDatabase, TypeBoolean.getInstance());
    	final BuiltinTypeBuilder typeBuilderInteger = new BuiltinTypeBuilder(relDatabase, TypeInteger.getInstance());    	
    	final BuiltinTypeBuilder typeBuilderRational = new BuiltinTypeBuilder(relDatabase, TypeRational.getInstance());    	
    	final BuiltinTypeBuilder typeBuilderCharacter = new BuiltinTypeBuilder(relDatabase, TypeCharacter.getInstance());    	
		
    	typeBuilderBoolean.checkTypeOperatorBinary(EQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderBoolean.generator, arguments[0].booleanValue() == arguments[1].booleanValue());
			}
    	});
    	typeBuilderBoolean.checkTypeOperatorBinary(NOTEQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderBoolean.generator, arguments[0].booleanValue() != arguments[1].booleanValue());
			}
    	});    	
    	typeBuilderBoolean.checkTypeOperatorBinary(XOR, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderBoolean.generator, arguments[0].booleanValue() ^ arguments[1].booleanValue());
			}
    	});
    	typeBuilderBoolean.checkTypeOperatorBinary(OR, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderBoolean.generator, arguments[0].booleanValue() | arguments[1].booleanValue());
			}
    	});
    	typeBuilderBoolean.checkTypeOperatorBinary(AND, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderBoolean.generator, arguments[0].booleanValue() & arguments[1].booleanValue());
			}
    	});
    	typeBuilderBoolean.checkTypeOperatorUnary(NOT, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderBoolean.generator, !arguments[0].booleanValue());
			}
    	});

    	typeBuilderInteger.checkTypeOperatorBinary(EQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderInteger.generator, arguments[0].longValue() == arguments[1].longValue());
			}
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(NOTEQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderInteger.generator, arguments[0].longValue() != arguments[1].longValue());
			}
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(GREATERTHANOREQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderInteger.generator, arguments[0].longValue() >= arguments[1].longValue());
			}
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(LESSTHANOREQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderInteger.generator, arguments[0].longValue() <= arguments[1].longValue());
			}
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(LESSTHAN, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderInteger.generator, arguments[0].longValue() < arguments[1].longValue());
			}
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(GREATERTHAN, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderInteger.generator, arguments[0].longValue() > arguments[1].longValue());
			}
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(MAX, TypeInteger.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				if (arguments[0].longValue() > arguments[1].longValue())
					return ValueInteger.select(typeBuilderInteger.generator, arguments[0].longValue());
				else
					return ValueInteger.select(typeBuilderInteger.generator, arguments[1].longValue());
			}	    		
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(MIN, TypeInteger.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				if (arguments[0].longValue() < arguments[1].longValue())
					return ValueInteger.select(typeBuilderInteger.generator, arguments[0].longValue());
				else
					return ValueInteger.select(typeBuilderInteger.generator, arguments[1].longValue());
			}	    		
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(PLUS, TypeInteger.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueInteger.select(typeBuilderInteger.generator, arguments[0].longValue() + arguments[1].longValue());
			}
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(MINUS, TypeInteger.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueInteger.select(typeBuilderInteger.generator, arguments[0].longValue() - arguments[1].longValue());
			}
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(TIMES, TypeInteger.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueInteger.select(typeBuilderInteger.generator, arguments[0].longValue() * arguments[1].longValue());
			}
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(DIVIDE, TypeInteger.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				if (arguments[1].longValue() == 0)
					throw new ExceptionSemantic("RS0207: Attempt to divide by zero.");
				return ValueInteger.select(typeBuilderInteger.generator, arguments[0].longValue() / arguments[1].longValue());
			}
    	});
    	typeBuilderInteger.checkTypeOperatorBinary(MODULO, TypeInteger.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				if (arguments[1].longValue() == 0)
					throw new ExceptionSemantic("RS0419: Attempt to perform modulo by zero.");
				return ValueInteger.select(typeBuilderInteger.generator, arguments[0].longValue() % arguments[1].longValue());
			}
    	});
    	typeBuilderInteger.checkTypeOperatorUnary(UNARY_PLUS, TypeInteger.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueInteger.select(typeBuilderInteger.generator, +arguments[0].longValue());
			}	    		
    	});
    	typeBuilderInteger.checkTypeOperatorUnary(UNARY_MINUS, TypeInteger.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueInteger.select(typeBuilderInteger.generator, -arguments[0].longValue());
			}
    	});

    	typeBuilderRational.checkTypeOperatorBinary(EQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderRational.generator, arguments[0].doubleValue() == arguments[1].doubleValue());
			}
    	});
    	typeBuilderRational.checkTypeOperatorBinary(NOTEQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderRational.generator, arguments[0].doubleValue() != arguments[1].doubleValue());
			}
    	});
    	typeBuilderRational.checkTypeOperatorBinary(GREATERTHANOREQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderRational.generator, arguments[0].doubleValue() >= arguments[1].doubleValue());
			}
    	});
    	typeBuilderRational.checkTypeOperatorBinary(LESSTHANOREQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderRational.generator, arguments[0].doubleValue() <= arguments[1].doubleValue());
			}
    	});
    	typeBuilderRational.checkTypeOperatorBinary(LESSTHAN, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderRational.generator, arguments[0].doubleValue() < arguments[1].doubleValue());
			}
    	});
    	typeBuilderRational.checkTypeOperatorBinary(GREATERTHAN, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderRational.generator, arguments[0].doubleValue() > arguments[1].doubleValue());
			}
    	});
    	typeBuilderRational.checkTypeOperatorBinary(MAX, TypeRational.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				if (arguments[0].doubleValue() > arguments[1].doubleValue())
					return ValueRational.select(typeBuilderRational.generator, arguments[0].doubleValue());
				else
					return ValueRational.select(typeBuilderRational.generator, arguments[1].doubleValue());
			}	    		
    	});
    	typeBuilderRational.checkTypeOperatorBinary(MIN, TypeRational.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				if (arguments[0].doubleValue() < arguments[1].doubleValue())
					return ValueRational.select(typeBuilderRational.generator, arguments[0].doubleValue());
				else
					return ValueRational.select(typeBuilderRational.generator, arguments[1].doubleValue());
			}	    		
    	});
    	typeBuilderRational.checkTypeOperatorBinary(PLUS, TypeRational.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueRational.select(typeBuilderRational.generator, arguments[0].doubleValue() + arguments[1].doubleValue());
			}
    	});
    	typeBuilderRational.checkTypeOperatorBinary(MINUS, TypeRational.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueRational.select(typeBuilderRational.generator, arguments[0].doubleValue() - arguments[1].doubleValue());
			}
    	});
    	typeBuilderRational.checkTypeOperatorBinary(TIMES, TypeRational.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueRational.select(typeBuilderRational.generator, arguments[0].doubleValue() * arguments[1].doubleValue());
			}
    	});
    	typeBuilderRational.checkTypeOperatorBinary(DIVIDE, TypeRational.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueRational.select(typeBuilderRational.generator, arguments[0].doubleValue() / arguments[1].doubleValue());
			}
    	});
    	typeBuilderRational.checkTypeOperatorUnary(UNARY_PLUS, TypeRational.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueRational.select(typeBuilderRational.generator, +arguments[0].doubleValue());
			}	    		
    	});
    	typeBuilderRational.checkTypeOperatorUnary(UNARY_MINUS, TypeRational.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueRational.select(typeBuilderRational.generator, -arguments[0].doubleValue());
			}
    	});

    	typeBuilderCharacter.checkTypeOperatorBinary(EQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderCharacter.generator, arguments[0].stringValue().compareTo(arguments[1].stringValue())==0);
			}
    	});
    	typeBuilderCharacter.checkTypeOperatorBinary(NOTEQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderCharacter.generator, arguments[0].stringValue().compareTo(arguments[1].stringValue())!=0);
			}
    	});
    	typeBuilderCharacter.checkTypeOperatorBinary(GREATERTHANOREQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderCharacter.generator, arguments[0].stringValue().compareTo(arguments[1].stringValue())>=0);
			}
    	});
    	typeBuilderCharacter.checkTypeOperatorBinary(LESSTHANOREQUALS, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderCharacter.generator, arguments[0].stringValue().compareTo(arguments[1].stringValue())<=0);
			}
    	});
    	typeBuilderCharacter.checkTypeOperatorBinary(LESSTHAN, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderCharacter.generator, arguments[0].stringValue().compareTo(arguments[1].stringValue())<0);
			}
    	});
    	typeBuilderCharacter.checkTypeOperatorBinary(GREATERTHAN, TypeBoolean.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				return ValueBoolean.select(typeBuilderCharacter.generator, arguments[0].stringValue().compareTo(arguments[1].stringValue())>0);
			}
    	});
    	typeBuilderCharacter.checkTypeOperatorBinary(MAX, TypeCharacter.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				if (arguments[0].stringValue().compareTo(arguments[1].stringValue())>0)
					return ValueCharacter.select(typeBuilderCharacter.generator, arguments[0].stringValue());
				else
					return ValueCharacter.select(typeBuilderCharacter.generator, arguments[1].stringValue());
			}	    		
    	});
    	typeBuilderCharacter.checkTypeOperatorBinary(MIN, TypeCharacter.getInstance(), new NativeFunction() {
			public Value evaluate(Value[] arguments) {
				if (arguments[0].stringValue().compareTo(arguments[1].stringValue())<0)
					return ValueCharacter.select(typeBuilderCharacter.generator, arguments[0].stringValue());
				else
					return ValueCharacter.select(typeBuilderCharacter.generator, arguments[1].stringValue());
			}	    		
    	});
    	
    	typeBuilderBoolean.go();
    	typeBuilderInteger.go();
    	typeBuilderRational.go();
    	typeBuilderCharacter.go();
    	
    	loadSubtypes(relDatabase);
	}	
	
}