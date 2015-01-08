package org.reldb.rel.generator;

import java.util.HashSet;
import java.util.Iterator;

import org.reldb.rel.debuginfo.DebugInfo;
import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.storage.relvars.RelvarHeading;
import org.reldb.rel.types.Type;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;
import org.reldb.rel.vm.Operator;

public interface OperatorDefinition {

	/** Compile execution of this operator using a given Generator. */
	void compileCall(Generator generator);

	/** Compile evaluation of this operator using a given Generator. */
	Type compileEvaluate(Generator generator);

	/** Execute this operator immediately. */
	void call(Context context);
	
	/** Evaluate this operator immediately. */
	void evaluate(Context context);
	
	/** Return true if this operator has a return declaration. */
	boolean hasReturnDeclaration();

	/** Get this operator's signature. */
	OperatorSignature getSignature();

	/** Set owner. */
	void setOwner(String owner);
	
	/** Get owner. */
	String getOwner();
	
	/** Set source code. */
	void setSourceCode(String source);
	
	/** Get source code. */
	String getSourceCode();
	
	/** Set references. */
	void setReferences(References refs);
	
	/** Get references. */
	References getReferences();

	/** Set defining type name. */
	void setCreatedByType(String typeName);
	
	/** Get defining type name. */
	String getCreatedByType();
	
	/** Mark this as a 'special' hidden operator definition. */
	void setSpecial(boolean flag);
	
	/** True if this is a 'special' hidden operator definition. */
	boolean isSpecial();

	/** Get the line number this definition starts on. */
	int getStartLine();
	
	/** Get static depth. */
	int getDepth();
	
	/** Set operator return type. */
	void setDeclaredReturnType(Type type);
	
	/** Return this operator's declared return type.  Return null if it hasn't been declared. */
	Type getDeclaredReturnType();

	/** Set whether or not this operator has defined a return value via a RETURN statement. */
	void setDefinedReturnValue(boolean flag);
	
	/** Return true if this operator has defined a return value via a RETURN statement. */
	boolean hasDefinedReturnValue();
	
	/** Get parent operator definition.  Null if this is the root operator. */
	OperatorDefinition getParentOperatorDefinition();

	/** Compile an Instruction. */
	void compile(DebugInfo errorContext, Instruction o);
	
	/** Compile an Instruction at a given address. */
	void compileAt(DebugInfo errorContext, Instruction o, int address);
	
	/** Return current compilation address. */
	int getCP();
	
	/** Get executable code. */
	Operator getOperator();
	
	/** Get size of executable code. */
	int getExecutableSize();
	
	/** Return true if a variable, parameter, or slot exists. */
	boolean isDefined(String name);
	
	/** Define a slot. */
	void defineSlot(String name, SlotScoped slot);
	
	/** Define a variable. */
	void defineVariable(String name, Type type);
	
	/** Define a constant. */
	void defineConstant(String name, Type type);
	
	/** Define a parameter. */
	void defineParameter(String name, Type type);
	
	/** Define a private relvar. */
	void defineRelvarPrivate(RelDatabase database, String name, RelvarHeading keydef);
	
	/** Return a reference at this nesting level only. */
	Slot findReference(String name);
	
	/** Get the location of a variable, parameter or other identifier.  Return null if it doesn't exist. */
	Slot getReference(String name);
	
	/** Define an operator. */
	void defineOperator(OperatorDefinition definition);
	
	/** Remove an operator. */
	void removeOperator(OperatorSignature signature);
	
	/** Return an operator at this nesting level only. */
	OperatorDefinition findOperator(OperatorSignature signature);
	
	/** Return an operator at any nesting level. */
	OperatorDefinition getOperator(OperatorSignature signature);
	
	/** Return this definition's primary language. */
	String getLanguage();

	/** Get iterator over operators in this definition. */
	Iterator<OperatorDefinition> getOperators();
	
	/** Get possible target operators for a given invocation signature. */
	void getPossibleTargetOperators(HashSet<OperatorSignature> targets, OperatorSignature invocationSignature);
}
