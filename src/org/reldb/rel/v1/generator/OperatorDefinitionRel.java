package org.reldb.rel.v1.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v1.debuginfo.DebugInfo;
import org.reldb.rel.v1.storage.*;
import org.reldb.rel.v1.storage.relvars.RelvarHeading;
import org.reldb.rel.v1.types.*;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;
import org.reldb.rel.v1.vm.Operator;
import org.reldb.rel.v1.vm.instructions.core.OpInvoke;

/** This class captures information about the Rel operator currently being defined,
 * including its generated code.
 * 
 * @author dave
 *
 */
public class OperatorDefinitionRel extends OperatorDefinitionAbstract {
	
	private OperatorDefinition parent;	
	private Operator operator;
	private HashMap<OperatorSignature, OperatorDefinition> operators;
	private HashMap<String, Slot> slots;
	private int startLine;

	/** Ctor for operator definition. */
	public OperatorDefinitionRel(int startLine, String operatorName, OperatorDefinition parentDefinition) {
		super(operatorName);
		this.startLine = startLine;
		parent = parentDefinition;
		operator = new Operator((parent == null) ? 0 : parent.getDepth() + 1);
		operators = new HashMap<OperatorSignature, OperatorDefinition>();
		slots = new HashMap<String, Slot>();
	}
	
	/** Get primary language. */
	public String getLanguage() {
		return "Rel";
	}
	
	/** Get the line number this definition starts on. */
	public int getStartLine() {
		return startLine;
	}
	
	/** Get static depth. */
	public int getDepth() {
		return operator.getDepth();
	}
	
	/** Get parent operator definition.  Null if this is the root operator. */
	public OperatorDefinition getParentOperatorDefinition() {
		return parent;
	}

	/** Compile an Instruction. */
	public void compile(DebugInfo errorContext, Instruction o) {
		o.setDebugInfo(errorContext);
		operator.compile(o);
	}
	
	/** Compile an Instruction at a given address. */
	public void compileAt(DebugInfo errorContext, Instruction o, int address) {
		o.setDebugInfo(errorContext);
		operator.compileAt(address, o);
	}
	
	/** Return current compilation address. */
	public int getCP() {
		return operator.size();
	}
	
	/** Get executable code. */
	public Operator getOperator() {
		return operator;
	}
	
	/** Get size of executable code. */
	public int getExecutableSize() {
		return operator.size();
	}
	
	/** Return true if a variable, parameter, or slot exists. */
	public boolean isDefined(String name) {
		return slots.containsKey(name);
	}

	private void checkDefined(String name) {
		if (isDefined(name))
			throw new ExceptionSemantic("RS0095: " + name + " is already defined in operator " + getSignature());		
	}
	
	/** Define a slot. */
	public void defineSlot(String name, SlotScoped slot) {
		checkDefined(name);
		slots.put(name, slot);
	}
	
	private void defineVariable(String name, SlotScoped slot) {
		defineSlot(name, slot);
		operator.setVariableCount(operator.getVariableCount() + 1);		
	}
	
	/** Define a variable. */
	public void defineVariable(String name, Type type) {
		defineVariable(name, new Variable(operator.getDepth(), operator.getVariableCount(), type));
	}
	
	/** Define a constant. */
	public void defineConstant(String name, Type type) {
		defineVariable(name, new Constant(operator.getDepth(), operator.getVariableCount(), type));
	}
	
	/** Define a parameter. */
	public void defineParameter(String name, Type type) {
		defineSlot(name, new Parameter(operator.getDepth(), operator.getParameterCount(), type));
		getSignature().addParameter(name, type);
		operator.setParameterCount(operator.getParameterCount() + 1);
	}
	
	/** Define a private relvar. */
	public void defineRelvarPrivate(RelDatabase database, String name, RelvarHeading keydef) {
		defineVariable(name, database.createPrivateRelvar(operator.getDepth(), operator.getVariableCount(), keydef));		
	}
	
	/** Return a reference at this nesting level only. */
	public Slot findReference(String name) {
		return slots.get(name);
	}
	
	/** Get the location of a variable, parameter or other identifier.  Return null if it doesn't exist. */
	public Slot getReference(String name) {
		OperatorDefinition definition = this;
		while (definition != null) {
			Slot slot = definition.findReference(name);
			if (slot != null)
				return slot;
			definition = definition.getParentOperatorDefinition();
		}
		return null;
	}
	
	/** Return true if an operator exists. */
	private boolean isOperatorDefined(OperatorSignature signature) {
		return operators.containsKey(signature);
	}
	
	/** Define an operator. */
	public void defineOperator(OperatorDefinition definition) {
		OperatorSignature signature = definition.getSignature();
		if (signature.isAnonymous())
			return;
		if (isOperatorDefined(signature))
			throw new ExceptionSemantic("RS0096: Operator " + signature + " is already defined.");
		operators.put(signature, definition);
	}
	
	/** Remove an operator. */
	public void removeOperator(OperatorSignature signature) {
		operators.remove(signature);
	}
	
	/** Return an operator at this nesting level only. */
	public OperatorDefinition findOperator(OperatorSignature signature) {
		return operators.get(signature);
	}
	
	/** Return an operator. */
	public OperatorDefinition getOperator(OperatorSignature signature) {
		OperatorDefinition definition = this;
		while (definition != null) {
			OperatorDefinition operator = definition.findOperator(signature);
			if (operator != null)
				return operator;
			definition = definition.getParentOperatorDefinition();
		}
		return null;		
	}

	public Iterator<OperatorDefinition> getOperators() {
		return operators.values().iterator();
	}
	
	public void getPossibleTargetOperators(HashSet<OperatorSignature> targets, OperatorSignature invocationSignature) {
		OperatorDefinition definition = this;
		while (definition != null) {
			Iterator<OperatorDefinition> i = definition.getOperators();
			if (i != null)
				while (i.hasNext()) {
					OperatorDefinition operator = i.next();
					OperatorSignature signature = operator.getSignature();
					if (signature.canBeInvokedBy(invocationSignature))
						targets.add(signature);
				}
			definition = definition.getParentOperatorDefinition();
		}		
	}

	public void compileCall(Generator generator) {
		generator.compileInstruction(new OpInvoke(getOperator()));
		if (hasReturnDeclaration())
			generator.compilePop();
	}

	private void checkHasReturn() {
		if (!hasReturnDeclaration())
			throw new ExceptionSemantic("RS0097: Attempt to evaluate " + getSignature() + " which does not have a return value.");		
	}
	
	public Type compileEvaluate(Generator generator) {
		generator.compileInstruction(new OpInvoke(getOperator()));
		checkHasReturn();
		return getDeclaredReturnType();
	}

	public void call(Context context) {
		(new OpInvoke(getOperator())).execute(context);
		if (hasReturnDeclaration())
			context.pop();
	}

	public void evaluate(Context context) {
		checkHasReturn();
		(new OpInvoke(getOperator())).execute(context);
	}
	
}
