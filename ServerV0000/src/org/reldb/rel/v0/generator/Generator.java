package org.reldb.rel.v0.generator;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.debuginfo.DebugInfo;
import org.reldb.rel.v0.external.ForeignCompilerJava;
import org.reldb.rel.v0.interpreter.TutorialDParser;
import org.reldb.rel.v0.languages.tutoriald.BaseASTNode;
import org.reldb.rel.v0.languages.tutoriald.parser.Token;
import org.reldb.rel.v0.storage.BuiltinTypeBuilder;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.Relvar;
import org.reldb.rel.v0.storage.relvars.RelvarCustomMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarDefinition;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.relvars.RelvarInProgress;
import org.reldb.rel.v0.storage.relvars.RelvarMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarPrivate;
import org.reldb.rel.v0.storage.relvars.RelvarRealMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarVirtualMetadata;
import org.reldb.rel.v0.storage.tables.TableExternal.DuplicateHandling;
import org.reldb.rel.v0.types.Attribute;
import org.reldb.rel.v0.types.AttributeMap;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.JoinMap;
import org.reldb.rel.v0.types.OrderMap;
import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.types.TypeAlpha;
import org.reldb.rel.v0.types.TypeArray;
import org.reldb.rel.v0.types.TypeHeading;
import org.reldb.rel.v0.types.TypeOperator;
import org.reldb.rel.v0.types.TypeRelation;
import org.reldb.rel.v0.types.TypeTuple;
import org.reldb.rel.v0.types.builtin.TypeBoolean;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;
import org.reldb.rel.v0.types.userdefined.DerivedPossrep;
import org.reldb.rel.v0.types.userdefined.Possrep;
import org.reldb.rel.v0.types.userdefined.PossrepComponent;
import org.reldb.rel.v0.values.Value;
import org.reldb.rel.v0.values.ValueAlpha;
import org.reldb.rel.v0.values.ValueBoolean;
import org.reldb.rel.v0.values.ValueCharacter;
import org.reldb.rel.v0.values.ValueInteger;
import org.reldb.rel.v0.values.ValueRational;
import org.reldb.rel.v0.values.ValueRelationLiteral;
import org.reldb.rel.v0.values.ValueTuple;
import org.reldb.rel.v0.version.Version;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.NativeFunction;
import org.reldb.rel.v0.vm.Operator;
import org.reldb.rel.v0.vm.instructions.array.OpArrayAppend;
import org.reldb.rel.v0.vm.instructions.array.OpArrayFor;
import org.reldb.rel.v0.vm.instructions.array.OpArrayGet;
import org.reldb.rel.v0.vm.instructions.array.OpArrayProject;
import org.reldb.rel.v0.vm.instructions.array.OpArraySet;
import org.reldb.rel.v0.vm.instructions.array.OpArrayToArray;
import org.reldb.rel.v0.vm.instructions.array.OpArrayToRelation;
import org.reldb.rel.v0.vm.instructions.core.OpAdd;
import org.reldb.rel.v0.vm.instructions.core.OpAverage;
import org.reldb.rel.v0.vm.instructions.core.OpBranchIfFalse;
import org.reldb.rel.v0.vm.instructions.core.OpConcatenate;
import org.reldb.rel.v0.vm.instructions.core.OpDuplicate;
import org.reldb.rel.v0.vm.instructions.core.OpDuplicateUnder;
import org.reldb.rel.v0.vm.instructions.core.OpExactly;
import org.reldb.rel.v0.vm.instructions.core.OpGetTemporarilyUniqueInteger;
import org.reldb.rel.v0.vm.instructions.core.OpInvokeAnonymousEvaluate;
import org.reldb.rel.v0.vm.instructions.core.OpInvokeDynamicCall;
import org.reldb.rel.v0.vm.instructions.core.OpInvokeDynamicEvaluate;
import org.reldb.rel.v0.vm.instructions.core.OpJump;
import org.reldb.rel.v0.vm.instructions.core.OpOutput;
import org.reldb.rel.v0.vm.instructions.core.OpPop;
import org.reldb.rel.v0.vm.instructions.core.OpPreserveContextInValueOperator;
import org.reldb.rel.v0.vm.instructions.core.OpPushLiteral;
import org.reldb.rel.v0.vm.instructions.core.OpReturn;
import org.reldb.rel.v0.vm.instructions.core.OpReturnValue;
import org.reldb.rel.v0.vm.instructions.core.OpSwap;
import org.reldb.rel.v0.vm.instructions.core.OpWrite;
import org.reldb.rel.v0.vm.instructions.core.OpWriteRaw;
import org.reldb.rel.v0.vm.instructions.core.OpWriteln;
import org.reldb.rel.v0.vm.instructions.core.OpWritelnNoExpression;
import org.reldb.rel.v0.vm.instructions.ddl.OpAlterVarRealAlterKey;
import org.reldb.rel.v0.vm.instructions.ddl.OpAlterVarRealChangeAttributeType;
import org.reldb.rel.v0.vm.instructions.ddl.OpAlterVarRealDropAttribute;
import org.reldb.rel.v0.vm.instructions.ddl.OpAlterVarRealInsertAttributes;
import org.reldb.rel.v0.vm.instructions.ddl.OpAlterVarRealRenameAttribute;
import org.reldb.rel.v0.vm.instructions.ddl.OpCreateConstraint;
import org.reldb.rel.v0.vm.instructions.ddl.OpCreateExternalRelvar;
import org.reldb.rel.v0.vm.instructions.ddl.OpCreateOperator;
import org.reldb.rel.v0.vm.instructions.ddl.OpCreateRealRelvar;
import org.reldb.rel.v0.vm.instructions.ddl.OpCreateType;
import org.reldb.rel.v0.vm.instructions.ddl.OpCreateVirtualRelvar;
import org.reldb.rel.v0.vm.instructions.ddl.OpDropConstraint;
import org.reldb.rel.v0.vm.instructions.ddl.OpDropOperator;
import org.reldb.rel.v0.vm.instructions.ddl.OpDropRelvar;
import org.reldb.rel.v0.vm.instructions.ddl.OpDropType;
import org.reldb.rel.v0.vm.instructions.possrep.OpPossrepGetComponent;
import org.reldb.rel.v0.vm.instructions.possrep.OpPossrepSetComponent;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationDUnion;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationGetTuple;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationGroup;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationIMinus;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationIntersect;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationJoin;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationLiteralInsertTuple;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationMinus;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationProduct;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationPushLiteral;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationTClose;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationUngroup;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationUnion;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationWhere;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationWrite;
import org.reldb.rel.v0.vm.instructions.relation.OpRelationXunion;
import org.reldb.rel.v0.vm.instructions.relation.OpTupleInRelation;
import org.reldb.rel.v0.vm.instructions.relvar.OpRelvarDeleteGivenExpression;
import org.reldb.rel.v0.vm.instructions.relvar.OpRelvarDeleteWhere;
import org.reldb.rel.v0.vm.instructions.relvar.OpRelvarGlobalGet;
import org.reldb.rel.v0.vm.instructions.relvar.OpRelvarIDelete;
import org.reldb.rel.v0.vm.instructions.relvar.OpRelvarInsert;
import org.reldb.rel.v0.vm.instructions.relvar.OpRelvarInsertNoDuplicates;
import org.reldb.rel.v0.vm.instructions.relvar.OpRelvarPurge;
import org.reldb.rel.v0.vm.instructions.relvar.OpRelvarUpdate;
import org.reldb.rel.v0.vm.instructions.relvar.OpRelvarUpdateWhere;
import org.reldb.rel.v0.vm.instructions.system.OpBackup;
import org.reldb.rel.v0.vm.instructions.system.OpCheckConstraintsAndCommitOrRollback;
import org.reldb.rel.v0.vm.instructions.system.OpExecute;
import org.reldb.rel.v0.vm.instructions.system.OpTransactionBegin;
import org.reldb.rel.v0.vm.instructions.system.OpTransactionCommit;
import org.reldb.rel.v0.vm.instructions.system.OpTransactionRollback;
import org.reldb.rel.v0.vm.instructions.tuple.OpTupleGetAttribute;
import org.reldb.rel.v0.vm.instructions.tuple.OpTupleJoin;
import org.reldb.rel.v0.vm.instructions.tuple.OpTupleJoinDisjoint;
import org.reldb.rel.v0.vm.instructions.tuple.OpTupleProject;
import org.reldb.rel.v0.vm.instructions.tuple.OpTuplePushLiteral;
import org.reldb.rel.v0.vm.instructions.tuple.OpTupleSetAttribute;
import org.reldb.rel.v0.vm.instructions.tupleIteratable.OpTupleIteratableProject;
import org.reldb.rel.v0.vm.instructions.tupleIteratable.OpTupleIteratableOrder;
import org.reldb.rel.v0.vm.instructions.tupleIteratable.OpTupleIteratableMap;

/** Code generator. */
public class Generator {

	// Temporary kludge to obtain a relvar owner
	private String userRelvarOwner = "User";
	
	private boolean compiling = true;
	private boolean persistentOnly = false;
	
	// The database upon which we're running.
	private RelDatabase database;
	
	// The parser being used to build code.
	private TutorialDParser parser;
	
	// Reference to current operator definition.
	private OperatorDefinition currentOperatorDefinition;
	
	// Depth of _main (interactive) operator.
	private int interactiveOperatorNestingDepth;

	// Depth of nested assignments
	private int assignmentNestingLevel = 0;

	// Depth of nested compiling on/off actuations.
	private int compilingOffNestingLevel = 0;
	
	// Depth of persistent-only reference on/off actuations.
	private int persistentOnlyNestingLevel = 0;
	
	// Output stream
	private PrintStream printStream;
	
	// When set, collects references to global relvars, operators, and user-defined types.
	// Used to prevent DROPping VIRTUAL relvars, operators, constraints, and types that use these.
	private References globalReferenceCollector = null;

	// New relvars at compile-time.
    private HashMap<String, RelvarDefinition> relvarsInProgress = new HashMap<String, RelvarDefinition>();
	
    // New types at compile-time.
    private HashMap<String, Type> typesInProgress = new HashMap<String, Type>();

	// True if verbose external operator/type generation is enabled
	private boolean verboseExternalOperatorTypeGeneration = false;

	// True if verbose reporting of relvar updates is enabled
	private boolean verboseRelvarUpdates = true;
	
	public Generator(RelDatabase database, PrintStream outputStream) {
		this.database = database;
		this.parser = null;
		this.printStream = outputStream;
		initialise();
	}
	
	private void initialise() {
		compiling = true;
		persistentOnly = false;
		assignmentNestingLevel = 0;
		compilingOffNestingLevel = 0;
		persistentOnlyNestingLevel = 0;
		currentOperatorDefinition = null;
		globalReferenceCollector = null;
		// _root definition does not execute but holds built-in operator definitions
		operatorDefinition("Root Operator Context");
		// make sure it's "special", so we don't really see it
		currentOperatorDefinition.setSpecial(true);
		interactiveOperatorNestingDepth = currentOperatorDefinition.getDepth();		
	}
	
	/** Reset the generator after encountering an error. */
	public void reset() {		
		database.rollbackTransactionIfThereIsOne();
    	relvarsInProgress.clear();
    	typesInProgress.clear();
		initialise();
	}
	
	public RelDatabase getDatabase() {
		return database;
	}
	
	public void setParser(TutorialDParser parser) {
		this.parser = parser;
	}
	
	public PrintStream getPrintStream() {
		return printStream;
	}

	public void setOwner(String owner) {
		userRelvarOwner = owner;
	}

	public void beginCompilation() {
		// Begin main operator definition
		operatorDefinition("Interactive Session");
		// make sure it's "special", so we don't really see it
		currentOperatorDefinition.setSpecial(true);
		// Obtain depth of "Interactive Session"
		interactiveOperatorNestingDepth = currentOperatorDefinition.getDepth();
	}
	
	public OperatorDefinition endCompilation() {
		// Capture reference to main operator definition
		OperatorDefinition mainOperatorDefinition = currentOperatorDefinition;
		// End main operator definition
		endOperator();
		// Return main operator definition
		return mainOperatorDefinition;
	}

	public void setVerboseExternalCompilation(boolean b) {
		verboseExternalOperatorTypeGeneration = b;
	}

	public void setVerboseRelvarUpdates(boolean b) {
		verboseRelvarUpdates = b;
	}

	public boolean isVerboseRelvarUpdates() {
		return verboseRelvarUpdates;
	}

	/** Turn code generation on or off.  Useful for doing type checking without generating code. */
	public void setCompilingOn() {
		if (--compilingOffNestingLevel == 0)
			compiling = true;
	}
	
	/** Turn code generation off. */
	public void setCompilingOff() {
		if (compilingOffNestingLevel++ == 0)
			compiling = false;
	}
	
	private DebugInfo getDebugInfo() {
		if (parser != null)
			return new DebugInfo(parser.getCurrentNode(), getOperatorDefinitionLineReferenceStack(parser.getCurrentNode().first_token.beginLine));
		return new DebugInfo("unknown location");
	}
	
	public void compileInstruction(Instruction instruction) {
		if (compiling)
			currentOperatorDefinition.compile(getDebugInfo(), instruction);
	}
	
	public void compileInstructionAt(Instruction instruction, int address) {
		if (compiling)
			currentOperatorDefinition.compileAt(getDebugInfo(), instruction, address);
	}
	
	private void compileCheckConstraintsAndCommitOrRollback() {
		compileInstruction(new OpCheckConstraintsAndCommitOrRollback());
	}
	
	/** Invoked prior to a set of possibly-nested assignment statements. */
	private void beginAssignments() {
		compileTransactionBegin();
	}
	
	/** Invoked after a set of possibly-nested assignment statements. */
	private void endAssignments() {
		compileCheckConstraintsAndCommitOrRollback();
	}
	
	/** Invoked prior to one or more assignment statements.  May be nested. */
	public void beginAssignment() {
		if (assignmentNestingLevel++ == 0)
			beginAssignments();
	}
	
	/** Invoked after one or more assignment statements.  May be nested. */
	public void endAssignment() {
		if (--assignmentNestingLevel == 0)
			endAssignments();
	}
    
	public void defineVariable(String varname, Type type) {
		currentOperatorDefinition.defineVariable(varname, type);
		compileVariableInitialise(varname);
	}
	
	public void defineConstant(String constname, Type type) {
		currentOperatorDefinition.defineConstant(constname, type);
		compileVariableInitialise(constname);
	}

	public void defineRelvarReal(String varname, RelvarHeading keydef, References references) {
		if (currentOperatorDefinition.getDepth() > interactiveOperatorNestingDepth)
			throw new ExceptionSemantic("RS0018: REAL relation-valued variables may not be defined inside a user-defined operator.");
		if (relvarsInProgress.containsKey(varname) || database.isRelvarExists(varname))
			throw new ExceptionSemantic("RS0019: " + varname + " already exists.");
		RelvarDefinition relvar = new RelvarDefinition(varname, new RelvarRealMetadata(database, keydef, userRelvarOwner), references);
		relvarsInProgress.put(varname, relvar);
		beginAssignment();
		compileInstruction(new OpCreateRealRelvar(relvarsInProgress, relvar));
		compileVariableInitialise(varname);
		endAssignment();
	}

	public void defineRelvarPublic(String varname, RelvarHeading expectedKeydef) {
		RelvarMetadata metadata = database.getRelvarMetadata(varname);
		if (metadata == null)
			throw new ExceptionSemantic("RS0020: Relation-valued variable '" + varname + "' does not exist.");
		TypeRelation actualRelvarType = new TypeRelation(metadata.getHeadingDefinition(database).getHeading());
		// TODO - type check can be less restrictive; if heading of actualRelvarType is a subset of expectedType it should be acceptable
		if (!expectedKeydef.getHeading().canAccept(actualRelvarType.getHeading()))
			throw new ExceptionSemantic("RS0021: Expected relation-valued variable '" + varname + "' to be " + new TypeRelation(expectedKeydef.getHeading()) + " but got " + actualRelvarType);
		// TODO - check that expectedKeydef is a subkey of metadata.getKeyDefinition() here... 
		// Force run-time check of relvar by accessing it
		compileInstruction(new OpRelvarGlobalGet(varname, expectedKeydef));
		compileInstruction(new OpPop());
	}
	
	public void defineRelvarPrivate(String varname, RelvarHeading keydef) {
		currentOperatorDefinition.defineRelvarPrivate(database, varname, keydef);	
		compileVariableInitialise(varname);
	}

	public void defineRelvarVirtual(String varname, String sourceCode, RelvarHeading keydef, References references) {
		if (currentOperatorDefinition.getDepth() > interactiveOperatorNestingDepth)
			throw new ExceptionSemantic("RS0021: VIRTUAL relation-valued variables may not be defined inside a user-defined operator.");
		if (relvarsInProgress.containsKey(varname) || database.isRelvarExists(varname))
			throw new ExceptionSemantic("RS0022: " + varname + " already exists.");
		RelvarDefinition relvar = new RelvarDefinition(varname, new RelvarVirtualMetadata(database, sourceCode, keydef, userRelvarOwner), references);
		relvarsInProgress.put(varname, relvar);
		beginAssignment();
		compileInstruction(new OpCreateVirtualRelvar(relvarsInProgress, relvar));
		endAssignment();
	}

	public void defineRelvarExternal(String varname, String externalRelvarType, String externalRelvarSpecification, String duplicates) {
		DuplicateHandling handler = DuplicateHandling.AUTOKEY;
		if (duplicates.compareToIgnoreCase("DUP_REMOVE") == 0)
			handler = DuplicateHandling.DUP_REMOVE;
		else if (duplicates.compareToIgnoreCase("DUP_COUNT") == 0)
			handler = DuplicateHandling.DUP_COUNT;
		else if (duplicates.compareToIgnoreCase("AUTOKEY") == 0)
			handler = DuplicateHandling.AUTOKEY;
		else
			throw new ExceptionSemantic("RS0023: Expected DUP_REMOVE, DUP_COUNT or AUTOKEY but got: " + duplicates);
		
		if (currentOperatorDefinition.getDepth() > interactiveOperatorNestingDepth)
			throw new ExceptionSemantic("RS0024: EXTERNAL relation-valued variables may not be defined inside a user-defined operator.");
		if (relvarsInProgress.containsKey(varname) || database.isRelvarExists(varname))
			throw new ExceptionSemantic("RS0025: " + varname + " already exists.");

		String className = "org.reldb.rel.v" + Version.getDatabaseVersion() + ".storage.relvars.external." + externalRelvarType.toLowerCase() + ".Relvar" + externalRelvarType.toUpperCase() + "Metadata";
		Class<?> clazz;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e1) {
			throw new ExceptionSemantic("RS0451: Can't find " + className + " to handle external relvar of type " + externalRelvarType);
		}
		
		RelvarCustomMetadata metadata = null;
		try {
			metadata = (RelvarCustomMetadata)clazz.getConstructors()[0].newInstance(database, userRelvarOwner, externalRelvarSpecification, handler);
		} catch (InvocationTargetException ite) {
			String msg = "RS0450: EXTERNAL relvar definition failed due to: " + ite.getCause();
			throw new ExceptionSemantic(msg);
		} catch (Exception e) {
			throw new ExceptionFatal("RS0449: EXTERNAL relvar definition failed due to: " + e);
		}

		RelvarDefinition relvar = new RelvarDefinition(varname, metadata, new References());
		relvarsInProgress.put(varname, relvar);
		beginAssignment();
		compileInstruction(new OpCreateExternalRelvar(relvarsInProgress, relvar));
		endAssignment();
	}
	
	public void dropRelvar(String varName) {
		if (!database.isRelvarExists(varName))
			throw new ExceptionSemantic("RS0031: " + varName + " is not defined.");
		beginAssignment();
		compileInstruction(new OpDropRelvar(varName));
		endAssignment();
	}
	
	// Return TypeScalar value about heading
	public Value getTypeOf(Heading heading, String headingIn) {
		Heading metaHeading = new Heading();
		metaHeading.add("AttrName", TypeCharacter.getInstance());
		metaHeading.add("AttrType", findType("TypeInfo"));
		ValueRelationLiteral attributes = new ValueRelationLiteral(this);
		for (Attribute attribute: heading.getAttributes()) {
			Value[] values = new Value[] {
				ValueCharacter.select(this, attribute.getName()),
				getTypeOf(attribute.getType())
			};
			ValueTuple metadataTuple = new ValueTuple(this, values);
			attributes.insert(metadataTuple);
		}
		ValueCharacter kind = ValueCharacter.select(this, headingIn);
		TypeAlpha typeNonScalar = (TypeAlpha)findType("NonScalar");
		ValueAlpha value = new ValueAlpha(this, typeNonScalar, new Value[] {kind, attributes}, 0);
		return value;
	}
	
	// Return TypeInfo value about typeOfExpression
	public Value getTypeOf(Type typeOfExpression) {
		if (typeOfExpression instanceof TypeHeading) {
			if (typeOfExpression instanceof TypeTuple) {
				Heading heading = ((TypeTuple)typeOfExpression).getHeading();
				return getTypeOf(heading, "TUPLE");
			} else if (typeOfExpression instanceof TypeRelation) {
				Heading heading = ((TypeRelation)typeOfExpression).getHeading();
				return getTypeOf(heading, "RELATION");
			} else if (typeOfExpression instanceof TypeArray) {
				Heading heading = ((TypeArray)typeOfExpression).getElementType().getHeading();
				return getTypeOf(heading, "ARRAY");
			} else {
				// We should never get here, but deal with it sensibly if we do.
				Heading heading = ((TypeRelation)typeOfExpression).getHeading();
				return getTypeOf(heading, typeOfExpression.getSignature());
			}
		} else {
			ValueCharacter scalarSignature = ValueCharacter.select(this, typeOfExpression.getSignature());
			TypeAlpha typeScalar = (TypeAlpha)findType("Scalar");
			ValueAlpha value = new ValueAlpha(this, typeScalar, new Value[] {scalarSignature}, 0);
			return value;
		}
	}

	public void addTypeInProgress(String typeName, Type type) {
		typesInProgress.put(typeName, type);
	}
	
	public void createTypeExternal(String typeName, String language, String source, References references) {
		if (database.isTypeExists(this, typeName))
			throw new ExceptionSemantic("RS0032: TYPE " + typeName + " already exists.");
		(new ForeignCompilerJava(this, verboseExternalOperatorTypeGeneration)).compileForeignType(typeName, language, source);
		source = "TYPE " + typeName + " Java FOREIGN " + source + "\nEND TYPE;";
		beginAssignment();
		compileInstruction(new OpCreateType(typeName, source, userRelvarOwner, "Java", references, null));
		endAssignment();
	}

	private int retrievingTypeCount = 0;
	private Type lastRetrievedType = null;
	
	public void beginTypeRetrieval() {
		retrievingTypeCount++;
	}
	
	public Type endTypeRetrieval() {
		retrievingTypeCount--;
		return lastRetrievedType;
	}
	
	// Needed to support recursive type definitions.
	public void createTypeInternalForwardReference(TypeAlpha udt) {
		String typeName = udt.getTypeName();
		if (retrievingTypeCount == 0 && database.isTypeExists(this, typeName))
			throw new ExceptionSemantic("RS0033: TYPE " + typeName + " already exists.");
		addTypeInProgress(udt.getTypeName(), udt);
	}
	
	public abstract class OperatorAssociatedWithType {		
		public OperatorAssociatedWithType(String createdByType, String owner, OperatorSignature signature, References references) {
			NativeFunction fn = new NativeFunction() {
				public Value evaluate(Value[] arguments) {
					return OperatorAssociatedWithType.this.evaluate(arguments);
				}
			};
			OperatorDefinitionNativeFunction typeOperator = new OperatorDefinitionNativeFunction(signature, fn);
			typeOperator.setCreatedByType(createdByType);
			typeOperator.setLanguage("System");
			typeOperator.setReferences(references);
			typeOperator.setOwner(owner);
			if (retrievingTypeCount == 0)
				persistOperator(typeOperator);
			database.cacheOperator(typeOperator);			
		}
		public abstract Value evaluate(Value[] arguments);
	}
	
	private boolean isSbyC(TypeAlpha udt) {
		if (udt.hasSpecialisationConstraint())
			return true;
		for (TypeAlpha subtype: udt.getSubtypes())
			if (isSbyC(subtype))
				return true;
		return false;
	}
	
	public TypeAlpha findMST(TypeAlpha udt, ValueAlpha value) {
		while (true) 
			try {
				for (TypeAlpha subtype: udt.getSubtypes())
					if (subtype.checkSpecialisationConstraint(this, value, database))
						return findMST(subtype, value);
				return udt;
			} catch (java.util.ConcurrentModificationException cme) {
				System.out.println("Generator: Attempting to recover from concurrent modification exception in findMST() caused by type(s) being loaded.");
			}
	}
	
	private static boolean inSelectValue = false;
	
	public ValueAlpha selectValue(TypeAlpha udt, ValueAlpha value) {
		if (inSelectValue)
			return value;
		inSelectValue = true;
		if (isSbyC(udt))
			value.setMST(findMST(udt, value));
		inSelectValue = false;
		return value;
	}
	
	private void createTypeOperators(final TypeAlpha udt, final String owner) {
		final String typeName = udt.getTypeName();
		// create type's operators
		if (udt.getPossrepCount() == 0) {
			if (udt.isSubtype() && udt.getSupertype().isBuiltin()) {
				// create selector per Database Explorations Chapter 21 page 348 "Selectors for System Defined Types".
				References selectorReferenceToType = new References();
				selectorReferenceToType.addReferenceToType(typeName);
				selectorReferenceToType.addReferenceToType(udt.getSupertype().getTypeName());
				final OperatorSignature selectorSignature = new OperatorSignature(typeName);
				selectorSignature.setReturnType(udt);
				selectorSignature.addParameterType(udt.getSupertype());
				new OperatorAssociatedWithType(typeName, owner, selectorSignature, selectorReferenceToType) {
					public Value evaluate(Value[] arguments) {
						// Essentially, this is TREAT_AS_typename
						if (!((ValueAlpha)arguments[0]).getTypeName().equals(typeName))
							throw new ExceptionSemantic("RS0034: Selector failed.  Argument is not of type '" + typeName + "'.");
						return arguments[0];
					}
				};
			}
		} else {
			for (int possrepAt=0; possrepAt<udt.getPossrepCount(); possrepAt++) {
				final int possrepNumber = possrepAt;
				final Possrep possrep = udt.getPossrep(possrepNumber);
				String selectorName = (possrep.getName() == null) ? typeName : possrep.getName();
				final OperatorSignature selectorSignature = new OperatorSignature(selectorName);
				selectorSignature.setReturnType(udt);
				References selectorReferenceToType = new References();
				selectorReferenceToType.addReferenceToType(typeName);
				for (int componentAt=0; componentAt<possrep.getComponentCount(); componentAt++) {
					PossrepComponent component = possrep.getComponent(componentAt);
					selectorSignature.addParameter(component.getName(), component.getType());
					selectorReferenceToType.addReferenceToType(component.getType().getSignature());
					// create THE_x operator
					References theReferenceToType = new References();
					theReferenceToType.addReferenceToType(typeName);
					theReferenceToType.addReferenceToType(component.getType().getSignature());
					final String theOperatorName = "THE_" + component.getName();
					final OperatorSignature theSignature = new OperatorSignature(theOperatorName);
					theSignature.addParameter("%p0", udt);
					theSignature.setReturnType(component.getType());
					final int componentIndex = component.getComponentIndex();		
					new OperatorAssociatedWithType(typeName, owner, theSignature, theReferenceToType) {
						public Value evaluate(Value[] arguments) {
							Value value = ((ValueAlpha)arguments[0]).getComponentValue(componentIndex);
							if (value == null)
								throw new ExceptionSemantic("RS0036: The value for '" + theOperatorName + "' is undefined.");
							return value;						
						}
					};
				}
				// create selector
				if (!(possrep instanceof DerivedPossrep)) {
					new OperatorAssociatedWithType(typeName, owner, selectorSignature, selectorReferenceToType) {
						public Value evaluate(Value[] arguments) {
							ValueAlpha value;
							// this can be optimised by removing it from the inside of evaluate()
							if (typeName.equals("CHARACTER"))
								value = ValueCharacter.select(Generator.this, arguments[0].stringValue());
							else if (typeName.equals("BOOLEAN"))
								value = ValueBoolean.select(Generator.this, arguments[0].booleanValue());
							else if (typeName.equals("INTEGER"))
								value = ValueInteger.select(Generator.this, arguments[0].longValue());
							else if (typeName.equals("RATIONAL"))
								value = ValueRational.select(Generator.this, arguments[0].doubleValue());
							else
								value = new ValueAlpha(Generator.this, udt, arguments, possrepNumber);					
							if (!possrep.checkConstraint(Generator.this, value, database))
								throw new ExceptionSemantic("RS0037: Selector " + selectorSignature.toRelLookupString() + " violates POSSREP constraint in type '" + typeName + "'."); 
							possrep.runInitialiser(Generator.this, value, database);
							if (isSbyC(udt)) {
								if (!udt.checkSpecialisationConstraint(Generator.this, value, database))
									throw new ExceptionSemantic("RS0038: Selector " + selectorSignature.toRelLookupString() + " violates specialisation constraint in type '" + typeName + "'.");
								value.setMST(findMST(udt, value));
							}
							return value;
						}
					};
				}
			}
		}
		// create IS_x operator
		References isReferenceToType = new References();
		isReferenceToType.addReferenceToType(typeName);
		OperatorSignature isSignature = new OperatorSignature("IS_" + typeName);
		isSignature.addParameter("%p0", udt.getRootType());
		isSignature.setReturnType(TypeBoolean.getInstance());		
		new OperatorAssociatedWithType(typeName, owner, isSignature, isReferenceToType) {
			public Value evaluate(Value[] arguments) {
				if (isSbyC(udt)) {
					return ValueBoolean.select(Generator.this, ((ValueAlpha)arguments[0]).getTypeName().equals(typeName));
				} else {
					Type type = ((ValueAlpha)arguments[0]).getType(getDatabase());
					return ValueBoolean.select(Generator.this, udt.canAccept(type));
				}
			}
		};
		// create TREAT_AS_x operator
		References treatReferenceToType = new References();
		treatReferenceToType.addReferenceToType(typeName);
		final String treatName = "TREAT_AS_" + typeName;
		OperatorSignature treatSignature = new OperatorSignature(treatName);
		treatSignature.addParameter("%p0", udt.getRootType());
		treatSignature.setReturnType(udt);
		new OperatorAssociatedWithType(typeName, owner, treatSignature, treatReferenceToType) {
			public Value evaluate(Value[] arguments) {
				if (isSbyC(udt)) {
					if (!((ValueAlpha)arguments[0]).getTypeName().equals(typeName))
						throw new ExceptionSemantic("RS0039: " + treatName + " failed.  Argument is not of type '" + typeName + "'.");
					return arguments[0];
				} else {
					Type type = ((ValueAlpha)arguments[0]).getType(getDatabase());
					if (!udt.canAccept(type))
						throw new ExceptionSemantic("RS0396: " + treatName + " failed.  Argument is not of type '" + typeName + "'.");
					return arguments[0];
				}
			}
		};
	}
	
	public Type createTypeInternal(final TypeAlpha udt, String source, References references) {
		String typeName = udt.getTypeName();
		if (udt.getPossrepCount() == 0 && !udt.isUnion() && !(udt.isSubtype() && udt.getSupertype().isBuiltin()))
			throw new ExceptionSemantic("RS0040: A non-UNION type must define at least one POSSREP or be an immediate subtype of a built-in type.");
		if (udt.getPossrepCount() > 1)
			udt.checkPossrepInitialisation();		// multiple POSSREPs require initialisation
		if (retrievingTypeCount == 0) {	
			if (database.isTypeExists(this, typeName))
				throw new ExceptionSemantic("RS0041: TYPE " + typeName + " already exists.");
			source = "TYPE " + typeName + " " + source + ";";
			beginAssignment();
			String superTypeName = (udt.isSubtype()) ? udt.getSupertype().getSignature() : null;
			compileInstruction(new OpCreateType(typeName, source, userRelvarOwner, "Rel", references, superTypeName));
			endAssignment();
		}
		createTypeOperators(udt, userRelvarOwner);
		// set last retrieved type
		lastRetrievedType = udt;
		return udt;
	}

	public void createTypeBuiltin(final TypeAlpha udt) {
		String typeName = udt.getTypeName();
		createTypeOperators(udt, "Rel");
		if (retrievingTypeCount == 0) {
			beginAssignment();
			compileInstruction(new OpCreateType(typeName, "", "Rel", "System", new References(), null));
			endAssignment();
		}
	}
	
	public void dropType(String typeName) {
		if (!database.isTypeExists(this, typeName))
			throw new ExceptionSemantic("RS0042: TYPE " + typeName + " does not exist.");
		beginAssignment();
		compileInstruction(new OpDropType(typeName));
		endAssignment();
	}

	private void checkRelvarIsGlobalPersistent(String varname) {
		RelvarMetadata metadata = database.getRelvarMetadata(varname);
		if (!(metadata instanceof RelvarRealMetadata))
			throw new ExceptionSemantic("RS0418: To ALTER VAR " + varname + ", it must be a REAL relvar.");
	}
	
	public RelvarHeading alterVarRealRename(String varname, RelvarHeading relvarHeading, String oldAttributeName, String newAttributeName) {
		checkRelvarIsGlobalPersistent(varname);
		relvarHeading.renameAttribute(oldAttributeName, newAttributeName);
		beginAssignment();
		compileInstruction(new OpAlterVarRealRenameAttribute(varname, oldAttributeName, newAttributeName));
		endAssignment();
		return relvarHeading;
	}

	public RelvarHeading alterVarRealChangeType(String varname, RelvarHeading relvarHeading, String attributeName, Type newType) {
		checkRelvarIsGlobalPersistent(varname);
		relvarHeading.changeTypeAttribute(attributeName, newType);
		beginAssignment();
		compileInstruction(new OpAlterVarRealChangeAttributeType(varname, attributeName, newType));
		endAssignment();
		return relvarHeading;
	}

	public RelvarHeading alterVarRealInsertAttributes(String varname, RelvarHeading relvarHeading, Heading heading) {
		checkRelvarIsGlobalPersistent(varname);
		relvarHeading.insertAttributes(heading);
		beginAssignment();
		compileInstruction(new OpAlterVarRealInsertAttributes(varname, heading));
		endAssignment();
		return relvarHeading;
	}

	public RelvarHeading alterVarRealDropAttribute(String varname, RelvarHeading relvarHeading, String attributeName) {
		checkRelvarIsGlobalPersistent(varname);
		relvarHeading.dropAttribute(attributeName);
		beginAssignment();
		compileInstruction(new OpAlterVarRealDropAttribute(varname, attributeName));
		endAssignment();
		return relvarHeading;
	}

	public void alterVarRealAlterKey(String varname, RelvarHeading keydefs) {
		checkRelvarIsGlobalPersistent(varname);
		beginAssignment();
		compileInstruction(new OpAlterVarRealAlterKey(varname, keydefs));
		endAssignment();
	}

	// Define new slots in the given operator definition to expose individual possrep components (where the ValueUserdefined is assumed to be
	// a parameter in the current operation definition, with a name specified by sourceValueParameterName).
	private class PossrepComponentExposure {
		PossrepComponentExposure(TypeAlpha udt, final String sourceValueParameterName, final Possrep possrep) {
			for (int index=0; index < possrep.getComponentCount(); index++) {
				final int depth = currentOperatorDefinition.getDepth();
				final PossrepComponent component = possrep.getComponent(index);
				currentOperatorDefinition.defineSlot(component.getName(), 
					new SlotScoped(depth, component.getComponentIndex(), component.getType()) {
						public void compileGet(Generator generator) {
							generator.compileGet(sourceValueParameterName);
							generator.compileInstruction(new OpPossrepGetComponent(component.getName(), getOffset()));
						}
						public void compileSet(Generator generator) {
							throw new ExceptionFatal("RS0294: compileSet invoked on SlotScoped in PossrepComponentExposure.");
						}
						public void compileInitialise(Generator generator) {
							throw new ExceptionFatal("RS0295: compileInitialise invoked on SlotScoped in PossrepComponentExposure.");
						}
					});
			}
		}
	}

	public class PossrepConstraint {

		private Possrep possrep;
		private OperatorDefinition possrepConstraintFn;
		
		public PossrepConstraint(Possrep possrep) {
			this.possrep = possrep;
			setPersistentOnlyOn();
			// Define anonymous operator of the form Fn(ValueUserdefined v) RETURNS BOOLEAN; RETURN <boolexpr>; END;
			possrepConstraintFn = beginAnonymousOperator();
			possrepConstraintFn.setDeclaredReturnType(TypeBoolean.getInstance());
			possrepConstraintFn.defineParameter("%p0", possrep.getType());
			new PossrepComponentExposure(possrep.getType(), "%p0", possrep);
		}
		
		public void endPossrepConstraint() {
			// Compile RETURN
			compileReturnValue(possrepConstraintFn.getDeclaredReturnType());
			// End of operator
			endOperator();		
			setPersistentOnlyOff();
			possrep.setConstraint(possrepConstraintFn.getOperator());
		}
	}
			
	public class SpecialisationConstraint {
	
		private TypeAlpha udt;
		private OperatorDefinition specialisationConstraintFn;
		
		public SpecialisationConstraint(TypeAlpha udt) {
			if (!udt.isSubtype())
				throw new ExceptionSemantic("RS0043: A specialisation constraint may only be specified for subtypes.");
			this.udt = udt;
			setPersistentOnlyOn();
			// Define anonymous operator of the form Fn(ValueUserdefined SUPERTYPE_NAME) RETURNS BOOLEAN; RETURN <boolexpr>; END;
			specialisationConstraintFn = beginAnonymousOperator();
			specialisationConstraintFn.setDeclaredReturnType(TypeBoolean.getInstance());
			specialisationConstraintFn.defineParameter(udt.getSupertype().getTypeName(), udt.getSupertype());
		}
		
		public void endSpecialisationConstraint() {
			// Compile RETURN
			compileReturnValue(specialisationConstraintFn.getDeclaredReturnType());
			// End of operator
			endOperator();		
			setPersistentOnlyOff();			
			udt.setSpecialisationConstraint(specialisationConstraintFn.getOperator());
		}
	}
	
	public class PossrepInitialisation {
		
		// Define new slots in the given operator definition to expose all of a type's possrep components 
		// (where the ValueUserdefined is assumed to be a parameter in the current operation definition, 
		// with a name specified by sourceValueParameterName).
		private class PossrepComponentExposureForInitialisation {
			private HashSet<String> componentsRequiringInitialisation = new HashSet<String>();
			
			PossrepComponentExposureForInitialisation(final TypeAlpha udt, final String sourceValueParameterName, final Possrep possrepToInitialise) {
				for (int possrepIndex=0; possrepIndex < udt.getPossrepCount(); possrepIndex++) {
					final Possrep possrep = udt.getPossrep(possrepIndex);
					for (int componentIndex=0; componentIndex<possrep.getComponentCount(); componentIndex++) {
						final int depth = currentOperatorDefinition.getDepth();
						final String componentName = possrep.getComponent(componentIndex).getName();
						if (possrep != possrepToInitialise)
							componentsRequiringInitialisation.add(componentName);
						PossrepComponent component = possrep.getComponent(componentIndex);
						currentOperatorDefinition.defineSlot(component.getName(), 
							new SlotScoped(depth, component.getComponentIndex(), component.getType()) {
								public void compileGet(Generator generator) {
									if (componentsRequiringInitialisation.contains(componentName) && possrep != possrepToInitialise)
										throw new ExceptionSemantic("RS0044: Component '" + componentName + "' has been referenced before being initialised.");
									generator.compileGet(sourceValueParameterName);
									generator.compileInstruction(new OpPossrepGetComponent(componentName, getOffset()));
								}
								public void compileSet(Generator generator) {
									if (possrep == possrepToInitialise)
										throw new ExceptionSemantic("RS0045: Component '" + componentName + "' cannot be assigned because it is set via the selector.");
									generator.compileGet(sourceValueParameterName);
									generator.compileInstruction(new OpPossrepSetComponent(getOffset()));
									componentsRequiringInitialisation.remove(componentName);
								}
								public void compileInitialise(Generator generator) {
									throw new ExceptionFatal("RS0296: compileInitialise invoked on SlotScoped in PossrepComponentExposureForInitialisation.");
								}
							});
					}
				}
			}
			
			public HashSet<String> getUninitialisedComponents() {
				return componentsRequiringInitialisation;
			}
		}

		private PossrepComponentExposureForInitialisation componentExposure;
		
		public PossrepInitialisation(TypeAlpha possreps, String possrepName) {
			if (possreps.getPossrepCount() <= 1)
				throw new ExceptionSemantic("RS0046: INIT is not required if the number of POSSREPs is <= 1.");
			Possrep possrep = possreps.locatePossrep(possrepName);
			if (possrep == null)
				throw new ExceptionSemantic("RS0047: No POSSREP named " + possrepName + " was found.");
			setPersistentOnlyOn();
			// Define anonymous operator of the form Fn(PossrepValue v);
			OperatorDefinition possrepInitProc = beginAnonymousOperator();
			possrepInitProc.defineParameter("p0", possrep.getType());
			componentExposure = new PossrepComponentExposureForInitialisation(possrep.getType(), "p0", possrep);
			possrep.setInitialiser(possrepInitProc.getOperator());
		}
		
		public void endPossrepInitialisation() {
			// End of operator
			endOperator();
			setPersistentOnlyOff();
			HashSet<String> uninitialisedComponents = componentExposure.getUninitialisedComponents();
			if (uninitialisedComponents.size() > 0) {
				String s = "The following POSSREP components have not been INITialised:";
				for (String componentName: uninitialisedComponents)
					s += "\n\t" + componentName;
				throw new ExceptionSemantic("RS0048: " + s);
			}
		}		
	}
		
	public Operator beginConstraintDefinition() {
		setPersistentOnlyOn();
		OperatorDefinition constraintOperator = beginAnonymousOperator();
		setDeclaredReturnType(TypeBoolean.getInstance());
		return constraintOperator.getOperator();
	}
	
	public void endConstraintDefinition() {
		endOperator();
		setPersistentOnlyOff();		
	}
	
	public void createConstraint(String constraintName, String sourceCode, Operator operator, References references) {
		if (database.isConstraintExists(constraintName))
			throw new ExceptionSemantic("RS0049: CONSTRAINT " + constraintName + " already exists.");
		beginAssignment();
		compileInstruction(new OpCreateConstraint(constraintName, sourceCode, operator, userRelvarOwner, references));
		endAssignment();
	}

	public void dropConstraint(String constraintName) {
		if (!database.isConstraintExists(constraintName))
			throw new ExceptionSemantic("RS0050: CONSTRAINT " + constraintName + " does not exist.");
		beginAssignment();
		compileInstruction(new OpDropConstraint(constraintName));
		endAssignment();
	}
	
	/** Turn on capture of global references.  Pass null to turn off collecting. */
	public void setGlobalReferenceCollector(References referenceCollector) {
		globalReferenceCollector = referenceCollector;
	}

	public class ExternalOperator {
		private OperatorDefinition operator;
		
		public ExternalOperator(String fnname) {
			beginOperator(fnname);
			operator = getCurrentOperatorDefinition();
			if (!isTopLevelOperator(operator))
				throw new ExceptionSemantic("RS0051: FOREIGN operators may not be nested.");
		}
		
		public void endExternalOperator(String externalLanguage, String sourcecode) {
			if (operator.getDeclaredReturnType() != null)
				operator.setDefinedReturnValue(true);
			operator = (new ForeignCompilerJava(Generator.this, verboseExternalOperatorTypeGeneration)).compileForeignOperator(getCurrentOperatorDefinition().getSignature(), externalLanguage, sourcecode);
			endOperator();
			// Need to remove and redefine, because operator was initially an OperatorDefinitionRel but is now an OperatorDefinitionNative derivative.
			currentOperatorDefinition.removeOperator(operator.getSignature());
			currentOperatorDefinition.defineOperator(operator);
			addOperator(operator);
		}
	}
	
	public Type locateType(String typeName) {
		Type type = typesInProgress.get(typeName);
		if (type == null)
			type = database.loadType(this, typeName);
		if (type != null && globalReferenceCollector != null)
			globalReferenceCollector.addReferenceToType(typeName);
		return type;
	}
	
	public Type findType(String typeName) {
		Type type = locateType(typeName);
		if (type == null) {
			
			ExceptionSemantic e = new ExceptionSemantic("RS0052: Type '" + typeName + "' has not been defined.");
			e.printStackTrace();
			throw e;
		}
		return type;
	}

	private Type[] possibleTypes(Type type) {
		if (type instanceof TypeAlpha) {
			Type[] subTypes = ((TypeAlpha)type).getSubtypes().toArray(new Type[0]);
			Type[] superTypes = ((TypeAlpha)type).getSupertypes().toArray(new Type[0]);
			Type[] allTypes = new Type[subTypes.length + superTypes.length + 1];
			allTypes[0] = type;
			System.arraycopy(subTypes, 0, allTypes, 1, subTypes.length);
			System.arraycopy(superTypes, 0, allTypes, subTypes.length + 1, superTypes.length);
			return allTypes;
		} else 
			return new Type[] {type};
	}
	
	private void addTypeSignatures(HashSet<OperatorSignature> invocations, OperatorSignature invocationSignature) {
		int parmCount = invocationSignature.getParmCount();
		Type[][] parameterTypes = new Type[parmCount][];
		Type returnType = invocationSignature.getReturnType();
		String name = invocationSignature.getName();
		int permuter[] = new int[parmCount];
		for (int i=0; i<parmCount; i++) {
   			Type type = invocationSignature.getParameterType(i);
   			parameterTypes[i] = possibleTypes(type);
			permuter[i] = 0;
		}
		while (true) {
			OperatorSignature signature = new OperatorSignature(name);
			signature.setReturnType(returnType);
			for (int j=0; j<parmCount; j++)
				signature.addParameterType(parameterTypes[j][permuter[j]]);
			invocations.add(signature);
			int j = 0;
			while (true) {
				if (permuter[j] < parameterTypes[j].length - 1) {
					permuter[j]++;
					break;
				} else {
					permuter[j] = 0;
					j++;
					if (j >= parmCount)
						return;
				}
			}
		}
	}
	
	public HashSet<OperatorSignature> getPossibleTargetSignatures(OperatorDefinition startingOperator, OperatorSignature invocationSignature) {
		HashSet<OperatorSignature> invocations = new HashSet<OperatorSignature>();
		addTypeSignatures(invocations, invocationSignature);
		HashSet<OperatorSignature> sigs = new HashSet<OperatorSignature>();
		for (OperatorSignature invocation: invocations) {
			database.getPossibleTargetSignatures(sigs, this, invocation);
			startingOperator.getPossibleTargetOperators(sigs, invocation);
		}
		return sigs;
	}
	
	public OperatorDefinition locateOperator(OperatorDefinition startingOperator, OperatorSignature signature) {
		OperatorDefinition fn = startingOperator.getOperator(signature);
		if (fn == null) {
			fn = database.loadOperator(this, signature);
			if (fn == null)
				return null;
			else if (globalReferenceCollector != null)
				globalReferenceCollector.addReferenceToOperator(signature.toRelLookupString());
		}
		return fn;
	}
	
	public OperatorDefinition locateOperator(OperatorSignature signature) {
		return locateOperator(currentOperatorDefinition, signature);
	}
	
	public OperatorInvocation findOperator(OperatorSignature signature) {
		if (signature.isPossiblyDynamicDispatch())
			return new OperatorInvocation(signature);
		OperatorDefinition fn = locateOperator(signature);
		if (fn == null)
			throw new ExceptionSemantic("RS0053: Operator '" + signature + "' has not been defined.");
		return new OperatorInvocation(fn);
	}
	
	public Type compileEvaluate(OperatorDefinition operator) {
		return operator.compileEvaluate(this);
	}	
	
	private void compileCall(OperatorDefinition operator) {
		operator.compileCall(this);
	}
	
	public Type compileEvaluate(OperatorSignature signature) {
		OperatorInvocation invocation = findOperator(signature);
		if (invocation.useDynamicDispatch()) {
			Type lastFoundReturnType = null;
			HashSet<OperatorSignature> possibleInvocations = getPossibleTargetSignatures(currentOperatorDefinition, signature);
			for (OperatorSignature searchFor: possibleInvocations) {
				OperatorDefinition operator = locateOperator(searchFor);
				if (operator != null) {
					Type returnType = operator.getDeclaredReturnType();
					if (returnType != null && lastFoundReturnType != null && !lastFoundReturnType.canAccept(returnType))
						throw new ExceptionSemantic("RS0054: Operator " + searchFor + " is a possible invocation target but returns type " + returnType + " which is incompatible with " + lastFoundReturnType);
					if (returnType != null)
						lastFoundReturnType = returnType;
				}
			}
			if (lastFoundReturnType == null)
				throw new ExceptionSemantic("RS0055: Could not find operator " + signature);
			compileInstruction(new OpInvokeDynamicEvaluate(this, invocation.getOperatorSignature()));
			return lastFoundReturnType;
		} else {
			OperatorDefinition operator = invocation.getStaticOperatorDefinition();
			return compileEvaluate(operator);
		}
	}
	
	// ValueOperator will be topmost on stack.  Arguments follow.
	public void compileEvaluateAnonymous() {
		compileInstruction(new OpInvokeAnonymousEvaluate());
	}
	
	public void compileCall(OperatorSignature signature) {
		OperatorInvocation invocation = findOperator(signature);
		if (invocation.useDynamicDispatch()) {
			compileInstruction(new OpInvokeDynamicCall(this, invocation.getOperatorSignature()));
		} else {
			OperatorDefinition operator = invocation.getStaticOperatorDefinition();
			compileCall(operator);
		}
	}
	
	/** Limit global references to non-transient relvars. */
	public void setPersistentOnlyOn() {
		if (persistentOnlyNestingLevel++ == 0)
			persistentOnly = true;
	}
	
	/** Turn off global reference limit. */
	public void setPersistentOnlyOff() {
		if (--persistentOnlyNestingLevel == 0)
			persistentOnly = false;
	}
	
	public Slot findReference(String refname) {
		Slot slot = currentOperatorDefinition.getReference(refname);
		if (slot == null) {
			// is it a database relvar?
			slot = database.openGlobalRelvar(refname);
			if (slot == null) {
		    	RelvarDefinition pendingRelvar = relvarsInProgress.get(refname);
		    	if (pendingRelvar == null)
					throw new ExceptionSemantic("RS0056: '" + refname + "' has not been defined.");
		    	slot = new RelvarInProgress(refname, database, pendingRelvar.getRelvarMetadata());
			}
			if (globalReferenceCollector != null)
				globalReferenceCollector.addReferenceToRelvar(refname);
		} else {
			if (persistentOnly) {
				if (slot instanceof SlotScoped) {
					if (((SlotScoped)slot).getDepth() <= interactiveOperatorNestingDepth)
						throw new ExceptionSemantic("RS0057: In this context, transient global variables like " + refname + " may not be referenced.");
				}
			}
		}
		return slot;
	}

	public void compileRelvarInsert(Slot slot, String relvarName, TypeRelation expressionType) {
		if (slot.isParameter())
			throw new ExceptionSemantic("RS0397: Parameter is not updateable.");
		slot.compileGet(this);
		compileInstruction(new OpRelvarInsert());
	}

	public void compileRelvarInsertNoDuplicates(Slot slot, String relvarName, TypeRelation expressionType) {
		if (slot.isParameter())
			throw new ExceptionSemantic("RS0398: Parameter is not updateable.");
		slot.compileGet(this);
		compileInstruction(new OpRelvarInsertNoDuplicates());
	}

	public void compileRelvarPurge(Slot slot, String relvarName) {
		if (slot.isParameter())
			throw new ExceptionSemantic("RS0399: Parameter is not updateable.");
		slot.compileGet(this);
		compileInstruction(new OpRelvarPurge());
	}

	public void compileTransactionBegin() {
		compileInstruction(new OpTransactionBegin());	
	}
	
	public void compileTransactionCommit() {
		compileInstruction(new OpTransactionCommit());	
	}
	
	public void compileTransactionRollback() {
		compileInstruction(new OpTransactionRollback());	
	}
	
	// Define new slots in the given operator definition to expose individual tuple attributes (where the tuple is assumed to be
	// a parameter in the current operation definition, with a name specified by sourceTupleParameterName), and allow them to be set.
	private abstract class TupleAttributeExposure {
		TupleAttributeExposure(final String sourceTupleParameterName, Heading heading) {
			int index = 0;
			for (final Attribute attribute: heading.getAttributes()) {
				final int depth = currentOperatorDefinition.getDepth();
				currentOperatorDefinition.defineSlot(attribute.getName(), 
					new SlotScoped(depth, index++, attribute.getType()) {
						public void compileGet(Generator generator) {
							generator.compileGet(sourceTupleParameterName);
							generator.compileInstruction(new OpTupleGetAttribute(getOffset()));
						}
						public void compileSet(Generator generator) {
							TupleAttributeExposure.this.compileSet(generator, attribute, getDepth(), depth, getOffset());
						}
						public void compileInitialise(Generator generator) {
							throw new ExceptionFatal("RS0297: compileInitialise invoked on SlotScoped in TupleAttributeExposure.");
						}
					});
			}			
		}
		abstract void compileSet(Generator generator, Attribute attribute, int slotDepth, int operatorDepth, int offset);
	}

	private int parmNameSerialNumber = 0;
	
	// Define an anonymous tuple operator.
	private abstract class AnonymousTupleOperator {
		private OperatorDefinition operator;
		private String name;
		AnonymousTupleOperator(TypeTuple sourceType, Type returnType) {
			name = "%tuple" + parmNameSerialNumber++;
			operator = beginAnonymousOperator();
			beginParameterDefinitions();
			defineOperatorParameter(getTupleParameterName(), sourceType);
			endParameterDefinitions();
			setDeclaredReturnType(returnType);
			// Set up to access each attribute via a Slot in the current scope.
			// Subsequent dereference operations on original tuple attributes will use the following:
			new TupleAttributeExposure(getTupleParameterName(), sourceType.getHeading()) {
				public void compileSet(Generator generator, Attribute attribute, int slotDepth, int operatorDepth, int offset) {
					AnonymousTupleOperator.this.compileSet(generator, attribute, slotDepth, operatorDepth, offset);
				}
			};
		}
		String getTupleParameterName() {
			return name;
		}
		void end() {
			if (getDeclaredReturnType() != null)
				compileReturnValue(getDeclaredReturnType());
			endOperator();
		}
		Operator getOperator() {
			return operator.getOperator();
		}
		OperatorDefinition getOperatorDefinition() {
			return operator;
		}
		public abstract void compileSet(Generator generator, Attribute attribute, int slotDepth, int operatorDepth, int offset);
	}
	
	// Anonymous operator of the form OPERATOR(TUPLE x) RETURNS BOOLEAN
	private class TupleFilterOperator extends AnonymousTupleOperator {
		private String contextDescription;
		TupleFilterOperator(TypeTuple source, String contextDescription) {
			super(source, TypeBoolean.getInstance());
			this.contextDescription = contextDescription;
		}
		public void compileSet(Generator generator, Attribute attribute, int slotDepth, int operatorDepth, int offset) {
			throw new ExceptionFatal("RS0298: Attempt to set attribute " + attribute.getName() + " in " + contextDescription + ".");		
		}
	}
	
	// Anonymous operator of the form OPERATOR(TUPLE x) RETURNS TUPLE
	private class TupleMapOperator extends AnonymousTupleOperator {
		private String contextDescription;
		TupleMapOperator(TypeTuple source, String contextDescription) {
			super(source, source);
			this.contextDescription = contextDescription;
		}
		public void compileSet(Generator generator, Attribute attribute, int slotDepth, int operatorDepth, int offset) {
			if (slotDepth != operatorDepth)
				throw new ExceptionSemantic("RS0058: " + contextDescription + " may only assign values to the most local tuple's attributes.");
			generator.compileGet(getTupleParameterName());
			generator.compileInstruction(new OpTupleSetAttribute(offset));
			generator.compileSet(getTupleParameterName());					
		}
		void end() {
			compileGet(getTupleParameterName());
			super.end();
		}
	}
	
	public class DeleteHandler {
		private TypeRelation operandType;
		private Slot reference;
		private AnonymousTupleOperator whereOperator;
		
		/** Must precede the expression of a DELETE ... [ WHERE ] ... statement.
		 * 
		 * @param relvarName - name of relvar
		 * @param operand - TypeRelation of relation operand
		 * @return - DeleteHandler
		 */
		public DeleteHandler(String relvarName, TypeRelation operand) {
			reference = findReference(relvarName);
			if (!(reference instanceof Relvar || reference instanceof RelvarPrivate || reference.getType().toString().startsWith("RELATION")))
				throw new ExceptionSemantic("RS0059: Expected a relation-valued variable or attribute in DELETE but got " + reference.getType());
			if (reference.isParameter())
				throw new ExceptionSemantic("RS0400: Parameter is not updateable.");
			operandType = operand;
			whereOperator = null;
		}

		// Invoked if WHERE is specified.
		public void doWhere() {
			// Define an anonymous operator of the type OPERATOR(TUPLE x) RETURNS BOOLEAN
			whereOperator = new TupleFilterOperator(new TypeTuple(operandType.getHeading()), "DELETE ... WHERE");
		}
		
		/** Must follow the expression of a DELETE ... [ WHERE ] ... statement.
		 * @param expressionType 
		 * 
		 * @param where - DeleteWhere
		 * @return - TypeRelation
		 */
		public TypeRelation endDeleteHandler(Type expressionType) {
			if (whereOperator != null) {
				whereOperator.end();
				reference.compileGet(Generator.this);
				compileInstruction(new OpRelvarDeleteWhere(whereOperator.getOperator()));
			} else {
				if (!operandType.canAccept(expressionType))
					throw new ExceptionSemantic("RS0060: Expected expression of type " + operandType + " but got " + expressionType);
				compileReformat(operandType, expressionType);
				reference.compileGet(Generator.this);
				compileInstruction(new OpRelvarDeleteGivenExpression());
			}
			return operandType;
		}
	}

	public void compileRelvarIDelete(Slot slot, String identifier) {
		if (slot.isParameter())
			throw new ExceptionSemantic("RS0401: Parameter is not updateable.");
		slot.compileGet(Generator.this);
		compileInstruction(new OpRelvarIDelete());
	}
	
	public class UpdateWhere {
		private AnonymousTupleOperator tupleUpdateOp = null;
		private AnonymousTupleOperator tupleFilterOp = null;
		private TypeTuple sourceTupleType;
		private Slot reference;
		
		/** Begin relvar update.  
		 * 
		 * @param relvarName
		 * @param sourceType
		 */
		public UpdateWhere(String relvarName, TypeRelation sourceType) {
			reference = findReference(relvarName);
			if (!(reference instanceof Relvar || reference instanceof RelvarPrivate || reference.getType().toString().startsWith("RELATION")))
				throw new ExceptionSemantic("RS0061: Expected a relation-valued variable or attribute in UPDATE ... WHERE but got " + reference.getType());
			if (reference.isParameter())
				throw new ExceptionSemantic("RS0402: Parameter is not updateable.");
			sourceTupleType = new TypeTuple(sourceType.getHeading());
		}
		
		/** To be used immediately prior to a relvar update's optional WHERE clause expression. */
		public void beginRelvarUpdateWhere() {
			// Define an anonymous operator of the type OPERATOR(TUPLE x) RETURNS BOOLEAN
			tupleFilterOp = new TupleFilterOperator(sourceTupleType, "UPDATE ... WHERE");
		}
				
		/** To be used immediately after a relvar update's optional WHERE clause expression. */
		public void endRelvarUpdateWhere() {
			tupleFilterOp.end();
		}
		
		/** Begin update statement */
		public void beginRelvarUpdateAssignment() {
			// Define anonymous function that accepts a tuple as an argument and returns a tuple
			tupleUpdateOp = new TupleMapOperator(sourceTupleType, "Tuple UPDATE");
		}
		
		/**
		 * End relvar update.
		 * 
		 * This is to be used immediately after an assignment statement.
		 */	
		public void endUpdateWhere() {
			tupleUpdateOp.end();
			reference.compileGet(Generator.this);
			if (tupleFilterOp != null)
				compileInstruction(new OpRelvarUpdateWhere(tupleFilterOp.getOperator(), tupleUpdateOp.getOperator()));
			else
				compileInstruction(new OpRelvarUpdate(tupleUpdateOp.getOperator()));
		}
	}

	/** Assuming a Value on the stack of Type sourceType is to be assigned
	 * to a slot of Type destinationType, project the Value to conform to
	 * destinationType.  This should only compile a projection if the
	 * source and destination are both of TypeHeading, or are both of TypeArray.
	 * 
	 * @param destinationType
	 * @param sourceType
	 */
	public Type compileReformat(Type destinationType, Type sourceType) {
		if (!destinationType.requiresReformatOf(sourceType))
			return destinationType;
		if (destinationType instanceof TypeArray && sourceType instanceof TypeArray) {
			Type sourceElementType = ((TypeArray)sourceType).getElementType();
			Type destinationElementType = ((TypeArray)destinationType).getElementType();
			if (!destinationType.requiresReformatOf(sourceType))
				return destinationType;
			AttributeMap reformatMap = new AttributeMap(((TypeHeading)destinationElementType).getHeading(), ((TypeHeading)sourceElementType).getHeading()); 
			compileInstruction(new OpArrayProject(reformatMap));
		} else {
			AttributeMap reformatMap = new AttributeMap(((TypeHeading)destinationType).getHeading(), ((TypeHeading)sourceType).getHeading()); 
			if (destinationType instanceof TypeTuple && sourceType instanceof TypeTuple) {
				compileInstruction(new OpTupleProject(reformatMap));
			} else if (destinationType instanceof TypeRelation && sourceType instanceof TypeRelation) {
				compileInstruction(new OpTupleIteratableProject(reformatMap));
			} else 
				throw new ExceptionFatal("RS0299: compileReformat doesn't know how to deal with a " + destinationType + " and a " + sourceType);
		}
		return destinationType;
	}

	/** Load an ARRAY variable from an ARRAY (usually generated by ORDER). */
	private void compileLoadArrayFromRelation(String identifier, Slot slot, TypeArray expressionType) {
		TypeArray arrayVarType = (TypeArray)slot.getType();
		Type targetContainedType = arrayVarType.getElementType();
		if (!(targetContainedType instanceof TypeTuple))
			throw new ExceptionSemantic("RS0062: 'LOAD " + identifier + "' should reference an ARRAY of TUPLEs, but references an ARRAY of " + targetContainedType);
		Type sourceContainedType = expressionType.getElementType();
		if (!targetContainedType.canAccept(sourceContainedType))
			throw new ExceptionSemantic("RS0063: 'LOAD " + identifier + "' expected an ARRAY of " + targetContainedType + " but got an ARRAY of " + sourceContainedType);
		compileReformat(arrayVarType, expressionType);
		compileInstruction(new OpArrayToArray());
		compileSet(identifier);
	}
	
	/** Load a relvar from an ARRAY. */
	private void compileLoadRelationFromArray(String identifier, Slot slot, TypeArray expressionType) {
		compileInstruction(new OpArrayToRelation());
		Type containedType = expressionType.getElementType();
		if (!(containedType instanceof TypeTuple))
			throw new ExceptionSemantic("RS0064: 'LOAD " + identifier + "' should reference an ARRAY of TUPLEs, but references an ARRAY of " + containedType);
		Heading sourceHeading = ((TypeTuple)containedType).getHeading();
		TypeRelation source = new TypeRelation(sourceHeading);
		TypeRelation target = (TypeRelation)slot.getType();
		Heading targetHeading = target.getHeading();
		if (!target.canAccept(source))
			throw new ExceptionSemantic("RS0065: 'LOAD " + identifier + "' expected an ARRAY with TUPLEs of heading " + targetHeading + " but got " + sourceHeading);
		compileReformat(target, source);
		compileSet(identifier);
	}

	public void compileLoad(String identifier, Type expressionType) {
		Slot slot = findReference(identifier);
		if (slot.getType() instanceof TypeRelation) {
			if (expressionType instanceof TypeArray)
				compileLoadRelationFromArray(identifier, slot, (TypeArray)expressionType);
			else
				throw new ExceptionSemantic("RS0066: 'LOAD " + identifier + " FROM ...' expected an expression of type ARRAY, but got " + expressionType);
		} else if (slot.getType() instanceof TypeArray) {
			if (expressionType instanceof TypeArray)
				compileLoadArrayFromRelation(identifier, slot, (TypeArray)expressionType);
			else
				throw new ExceptionSemantic("RS0067: 'LOAD " + identifier + " FROM ...' expected an expression of type ARRAY (usually via ORDER), but got " + expressionType);
		} else
			throw new ExceptionSemantic("RS0068: 'LOAD " + identifier + "', should reference a RELATION-valued variable or an ARRAY variable, but references a variable of type " + slot.getType());
	}
	
	/** Small DIVIDEBY 
	  	A1 - attributes common to r1 and r3
		A2 - attributes common to r2 and r3

		r1 {A1}   MINUS   ((r1 {A1} JOIN r2 {A2}) MINUS r3 {A1, A2}) {A1}
	 **/
	public TypeRelation compileSmallDivide(TypeRelation r1, TypeRelation r2, TypeRelation r3) {
		final String r1Name = "%r1";
		final String r2Name = "%r2";
		final String r3Name = "%r3";
		Heading A1 = r1.getHeading().intersect(r3.getHeading());
		Heading A2 = r2.getHeading().intersect(r3.getHeading());
		Heading A1A2 = A1.union(A2);
		// Define anonymous operator
		OperatorDefinition div = beginAnonymousOperator();
		beginParameterDefinitions();
		defineOperatorParameter(r1Name, r1);
		defineOperatorParameter(r2Name, r2);
		defineOperatorParameter(r3Name, r3);
		endParameterDefinitions();
		// VarGet r1 
		compileGet(r1Name);
		// RelationProject A1
		TypeRelation r1p = compileRelationProject(r1, new SelectAttributes(A1));
		// DUP
		compileDuplicate();
		// VarGet r2 
		compileGet(r2Name);
		// RelationProject A2
		TypeRelation r2p = compileRelationProject(r2, new SelectAttributes(A2));
		// RelationJoin
		TypeRelation r1r2joined = compileRelationJoin(r1p, r2p);
		// VarGet r3 
		compileGet(r3Name);
		// RelationProject A1, A2
		TypeRelation r3p = compileRelationProject(r3, new SelectAttributes(A1A2));
		// RelationMinus 
		TypeRelation minusResult = compileRelationMinus(r1r2joined, r3p);
		// RelationProject A1
		TypeRelation rightResult = compileRelationProject(minusResult, new SelectAttributes(A1));
		// RelationMinus 
		TypeRelation finalResult = compileRelationMinus(r1p, rightResult);
		setDeclaredReturnType(finalResult);
		compileReturnValue(finalResult);
		endOperator();
		return (TypeRelation)compileEvaluate(div);		
	}
	
	/** Great DIVIDEBY 
		A1 - attributes common to r1, r3
		A2 - attributes common to r2, r4
		A3 - attributes common to r3, r4

		WITH r1 {A1} as r1p, r4 {A2, A3} as r4p:
		(r1p JOIN r2 {A2})  MINUS  ((r1p JOIN r4p) MINUS (r3 {A1, A3} JOIN r4p)) {A1, A2}
	 **/
	public TypeRelation compileGreatDivide(TypeRelation r1, TypeRelation r2, TypeRelation r3, TypeRelation r4) {
		final String r1Name = "%r1";
		final String r2Name = "%r2";
		final String r3Name = "%r3";
		final String r4Name = "%r4";
		final String r1pName = "%r1p";
		final String r4pName = "%r4p";
		Heading A1 = r1.getHeading().intersect(r3.getHeading());
		Heading A2 = r2.getHeading().intersect(r4.getHeading());
		Heading A3 = r3.getHeading().intersect(r4.getHeading());
		Heading A1A2 = A1.union(A2);
		Heading A1A3 = A1.union(A3);
		Heading A2A3 = A2.union(A3);
		// Define anonymous operator
		OperatorDefinition div = beginAnonymousOperator();
		beginParameterDefinitions();
		defineOperatorParameter(r1Name, r1);
		defineOperatorParameter(r2Name, r2);
		defineOperatorParameter(r3Name, r3);
		defineOperatorParameter(r4Name, r4);
		endParameterDefinitions();
		// WITH
		With with = new With();
		// r1
		compileGet(r1Name);
		// {A1}
		TypeRelation r1pType = this.compileRelationProject(r1, new SelectAttributes(A1));
		// as r1p
		with.addWithItem(r1pType, r1pName);
		// r4
		compileGet(r4Name);
		// {A2, A3}
		TypeRelation r4pType = this.compileRelationProject(r4, new SelectAttributes(A2A3));
		// as r4p
		with.addWithItem(r4pType, r4pName);		
		// VarGet r1p 
		compileGet(r1pName);
		// VarGet r2 
		compileGet(r2Name);
		// RelationProject A2
		TypeRelation r2Project = compileRelationProject(r2, new SelectAttributes(A2));
		// RelationJOIN
		TypeRelation leftJoin = compileRelationJoin(r1pType, r2Project);
		// VarGet r1p 
		compileGet(r1pName);
		// VarGet r4p 
		compileGet(r4pName);
		// RelationJOIN
		TypeRelation middleJoin = compileRelationJoin(r1pType, r4pType);
		// VarGet r3 
		compileGet(r3Name);
		// RelationProject A1, A3
		TypeRelation r3Project = compileRelationProject(r3, new SelectAttributes(A1A3));
		// VarGet r4p 
		compileGet(r4pName);
		// RelationJoin
		TypeRelation rightJoin = compileRelationJoin(r3Project, r4pType);
		// RelationMinus 
		TypeRelation firstMinus = compileRelationMinus(middleJoin, rightJoin);
		// RelationProject A1, A2
		TypeRelation rightProject = compileRelationProject(firstMinus, new SelectAttributes(A1A2));
		// RelationMinus 
		TypeRelation finalResult = compileRelationMinus(leftJoin, rightProject);
		// end of WITH
		with.endWith(finalResult);
		// end of DIVIDEBY anonymous operator
		setDeclaredReturnType(finalResult);
		compileReturnValue(finalResult);
		endOperator();
		return (TypeRelation)compileEvaluate(div);		
	}

	private static int summarizeParmNameSerial = 0;
	private static int summarizeItemNameSerial = 0;
	
	public class Summarize {
		private OperatorDefinition summarizeOperator;
		private Extend outerExtend;
		private String r1ParmName;
		private String r2ParmName;
		private String yItemName;
		
		/** Begin SUMMARIZE */
		public Summarize(TypeRelation source, TypeRelation per) {
			// create parm names
			r1ParmName = "%R1" + summarizeParmNameSerial;
			r2ParmName = "%R2" + summarizeParmNameSerial;
			yItemName = "%Y" + summarizeParmNameSerial;
			summarizeParmNameSerial++;
			// Attributes common to r1 (source) and r2 (per)
			Heading common = source.getHeading().intersect(per.getHeading());
			// Begin SUMMARIZE anonymous operator
			summarizeOperator = beginAnonymousOperator();
			beginParameterDefinitions();
			defineOperatorParameter(r1ParmName, source);
			defineOperatorParameter(r2ParmName, per);
			endParameterDefinitions();
			// Get r2
			compileGet(r2ParmName);
			// begin extend of r2
			outerExtend = new Extend(per.getHeading());
			// get r1
			compileGet(r1ParmName);
			// RELATION {
			RelationDefinition relDef = new RelationDefinition(common);
			// TUPLE {
			TupleDefinition tupleDef = new TupleDefinition();
			// put common attributes in tuple -- {a a, b b, ...} etc.
			for (Attribute attribute : common.getAttributes()) {
				// get attribute from r1
				compileGet(attribute.getName());
				// set tuple attribute
				tupleDef.setTupleAttribute(attribute.getName(), attribute.getType());
			}
			// }
			TypeTuple tupleType = tupleDef.endTuple();
			relDef.addTupleToRelation(tupleType);
			// }
			TypeRelation relationType = relDef.endRelation();
			Type typeOfY = compileRelationJoin(source, relationType);
			// Extend AS %Y
			outerExtend.addExtendItem(yItemName, typeOfY);
		}
		
		/** End a SUMMARIZE definition. */
		public TypeRelation endSummarize() {
			// end EXTEND
			TypeRelation extendType = endRelationExtend(outerExtend);
			// {ALL BUT %Y}
			SelectAttributes eliminateYProjector = new SelectAttributes();
			eliminateYProjector.add(yItemName);
			eliminateYProjector.setAllBut(true);
			TypeRelation result = compileRelationProject(extendType, eliminateYProjector);
			// End SUMMARIZE operator
			setDeclaredReturnType(result);
			compileReturnValue(result);
			endOperator();
			// Compile invocation of SUMMARIZE operator
			return (TypeRelation)compileEvaluate(summarizeOperator);
		}
		
		public class SummarizeItem {
			private Extend innerExtend;
			private TypeRelation typeOfY;
			private String extendAttributeName;
			private boolean isDistinct;
			
			/** Begin a SUMMARIZE item definition. */
			public SummarizeItem() {
				extendAttributeName = "%X" + summarizeItemNameSerial;
				summarizeItemNameSerial++;
				// PUSH value of '%Y'
				typeOfY = (TypeRelation)compileGet(yItemName);
			}

			public String getExtendAttributeName() {
				return extendAttributeName;
			}
			
			public TypeRelation getTypeOfY() {
				return typeOfY;
			}
			
			/** This must appear before a SUMMARIZE expression 'exp' */
			public void beginSummarizeItemExpression() { 
				// BEGIN EXTEND
				innerExtend = new Extend(typeOfY.getHeading());
			}

			/** End a SUMMARIZE aggregate operator's expression. 
			 * 
			 * This must appear immediately after a SUMMARIZE expression 'exp' 
			 * 
			 * This should appear before invocation of the aggregate operator.
			 */
			public TypeRelation endSummarizeItemExpression(Type exprType, boolean distinct) {
				// Extend AS %X
				innerExtend.addExtendItem(extendAttributeName, exprType);
				TypeRelation forAggregateExpressionType = endRelationExtend(innerExtend);
				// if distinct, compile {extendAttributeName}
				if (distinct) {
					SelectAttributes distinctProjector = new SelectAttributes();
					distinctProjector.add(extendAttributeName);
					forAggregateExpressionType = compileRelationProject(forAggregateExpressionType, distinctProjector);
				}
				return forAggregateExpressionType;
			}

			/** End a SUMMARIZE item. 
			 * 
			 * This must appear after invocation of the aggregate operator. 
			 */
			public void endSummarizeItem(Type aggReturnType, String aggResultName) {
				// Extend with aggResultName
				outerExtend.addExtendItem(aggResultName, aggReturnType);
			}

			public void setDistinct(boolean b) {
				isDistinct = b;
			}
			
			public boolean isDistinct() {
				return isDistinct;
			}
		}
	}

	private static int extendTupleNameSerial = 0;

	public class Extend {
		private OperatorDefinition extendOp;
		private Heading sourceHeading;
		private Heading extendedHeading = new Heading();
		private String sourceTupleParmName;
		private String extendTupleParmName;
		
		/**
		 * Begin tuple or relation extend.
		 * 
		 * This is to be used immediately before any extend expressions, to
		 * introduce the existing tuple's attributes into the current scope so they
		 * can be dereferenced, and to introduce the extend attributes so they can
		 * be assigned.
		 */
		public Extend(Heading sourceHeading) {
			this.sourceHeading = sourceHeading;
			// create parameter names
			sourceTupleParmName = "%source_tuple" + extendTupleNameSerial;
			extendTupleParmName = "%extend_tuple" + extendTupleNameSerial;
			extendTupleNameSerial++;
			// Define anonymous function that accepts two tuples as arguments and returns a tuple
			extendOp = beginAnonymousOperator();
			beginParameterDefinitions();
			defineOperatorParameter(sourceTupleParmName, new TypeTuple(sourceHeading));
			defineOperatorParameter(extendTupleParmName, TypeTuple.getEmptyTupleType());
			endParameterDefinitions();
			// Set up to access each attribute via a Slot in the current scope.
			// Subsequent dereference operations on original tuple attributes will use the following:
			new TupleAttributeExposure(sourceTupleParmName, sourceHeading) {
				public void compileSet(Generator generator, Attribute attribute, int slotDepth, int operatorDepth, int offset) {
					throw new ExceptionFatal("RS0300: Attempt to set original attribute '" + attribute.getName() + "' in EXTEND.");
				}
			};
		}

		/** This should be called immediately after the compilation of an expression whose
		 * result will be assigned to the given new identifier.
		 */
		public void addExtendItem(String identifier, Type expressionType) {
			// define target as new slot
			final int depth = currentOperatorDefinition.getDepth();
			SlotScoped newSlot = new SlotScoped(depth, extendedHeading.getDegree(), expressionType) {
				public void compileGet(Generator generator) {
					generator.compileGet(extendTupleParmName);
					generator.compileInstruction(new OpTupleGetAttribute(getOffset()));
				}
				public void compileSet(Generator generator) {
					if (getDepth() != depth)
						throw new ExceptionSemantic("RS0069: EXTEND may only assign values to newly-defined attributes.");
					generator.compileGet(extendTupleParmName);
					generator.compileInstruction(new OpTupleSetAttribute(getOffset()));
					generator.compileSet(extendTupleParmName);		
				}
				public void compileInitialise(Generator generator) {
					throw new ExceptionFatal("RS0301: compileInitialise invoked on SlotScoped in addExtendItem().");
				}
			};
			currentOperatorDefinition.defineSlot(identifier, newSlot);
			// compile assign to attribute here
			compileSet(newSlot);
			// add to extend heading
			extendedHeading.add(identifier, expressionType);
		}

		/** Compile generation of a unique tuple number, and assign it. */
		public void addExtendSerialiser(String attributeName) {
			compileInstruction(new OpGetTemporarilyUniqueInteger());
			addExtendItem(attributeName, TypeInteger.getInstance());
		}
		
		/** Get extended heading. */
		public Heading getExtendedHeading() {
			return sourceHeading.unionDisjoint(extendedHeading);
		}
		
		/** End extend definition.  This is invoked immediately after any extend expressions. */
		void endExtendDefinition() {
			setDeclaredReturnType(new TypeTuple(getExtendedHeading()));
			// Compile tuple join
			compileGet(sourceTupleParmName);
			compileGet(extendTupleParmName);
			compileInstruction(new OpTupleJoinDisjoint());
			compileReturnValue(getDeclaredReturnType());
			endOperator();
		}
	}
	
	public TypeRelation compileRelationProject(TypeRelation operand, SelectAttributes attributes) {
		if (attributes.isEverything())
			return operand;
		Heading destination = operand.getHeading().project(attributes);
		compileInstruction(new OpTupleIteratableProject(new AttributeMap(destination, operand.getHeading())));
		return new TypeRelation(destination);
	}

	public TypeArray compileArrayProject(TypeArray operand, SelectAttributes attributes) {
		if (attributes.isEverything())
			return operand;
		Heading destination = operand.getHeading().project(attributes);
		compileInstruction(new OpTupleIteratableProject(new AttributeMap(destination, operand.getHeading())));
		return new TypeArray(destination);
	}

	public class Where {
		private TypeRelation operandType;
		private AnonymousTupleOperator whereOperator;
		
		/** Must precede the boolean expression of a WHERE operator.
		 * 
		 * @param operand - TypeRelation of relation operand
		 * @return - Where
		 */
		public Where(TypeRelation operand) {
			operandType = operand;		
			// Define an anonymous operator of the type OPERATOR(TUPLE x) RETURNS BOOLEAN
			whereOperator = new TupleFilterOperator(new TypeTuple(operand.getHeading()), "WHERE");
		}
		
		/** Must follow the boolean expression of a WHERE operator.
		 * 
		 * @param where - Where
		 * @return - TypeRelation
		 */
		public TypeRelation endWhere() {
			whereOperator.end();
			compileInstruction(new OpRelationWhere(whereOperator.getOperator()));
			return operandType;
		}
	}
		
	public class With {
		private OperatorDefinition withWrapper;
		
		/** Must precede WITH definition. */
		public With() {
			// Wrap WITH in operator, so as to isolate introduced names to their own scope
			withWrapper = beginAnonymousOperator();
		}
		
		/** Must follow WITH item expression. */
		public void addWithItem(Type expressionType, String introducedName) {
			defineConstant(introducedName, expressionType);
		}
		
		/** Must follow WITH definition and final expression. */
		public Type endWith(Type expressionType) {
			setDeclaredReturnType(expressionType);
			compileReturnValue(expressionType);
			endOperator();
			return compileEvaluate(withWrapper);
		}
	}

	public Type compileTClose(Type expressionType) {
		if (!(expressionType instanceof TypeRelation))
			throw new ExceptionSemantic("RS0070: TCLOSE expected RELATION but got " + expressionType);
		Heading exprHeading = ((TypeRelation)expressionType).getHeading();
		if (exprHeading.getDegree() != 2)
			throw new ExceptionSemantic("RS0071: TCLOSE expected RELATION of degree 2, but degree is " + exprHeading.getDegree());
		if (!exprHeading.getAttributes().get(0).getType().getSignature().equals(exprHeading.getAttributes().get(1).getType().getSignature()))
			throw new ExceptionSemantic("RS0072: TCLOSE expected both attributes of the RELATION to be the same type, but they aren't.");
		compileInstruction(new OpRelationTClose());
		return expressionType;
	}
		
	private void testLeftCanAcceptRight(Type leftType, Type rightType) {
		if (!leftType.canAccept(rightType))
			throw new ExceptionSemantic("RS0073: " + leftType + " is not compatible with " + rightType);		
	}
	
	public TypeRelation compileRelationUnion(TypeRelation leftType, TypeRelation rightType) {
		testLeftCanAcceptRight(leftType, rightType);
		compileReformat(leftType, rightType);
		compileInstruction(new OpRelationUnion());
		return leftType;
	}
	
	public TypeRelation compileRelationXunion(TypeRelation leftType, TypeRelation rightType) {
		testLeftCanAcceptRight(leftType, rightType);
		compileReformat(leftType, rightType);
		compileInstruction(new OpRelationXunion());
		return leftType;
	}
	
	public TypeRelation compileRelationDUnion(TypeRelation leftType, TypeRelation rightType) {
		testLeftCanAcceptRight(leftType, rightType);
		compileReformat(leftType, rightType);
		compileInstruction(new OpRelationDUnion());
		return leftType;
	}
	
	public TypeRelation compileRelationIntersect(TypeRelation leftType, TypeRelation rightType) {
		testLeftCanAcceptRight(leftType, rightType);
		compileReformat(leftType, rightType);
		compileInstruction(new OpRelationIntersect());
		return leftType;
	}
	
	public TypeRelation compileRelationMinus(TypeRelation leftType, TypeRelation rightType) {
		testLeftCanAcceptRight(leftType, rightType);
		compileReformat(leftType, rightType);
		compileInstruction(new OpRelationMinus());
		return leftType;
	}

	public TypeRelation compileRelationIMinus(TypeRelation leftType, TypeRelation rightType) {
		testLeftCanAcceptRight(leftType, rightType);
		compileReformat(leftType, rightType);
		compileInstruction(new OpRelationIMinus());
		return leftType;
	}
	
	public TypeRelation compileRelationJoin(TypeRelation leftType, TypeRelation rightType) {
		Heading left = leftType.getHeading();
		Heading right = rightType.getHeading();
		Heading intersect = left.intersect(right);
		Heading result = left.union(right);
		if (intersect.getDegree() == 0)
			compileInstruction(new OpRelationProduct());
		else
			compileInstruction(new OpRelationJoin(new JoinMap(result, left, right)));
		return new TypeRelation(result);
	}

	public TypeRelation compileRelationTimes(TypeRelation leftType, TypeRelation rightType) {
		Heading left = leftType.getHeading();
		Heading right = rightType.getHeading();
		Heading intersect = left.intersect(right);
		Heading result = left.union(right);
		if (intersect.getDegree() == 0)
			compileInstruction(new OpRelationProduct());
		else
			throw new ExceptionSemantic("RS0074: Attempt to perform TIMES on operands with attributes in common.  Perhaps you want to use JOIN or RENAME?");
		return new TypeRelation(result);
	}
	
	public TypeRelation compileRelationCompose(TypeRelation leftType, TypeRelation rightType) {
		Heading intersect = leftType.getHeading().intersect(rightType.getHeading());
		if (intersect.getDegree() == 0) {
			compileInstruction(new OpRelationProduct());
			return new TypeRelation(leftType.getHeading().unionDisjoint(rightType.getHeading()));
		} else {
			TypeRelation joinType = compileRelationJoin(leftType, rightType);
			TypeRelation composeType = new TypeRelation(joinType.getHeading().minus(intersect));
			compileReformat(composeType, joinType);
			return composeType;
		}
	}

	public TypeRelation compileRelationSemijoin(TypeRelation leftType, TypeRelation rightType) {
		compileReformat(leftType, compileRelationJoin(leftType, rightType));
		return leftType;
	}
	
	public TypeRelation compileRelationSemiminus(TypeRelation leftType, TypeRelation rightType) {
		compileDuplicateUnder();
		return compileRelationMinus(leftType, compileRelationSemijoin(leftType, rightType));
	}
	
	/** End TupleIterable extend. */
	private Heading endTupleIteratableExtend(Extend extend) {
		final String sourceParameterName = "%source";
		extend.endExtendDefinition();
		Heading extendedHeading = extend.sourceHeading.unionDisjoint(extend.extendedHeading);
		// Define map operator for relational map operation
		OperatorDefinition mapOp = beginAnonymousOperator();
		beginParameterDefinitions();
		defineOperatorParameter(sourceParameterName, new TypeTuple(extend.sourceHeading));
		endParameterDefinitions();
		setDeclaredReturnType(new TypeTuple(extendedHeading));
		// Get source tuple
		compileGet(sourceParameterName);
		// Initialisation of extend_tuple.
		compilePush(new ValueTuple(this, new TypeTuple(extend.extendedHeading)));
		// Invoke anonymous function
		compileEvaluate(extend.extendOp);
		compileReturnValue(getDeclaredReturnType());
		endOperator();
		compileInstruction(new OpTupleIteratableMap(mapOp.getOperator()));
		return extendedHeading;
	}
	
	/** End relation extend. */
	public TypeRelation endRelationExtend(Extend extend) {
		Heading extendedHeading = endTupleIteratableExtend(extend);
		return new TypeRelation(extendedHeading);
	}
	
	/** End ARRAY extend. (Used by aggregation.) */
	public TypeArray endArrayExtend(Extend extend) {
		Heading extendedHeading = endTupleIteratableExtend(extend);
		return new TypeArray(extendedHeading);		
	}
	
	public TypeRelation compileRelationWrap(TypeRelation sourceType, SelectAttributes selection, String name) {
		final String sourceParameterName = "%source";
		// Define map operator for relational map operation
		OperatorDefinition mapOp = beginAnonymousOperator();
		beginParameterDefinitions();
		defineOperatorParameter(sourceParameterName, new TypeTuple(sourceType.getHeading()));
		endParameterDefinitions();
		setDeclaredReturnType(new TypeTuple(sourceType.getHeading()));  // bogus return type, but it's ignored
		// Get source tuple
		compileGet(sourceParameterName);
		TypeTuple tupleType = compileTupleWrap(new TypeTuple(sourceType.getHeading()), selection, name);
		compileReturnValue(getDeclaredReturnType());
		endOperator();
		compileInstruction(new OpTupleIteratableMap(mapOp.getOperator()));
		return new TypeRelation(tupleType.getHeading());	
	}

	public TypeRelation compileRelationUnwrap(TypeRelation operand, String attributeName) {
		final String sourceParameterName = "%source";
		// Define map operator for relational map operation
		OperatorDefinition mapOp = beginAnonymousOperator();
		beginParameterDefinitions();
		defineOperatorParameter(sourceParameterName, new TypeTuple(operand.getHeading()));
		endParameterDefinitions();
		setDeclaredReturnType(new TypeTuple(operand.getHeading()));  // bogus return type, but it's ignored
		// Get source tuple
		compileGet(sourceParameterName);
		TypeTuple tupleType = compileTupleUnwrap(new TypeTuple(operand.getHeading()), attributeName);
		compileReturnValue(getDeclaredReturnType());
		endOperator();
		compileInstruction(new OpTupleIteratableMap(mapOp.getOperator()));
		return new TypeRelation(tupleType.getHeading());	
	}

	public TypeRelation compileRelationGroup(TypeRelation sourceType, SelectAttributes itemList, String name) {
		if (itemList.isAllBut())
			itemList.makeNamesExplicit(sourceType.getHeading());
		Heading groupedAttributeHeading = new Heading();
		Heading sourceHeading = sourceType.getHeading();
		Heading sortAttributeHeading = new Heading(sourceHeading);
		for (String attributeName: itemList.getNames()) {
			groupedAttributeHeading.add(attributeName, sourceHeading.getAttribute(attributeName).getType());
			sortAttributeHeading.remove(attributeName);
		}
		Heading resultHeading = new Heading(sortAttributeHeading);
		resultHeading.add(name, new TypeRelation(groupedAttributeHeading));
		compileInstruction(new OpRelationGroup(new AttributeMap(sortAttributeHeading, sourceHeading), new AttributeMap(groupedAttributeHeading, sourceHeading)));		
		return new TypeRelation(resultHeading);
	}

	public TypeRelation compileRelationUngroup(TypeRelation sourceType, String attributeName) {
    	Heading sourceHeading = sourceType.getHeading();
		Attribute rvAttribute = sourceHeading.getAttribute(attributeName); 
        if (rvAttribute == null)
            throw new ExceptionSemantic("RS0075: Attribute '" + attributeName + "' not found in " + sourceType);
        Type rvaType = rvAttribute.getType();
        if (!(rvaType instanceof TypeRelation))
            throw new ExceptionSemantic("RS0076: Expected attribute '" + attributeName + "' to be a relation, but got " + rvaType);
        Heading rvaHeading = ((TypeRelation)rvaType).getHeading();
        if (sourceHeading.intersect(rvaHeading).getDegree() > 0)
            throw new ExceptionSemantic("RS0077: Relation-valued attribute '" + attributeName + "' shares attributes with " + sourceType);
        Heading resultType = sourceHeading.unionDisjoint(rvaHeading);
        AttributeMap sourceMap = new AttributeMap(sourceHeading, resultType);
        AttributeMap rvaMap = new AttributeMap(rvaHeading, resultType);
        int indexofRVA = sourceHeading.getIndexOf(attributeName);
        int resultDegree = resultType.getDegree();
		compileInstruction(new OpRelationUngroup(sourceMap, rvaMap, indexofRVA, resultDegree));
		// project out RVA
		SelectAttributes excludeRVA = new SelectAttributes();
		excludeRVA.add(attributeName);
		excludeRVA.setAllBut(true);
		sourceType = compileRelationProject(new TypeRelation(resultType), excludeRVA);
		return sourceType;
	}

	public TypeArray compileOrder(TypeHeading sourceType, SelectOrder orderItems) {
		Heading sourceHeading = sourceType.getHeading();
		compileInstruction(new OpTupleIteratableOrder(new OrderMap(sourceHeading, orderItems)));
		return new TypeArray(sourceHeading);		
	}

	public TypeRelation compileArrayUnorder(TypeArray sourceType) {
		Heading sourceHeading = sourceType.getHeading();
		compileInstruction(new OpArrayToRelation());
		return new TypeRelation(sourceHeading);
	}
	
	public class RelationSubstitute {
		private AnonymousTupleOperator updateOp;
		private TypeRelation sourceType;
		
		/** Begin relation substitute.  
		 * 
		 * This is to be used immediately before an assignment statement, to introduce a tuple's
		 * attributes into the current scope so they can be dereferenced and assigned.
		 * 
		 * @param sourceType
		 */
		public RelationSubstitute(TypeRelation sourceType) {
			this.sourceType = sourceType;
			// Define anonymous function that accepts a tuple as an argument and returns a tuple
			updateOp = new TupleMapOperator(new TypeTuple(sourceType.getHeading()), "Relation UPDATE");
		}
		
		/**
		 * End relation update.
		 * 
		 * This is to be used immediately after an assignment statement.
		 */	
		public TypeRelation endRelationSubstitute() {
			updateOp.end();
			compileInstruction(new OpTupleIteratableMap(updateOp.getOperator()));
			return sourceType;
		}
	}
		
	/** Compile TUPLE FROM relation */
	public TypeTuple compileRelationGetTuple(TypeRelation relationExpression) {
		compileInstruction(new OpRelationGetTuple());
		return new TypeTuple(relationExpression.getHeading());
	}
	
	public class TupleSubstitute {
		private AnonymousTupleOperator updateOp;
		
		/** Begin tuple substitute.  
		 * 
		 * This is to be used immediately before an assignment statement, to introduce a tuple's
		 * attributes into the current scope so they can be dereferenced and assigned.
		 * 
		 * The ValueTuple on the stack will be subject to a non-destructive update via the assignment
		 * statement.  A new ValueTuple of the same TypeTuple will be returned on the stack.
		 * 
		 * @param sourceType
		 */
		public TupleSubstitute(TypeTuple sourceType) {
			// Define anonymous function that accepts a tuple as an argument and returns a tuple
			updateOp = new TupleMapOperator(sourceType, "Tuple UPDATE");
		}
		
		/**
		 * End tuple substitute.
		 * 
		 * This is to be used immediately after an assignment statement.
		 */	
		public TypeTuple endTupleSubstitute() {
			updateOp.end();
			// Invoke anonymous function
			return (TypeTuple)compileEvaluate(updateOp.getOperatorDefinition());		
		}
	}
	
	/** Compile FROM TUPLE.  Return attribute type. */
	public Type compileTupleGetAttribute(TypeTuple sourceType, String attributeName) {
		Heading source = sourceType.getHeading();
		int index = source.getIndexOf(attributeName);
		if (index == -1)
			throw new ExceptionSemantic("RS0078: Attribute '" + attributeName + "' not found.");
		compileInstruction(new OpTupleGetAttribute(index));
		return source.getAttributes().get(index).getType();
	}
	
	/** Compile tuple unwrap.  Return new TypeTuple. */
	public TypeTuple compileTupleUnwrap(TypeTuple sourceType, String attributeName) {
		Attribute tupleAttribute = sourceType.getHeading().getAttribute(attributeName);
		if (tupleAttribute == null)
			throw new ExceptionSemantic("RS0079: Attribute '" + attributeName + "' not in " + sourceType);
		if (!(tupleAttribute.getType() instanceof TypeTuple))
			throw new ExceptionSemantic("RS0080: Expected attribute '" + tupleAttribute.getName() + "' to be a TUPLE but it is a " + tupleAttribute.getType());
		Heading unwrappingAttributeHeading = new Heading();
		unwrappingAttributeHeading.add(tupleAttribute.getName(), tupleAttribute.getType());
		TypeTuple preservedAttributes = new TypeTuple(sourceType.getHeading().minus(unwrappingAttributeHeading));
		compileDuplicate();
		compileReformat(preservedAttributes, sourceType);
		compileSwap();
		compileInstruction(new OpTupleGetAttribute(sourceType.getHeading().getIndexOf(tupleAttribute.getName())));
		sourceType = compileTupleJoin(preservedAttributes, (TypeTuple)tupleAttribute.getType());
		return sourceType;
	}
	
	/** Compile tuple wrap.  Return new TypeTuple. */
	public TypeTuple compileTupleWrap(TypeTuple sourceType, SelectAttributes selection, String name) {
		// Create a tuple type consisting of the attributes being wrapped by new attribute
		TypeTuple wrappingAttributes = new TypeTuple(sourceType.getHeading().project(selection));
		// Create an attribute selection to exclude the wrapped attributes
		SelectAttributes wrappedAttributeRemover = new SelectAttributes();
		wrappedAttributeRemover.setAllBut(true);
		wrappedAttributeRemover.add(wrappingAttributes.getHeading().getAttributes());
		// Push a duplicate of the operand onto the stack
		compileDuplicate();
		// Project the original operand without wrapped attributes
		TypeTuple wrappedAttributesRemoved = compileTupleProject(sourceType, wrappedAttributeRemover);
		// Put the original operand on top of the stack
		compileSwap();
		// Project the original operand to only have the wrapped attributes
		compileReformat(wrappingAttributes, sourceType);
		// Create a new tuple to host the wrapped attributes
		Generator.TupleDefinition extendTuple = new TupleDefinition();
		extendTuple.setTupleAttribute(name, wrappingAttributes);
		TypeTuple wrappedAttribute = extendTuple.endTuple();
		// Extend the operand without wrapped attributes to include the wrapping tuple
		return compileTupleJoin(wrappedAttributesRemoved, wrappedAttribute);		
	}
	
	/** Compile tuple d_union.  Return new TypeTuple. */
	public TypeTuple compileTupleDUnion(TypeTuple leftType, TypeTuple rightType) {
		Heading left = leftType.getHeading();
		Heading right = rightType.getHeading();
		if (left.intersect(right).getDegree() == 0) {
			compileInstruction(new OpTupleJoinDisjoint());
			return new TypeTuple(left.unionDisjoint(right));
		} else
			throw new ExceptionSemantic("RS0081: Attempt to perform disjoint union on tuple types that have attributes in common.");
	}
	
	/** Compile tuple semijoin.  Return new TypeTuple. */
	public TypeTuple compileTupleSemijoin(TypeTuple leftType, TypeTuple rightType) {
		return compileTupleIntersect(leftType, rightType);
	}
	
	/** Compile tuple semiminus.  Return new TypeTuple. */
	public TypeTuple compileTupleSemiminus(TypeTuple leftType, TypeTuple rightType) {
		compileDuplicateUnder();
		return compileTupleMinus(leftType, compileTupleSemijoin(leftType, rightType));
	}
	
	/** Compile tuple compose.  Return new TypeTuple. */
	public TypeTuple compileTupleCompose(TypeTuple leftType, TypeTuple rightType) {
		Heading intersect = leftType.getHeading().intersect(rightType.getHeading());
		if (intersect.getDegree() == 0) {
			compileInstruction(new OpTupleJoinDisjoint());
			return new TypeTuple(leftType.getHeading().unionDisjoint(rightType.getHeading()));
		} else {
			TypeTuple joinType = compileTupleJoin(leftType, rightType);
			TypeTuple composeType = new TypeTuple(joinType.getHeading().minus(intersect));
			compileReformat(composeType, joinType);
			return composeType;
		}
	}

	/** Compile tuple minus.  Return new TypeTuple. */
	public TypeTuple compileTupleMinus(TypeTuple leftType, TypeTuple rightType) {
		Heading left = leftType.getHeading();
		Heading right = rightType.getHeading();
		Heading intersect = left.intersect(right);
		if (intersect.getDegree() == 0) {
			compilePop();
			return leftType;
		} else {
			TypeTuple joinType = compileTupleJoin(leftType, rightType);
			TypeTuple minusType = new TypeTuple(left.minus(right));
			compileReformat(minusType, joinType);
			return minusType;
		}
	}

	public TypeTuple compileTupleIMinus(TypeTuple leftType, TypeTuple rightType) {
		Heading left = leftType.getHeading();
		Heading right = rightType.getHeading();
		Heading intersect = left.intersect(right);
		if (intersect.getDegree() != right.getDegree()) {
			throw new ExceptionSemantic("RS0082: In I_MINUS, the right operand must be a subset of the left operand.");
		} else {
			TypeTuple joinType = compileTupleJoin(leftType, rightType);
			TypeTuple minusType = new TypeTuple(left.minus(right));
			compileReformat(minusType, joinType);
			return minusType;
		}
	}
	
	/** Compile tuple intersect.  Return new TypeTuple. */
	public TypeTuple compileTupleIntersect(TypeTuple leftType, TypeTuple rightType) {
		TypeTuple intersectType = new TypeTuple(leftType.getHeading().intersect(rightType.getHeading()));
		if (intersectType.getHeading().getDegree() > 0) {
			TypeTuple joinType = compileTupleJoin(leftType, rightType);
			compileReformat(intersectType, joinType);
		} else {
			compilePop();
			compilePop();
			compilePush(intersectType.getDefaultValue(this));		
		}
		return intersectType;
	}
	
	/** End tuple extend.  This is invoked immediately after any extend expressions. */
	public TypeTuple endTupleExtend(Extend extend) {
		extend.endExtendDefinition();
		// Initialisation of extend_tuple.
		compilePush(new ValueTuple(this, new TypeTuple(extend.extendedHeading)));
		// Invoke anonymous function
		return (TypeTuple)compileEvaluate(extend.extendOp);
	}

	/** Compile tuple join.  Return new TypeTuple. */
	public TypeTuple compileTupleJoin(TypeTuple leftType, TypeTuple rightType) {
		Heading destination;
		Heading left = leftType.getHeading();
		Heading right = rightType.getHeading();
		if (left.intersect(right).getDegree() > 0) {
			destination = left.union(right);
			compileInstruction(new OpTupleJoin(new JoinMap(destination, left, right)));
		} else {
			compileInstruction(new OpTupleJoinDisjoint());
			destination = left.unionDisjoint(right);
		}
		return new TypeTuple(destination);
	}
	
	/** Compile tuple project.  Return new TypeTuple. */
	public TypeTuple compileTupleProject(TypeTuple sourceType, SelectAttributes attributes) {
		if (attributes.isEverything())
			return sourceType;
		Heading destination = sourceType.getHeading().project(attributes);
		compileInstruction(new OpTupleProject(new AttributeMap(destination, sourceType.getHeading())));
		return new TypeTuple(destination);
	}

	private void operatorDefinition(String fnname) {
		int startLine = 0;
		if (parser != null) {
			BaseASTNode node = parser.getCurrentNode();
			if (node != null) {
				Token token = node.first_token;
				if (token != null)
					startLine = token.beginLine;
			}
		}
		currentOperatorDefinition = new OperatorDefinitionRel(startLine, fnname, currentOperatorDefinition);
	}

	public void addOperator(OperatorDefinition op) {
		parser.addOperator(op);
	}
	
	/** Create an anonymous operator. */
	public OperatorDefinition beginAnonymousOperator() {
		operatorDefinition(null);
		// make sure it's "special", so we don't really see it
		currentOperatorDefinition.setSpecial(true);
		return currentOperatorDefinition;		
	}
	
	public void beginOperator(String fnname) {
		operatorDefinition(fnname);
	}
	
	public OperatorDefinition getCurrentOperatorDefinition() {
		return currentOperatorDefinition;
	}
	
	public String getOperatorDefinitionLineReferenceStack(int lineNumber) {
		StringBuffer out = new StringBuffer();
		OperatorDefinition current = currentOperatorDefinition;
		while (true) {
			if (!current.isSpecial())
				out.append("\tIn " + current.getSignature().toString() + " line " + (lineNumber - current.getStartLine() + 1));
			current = current.getParentOperatorDefinition();
			if (current == null)
				break;
			out.append('\n');
		}
		return out.toString();
	}
	
	public OperatorSignature getCurrentDefinitionSignature() {
		return currentOperatorDefinition.getSignature();
	}
	
	public void setDeclaredReturnType(Type type) {
		currentOperatorDefinition.setDeclaredReturnType(type);
	}
	
	public Type getDeclaredReturnType() {
		return currentOperatorDefinition.getDeclaredReturnType();
	}
	
	public void compileReturnValue(Type returnType) {
		if (!currentOperatorDefinition.hasReturnDeclaration())
			throw new ExceptionSemantic("RS0083: Operator " + currentOperatorDefinition.getSignature() + " has not declared a return type but has defined a return expression.");
		if (returnType instanceof TypeOperator)
			compileInstruction(new OpPreserveContextInValueOperator());
		compileInstruction(new OpReturnValue());
		currentOperatorDefinition.setDefinedReturnValue(true);
	}
	
	public void compileReturn() {
		if (currentOperatorDefinition.hasReturnDeclaration())
			throw new ExceptionSemantic("RS0084: Operator " + currentOperatorDefinition.getSignature() + " has declared a return type, but the RETURN statement is missing an expression of that type.");	
		compileInstruction(new OpReturn());
	}
	
	public void beginParameterDefinitions() {
		// This is a no-op, but here in case needed in the future.
	}
	
	public void defineOperatorParameter(String name, Type type) {
		currentOperatorDefinition.defineParameter(name, type);		
	}
	
	public void endParameterDefinitions() {
		currentOperatorDefinition.getParentOperatorDefinition().defineOperator(currentOperatorDefinition);		
	}
	
	public void endOperator() {
		if (currentOperatorDefinition.hasReturnDeclaration()) {
			if (!currentOperatorDefinition.hasDefinedReturnValue())
				throw new ExceptionSemantic("RS0085: Operator " + currentOperatorDefinition.getSignature() + " has declared a return type, but contains no RETURN statement of that type.");
			compilePush(currentOperatorDefinition.getDeclaredReturnType().getDefaultValue(this));
			compileInstruction(new OpReturnValue());
		} else
			compileInstruction(new OpReturn());
		// Done.  Restore previous definition context.
		currentOperatorDefinition = currentOperatorDefinition.getParentOperatorDefinition();
	}
	
	/** Return true if current operator definition is top level, and should therefore be persistent. */
	public boolean isTopLevelOperator(OperatorDefinition definition) {
		return (definition.getDepth() == interactiveOperatorNestingDepth + 1);
	}
	
	/** Persist the given operator definition. */
	public void persistOperator(OperatorDefinition operator) {
		if (database.isOperatorExists(operator.getSignature()))
			throw new ExceptionSemantic("RS0086: OPERATOR " + operator.getSignature() + " is already in the database.");
		if (operator.getOwner() == null || operator.getOwner().isEmpty())
			operator.setOwner(userRelvarOwner);
		beginAssignment();
		compileInstruction(new OpCreateOperator(operator));
		endAssignment();
	}

	public void dropOperator(OperatorSignature signature) {
		if (!database.isOperatorExists(signature))
			throw new ExceptionSemantic("RS0087: OPERATOR " + signature + " does not exist.");
		String operatorGenerationTypeName = database.getOperatorGenerationTypeName(signature);
		if (operatorGenerationTypeName != null && operatorGenerationTypeName.length() > 0)
			throw new ExceptionSemantic("RS0088: OPERATOR " + signature + " was generated by TYPE " + operatorGenerationTypeName + " and may not be dropped directly.");
		beginAssignment();
		compileInstruction(new OpDropOperator(signature));
		endAssignment();
	}
	
	public class IfStatement {
		private int resolveThisForwardBranch;
		private boolean conditional;
		
		public IfStatement() {
			// if the expression returns false, branch past the if(true) block
			resolveThisForwardBranch = currentOperatorDefinition.getCP();
			conditional = true;
			compileInstruction(new OpBranchIfFalse(0));		// needs to be resolved			
		}

		public void beginElse() {
			// if we executed the if(true) block, we need to jump past the else block
			int pendingForwardBranch = resolveThisForwardBranch;
			resolveThisForwardBranch = currentOperatorDefinition.getCP();
			conditional = false;
			compileInstruction(new OpJump(0));			// needs to be resolved
			// resolve the pending forward branch from the expression test
			// if the test returns false, we need to branch to this point
			compileInstructionAt(new OpBranchIfFalse(currentOperatorDefinition.getCP()), pendingForwardBranch);
		}
		
		public void endIf() {
			// resolve the pending forward branch
			if (conditional)
				compileInstructionAt(new OpBranchIfFalse(currentOperatorDefinition.getCP()), resolveThisForwardBranch);
			else
				compileInstructionAt(new OpJump(currentOperatorDefinition.getCP()), resolveThisForwardBranch);
		}
	}		
	
	public class DoLoop {
		private int head;
		private int resolveThisForwardBranch;

		public DoLoop() {
			head = currentOperatorDefinition.getCP();
		}
		
		public void testDo() {
			// compile branch out of loop if loop test returns false
			resolveThisForwardBranch = currentOperatorDefinition.getCP();
			compileInstruction(new OpBranchIfFalse(0));
		}
		
		public void endDo() {
			// compile jump back to top of loop
			compileInstruction(new OpJump(head));
			// resolve unresolved branch out of loop
			compileInstructionAt(new OpBranchIfFalse(currentOperatorDefinition.getCP()), resolveThisForwardBranch);
		}
	}

	// Anonymous operator of the form OPERATOR(TUPLE x)
	private class TupleProcessOperator extends AnonymousTupleOperator {
		private String contextDescription;
		TupleProcessOperator(TypeTuple source, String contextDescription) {
			super(source, null);
			this.contextDescription = contextDescription;
		}
		public void compileSet(Generator generator, Attribute attribute, int slotDepth, int operatorDepth, int offset) {
			throw new ExceptionFatal("RS0302: Attempt to set attribute '" + attribute.getName() + "' in " + contextDescription + ".");		
		}
	}
	
	public class ForLoop {
		private TupleProcessOperator tupleOperator;
		public void beginForLoop(TypeArray array) {
			tupleOperator = new TupleProcessOperator((TypeTuple)array.getElementType(), "FOR");
		}
		
		public void endForLoop() {
			tupleOperator.end();
			compileInstruction(new OpArrayFor(tupleOperator.getOperator()));
		}
	}
	
	public Type compileGet(Slot slot) {
		slot.compileGet(this);
		return slot.getType();		
	}
	
	public Type compileGet(String refname) {
		return compileGet(findReference(refname));
	}
	
	public Type compileSet(Slot slot) {
		slot.compileSet(this);
		return slot.getType();		
	}
	
	public Type compileSet(String refname) {
		return compileSet(findReference(refname));
	}
	
	public void compileVariableInitialise(Slot slot) {
		slot.compileInitialise(this);
	}
	
	public void compileVariableInitialise(String refname) {
		compileVariableInitialise(findReference(refname));
	}
		
	public void compileArrayGet() {
		compileInstruction(new OpArrayGet());
	}
	
	public void compileArraySet() {
		compileInstruction(new OpArraySet());
	}
	
	public void compileArrayAppend() {
		compileInstruction(new OpArrayAppend());
	}
	
	// EXACTLY (n-adic)
	public void compileExactly(int countOfBooleanExpressions) {
		compileInstruction(new OpExactly(countOfBooleanExpressions));
	}
	
	// AVG (n-adic)
	public void compileNadicAverage(int countOfExpressions) {
		compileInstruction(new OpAverage(countOfExpressions));
	}
	
	public Type compileOperatorInvocation(String opName, Type parmType, Type returnType) {
		OperatorSignature signature = new OperatorSignature(opName);
		signature.addParameterType(parmType);
		signature.setReturnType(returnType);
		compileEvaluate(signature);
		return returnType;
	}
	
	public Type compileOperatorInvocation(String opName, Type leftParmType, Type rightParmType, Type returnType) {
		OperatorSignature signature = new OperatorSignature(opName);
		signature.addParameterType(leftParmType);
		signature.addParameterType(rightParmType);
		signature.setReturnType(returnType);
		compileEvaluate(signature);
		return returnType;
	}
	
	private Type compileComparisonOperatorInvocation(String opName, Type leftType, Type rightType, Type returnType) {
		if (!leftType.canAccept(rightType))
			throw new ExceptionSemantic("RS0388: " + leftType + " is not the same type as " + rightType + ".");
		compileReformat(leftType, rightType);
		return compileOperatorInvocation(opName, leftType, rightType, returnType);
	}
	
	// =
	public Type compileEQ(Type leftType, Type rightType) {
		return compileComparisonOperatorInvocation(BuiltinTypeBuilder.EQUALS, leftType, rightType, TypeBoolean.getInstance());
	}

	// !=
	public Type compileNEQ(Type leftType, Type rightType) {
		return compileComparisonOperatorInvocation(BuiltinTypeBuilder.NOTEQUALS, leftType, rightType, TypeBoolean.getInstance());
	}

	private void checkNotSupportedByTuple(Type leftType, Type rightType) {
		if (leftType instanceof TypeTuple || rightType instanceof TypeTuple)
			throw new ExceptionSemantic("RS0089: Operator not supported by TUPLE.");		
	}
	
	// >=
	public Type compileGTE(Type leftType, Type rightType) {
		checkNotSupportedByTuple(leftType, rightType);
		return compileComparisonOperatorInvocation(BuiltinTypeBuilder.GREATERTHANOREQUALS, leftType, rightType, TypeBoolean.getInstance());
	}

	// <=
	public Type compileLTE(Type leftType, Type rightType) {
		checkNotSupportedByTuple(leftType, rightType);
		return compileComparisonOperatorInvocation(BuiltinTypeBuilder.LESSTHANOREQUALS, leftType, rightType, TypeBoolean.getInstance());
	}

	// >
	public Type compileGT(Type leftType, Type rightType) {
		checkNotSupportedByTuple(leftType, rightType);
		return compileComparisonOperatorInvocation(BuiltinTypeBuilder.GREATERTHAN, leftType, rightType, TypeBoolean.getInstance());
	}

	// <
	public Type compileLT(Type leftType, Type rightType) {
		checkNotSupportedByTuple(leftType, rightType);
		return compileComparisonOperatorInvocation(BuiltinTypeBuilder.LESSTHAN, leftType, rightType, TypeBoolean.getInstance());
	}

	public Object compileSubset(Type leftType, Type rightType) {
		checkNotSupportedByTuple(leftType, rightType);
		return compileComparisonOperatorInvocation(BuiltinTypeBuilder.SUBSET, leftType, rightType, TypeBoolean.getInstance());
	}

	public Object compileSubsetOrEqual(Type leftType, Type rightType) {
		checkNotSupportedByTuple(leftType, rightType);
		return compileComparisonOperatorInvocation(BuiltinTypeBuilder.SUBSETOREQUAL, leftType, rightType, TypeBoolean.getInstance());
	}

	public Object compileSuperset(Type leftType, Type rightType) {
		checkNotSupportedByTuple(leftType, rightType);
		return compileComparisonOperatorInvocation(BuiltinTypeBuilder.SUPERSET, leftType, rightType, TypeBoolean.getInstance());
	}

	public Object compileSupersetOrEqual(Type leftType, Type rightType) {
		checkNotSupportedByTuple(leftType, rightType);
		return compileComparisonOperatorInvocation(BuiltinTypeBuilder.SUPERSETOREQUAL, leftType, rightType, TypeBoolean.getInstance());
	}

	// IN
	public TypeBoolean compileTupleIn(TypeTuple leftType, TypeRelation rightType) {
		compileSwap();
		if (rightType.requiresReformatOf(new TypeRelation(leftType.getHeading())))
			compileInstruction(new OpTupleProject(new AttributeMap(leftType.getHeading(), rightType.getHeading())));
		compileInstruction(new OpTupleInRelation());
		return TypeBoolean.getInstance();
	}
	
	// ||
	public void compileConcatenate() {
		compileInstruction(new OpConcatenate());
	}
	
	// +
	public void compilePlus() {
		compileInstruction(new OpAdd());
	}
	
	// Pop value from stack
	public void compilePop() {
		compileInstruction(new OpPop());
	}
	
	// Duplicate topmost value on stack
	public void compileDuplicate() {
		compileInstruction(new OpDuplicate());
	}
	
	// Duplicate value under topmost on stack.  Topmost is unchanged.
	public void compileDuplicateUnder() {
		compileInstruction(new OpDuplicateUnder());
	}
	
	// Swap top two values on stack
	public void compileSwap() {
		compileInstruction(new OpSwap());
	}
	
	// Push literal Value to stack
	public void compilePush(Value v) {
		compileInstruction(new OpPushLiteral(v));
	}

	public class RelationDefinition {
		private Heading heading;
		private boolean isHeadingExplicit = false;
		
		// Begin relation literal.  If heading is null, first tuple added will define the type.
		public RelationDefinition(Heading heading) {
			this.heading = heading;
			if (heading != null)
				isHeadingExplicit = true;
			compileInstruction(new OpRelationPushLiteral());
		}

		// Add a tuple to the relation definition
		public void addTupleToRelation(TypeTuple tupleType) {
			Heading tupleHeading = tupleType.getHeading();
			if (heading == null)
				heading = tupleHeading;
			else
				if (!heading.canAccept(tupleHeading))
					if (!isHeadingExplicit && tupleHeading.canAccept(heading))
						heading = tupleHeading;
					else
						heading = heading.getMostSpecificCommonSupertype(tupleHeading);
			compileReformat(new TypeTuple(heading), tupleType);
			compileInstruction(new OpRelationLiteralInsertTuple());
		}

		// End relation literal
		public TypeRelation endRelation() {
			return new TypeRelation(heading);
		}
	}
	
	public class TupleDefinition {
		private Heading heading;
		private boolean wildcard = false;
		
		// Begin tuple literal
		public TupleDefinition() {
			 heading = new Heading();
		}
		
		// Define a tuple attribute.  At run-time, the value topmost on the stack will be
		// assigned to this attribute.
		public void setTupleAttribute(String name, Type type) {
			heading.add(name, type);
		}
		
		// End tuple literal.  Return the tuple's type.
		public TypeTuple endTuple() {
			if (wildcard) {
				OperatorDefinition currentOperator = getCurrentOperatorDefinition();
				while (currentOperator != null) {
					OperatorSignature currentOperatorSignature = currentOperator.getSignature();
					int parmCount = currentOperatorSignature.getParmCount();
					for (int parm = 0; parm < parmCount; parm++) {
						String name = currentOperatorSignature.getParameterName(parm);
						// Are we in an open expression context? If so, a parm "%tuple<n>" or "%source_tuple<n>" will exist.
						// Let TUP {*} refer to it.
						if (name.startsWith("%tuple") || name.startsWith("%source_tuple"))
							return (TypeTuple)compileGet(name);
					}
					currentOperator = currentOperator.getParentOperatorDefinition();
				}
			}
			compileInstruction(new OpTuplePushLiteral(heading.getDegree()));
			return new TypeTuple(heading);
		}

		// Identify this as a "wildcard" tuple, i.e., TUPLE {*}, which should close over its scope
		public void setWildcard() {
			wildcard = true;
		}
	}

	// Push delimited string literal to stack
	public void compilePushDelimitedString(String value) {
		compilePush(ValueCharacter.stripDelimited(Generator.this, value));
	}
	
	// Push string literal to stack
	public void compilePush(String value) {
		compilePush(ValueCharacter.select(this, value));		
	}
	
	// Push double literal to stack
	public void compilePush(double value) {
		compilePush(ValueRational.select(this, value));
	}
	
	// Push integer literal to stack
	public void compilePush(long value) {
		compilePush(ValueInteger.select(this, value));		
	}

	// Push boolean literal to stack
	public void compilePush(boolean flag) {
		compilePush(ValueBoolean.select(this, flag));
	}
	
	// Run-time announcement
	public void announce(String s) {
		printStream.println(ValueCharacter.stripDelimitedString(s));
	}
	
	// Compile: write value on top of stack to output stream.
	public void compileWrite(Type type) {
		compileInstruction(new OpWrite(type));
	}
	
	// Compile: write value on top of stack to output stream, followed by newline.
	public void compileWriteln(Type type) {
		compileInstruction(new OpWriteln(type));		
	}
	
	// Compile: write newline to output stream.
	public void compileWritelnNoExpression() {
		compileInstruction(new OpWritelnNoExpression());
	}
	
	// Compile: write value on top of stack to output stream in parsable format.
	public void compileOutput(Type type) {
		compileInstruction(new OpOutput(type));		
	}
	
	// Compile: EXECUTE value on top of stack.
	public void compileExecute() {
		compileInstruction(new OpExecute());
	}

	// SET <attribute> <value>
	public void set(String attribute, String value) {
		EnvironmentSettings.set(this, attribute, value);
	}

	// Compile: make a backup
	public void backup() {
		compileInstruction(new OpBackup());
	}
	
	// Compile: write value on top of stack to console.  For debugging purposes.
	public void compileWrite() {
		compileInstruction(new OpWriteRaw());		
	}
	
	// Compile: write relation on top of stack to console.  For debugging purposes.
	public void compileWriteRelation() {
		compileInstruction(new OpRelationWrite());		
	}

}
