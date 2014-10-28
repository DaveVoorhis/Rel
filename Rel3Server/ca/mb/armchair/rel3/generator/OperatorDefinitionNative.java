package ca.mb.armchair.rel3.generator;

import java.util.HashSet;
import java.util.Iterator;

import ca.mb.armchair.rel3.types.Type;
import ca.mb.armchair.rel3.debuginfo.DebugInfo;
import ca.mb.armchair.rel3.exceptions.*;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.storage.relvars.RelvarHeading;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.Operator;

public abstract class OperatorDefinitionNative extends OperatorDefinitionAbstract {
	
	/** Ctor for operator definition. */
	public OperatorDefinitionNative(String name) {
		super(name);
	}
	
	/** Ctor for operator definition. */
	public OperatorDefinitionNative(String name, Type[] parameters) {
		super(name);
		for (Type type: parameters)
			getSignature().addParameterType(type);
	}

	/** Get parameter count. */
	int getParmCount() {
		return getSignature().getParmCount();
	}
	
	private void nimp() {
		ExceptionFatal e = new ExceptionFatal("Invalid use of OperatorDefinitionNative.");
		e.printStackTrace();
		throw e;
	}
	
	public void compile(DebugInfo errorContext, Instruction o) {
		nimp();
	}

	public void compileAt(DebugInfo errorContext, Instruction o, int address) {
		nimp();
	}

	public void defineConstant(String name, Type type) {
		nimp();
	}

	public void defineOperator(OperatorDefinition definition) {
		nimp();
	}

	public void removeOperator(OperatorSignature signature) {
		nimp();
	}
	
	public void defineParameter(String name, Type type) {
		nimp();
	}

	public void defineRelvarPrivate(RelDatabase database, String name, RelvarHeading keydef) {
		nimp();
	}

	public void defineSlot(String name, SlotScoped slot) {
		nimp();
	}

	public void defineVariable(String name, Type type) {
		nimp();
	}

	public OperatorDefinition findOperator(OperatorSignature signature) {
		nimp();
		return null;
	}

	public Slot findReference(String name) {
		nimp();
		return null;
	}

	public int getCP() {
		nimp();
		return 0;
	}

	public int getDepth() {
		nimp();
		return 0;
	}

	public int getExecutableSize() {
		nimp();
		return 0;
	}

	public Operator getOperator() {
		nimp();
		return null;
	}

	public OperatorDefinition getOperator(OperatorSignature signature) {
		nimp();
		return null;
	}

	public Iterator<OperatorDefinition> getOperators() {
		return null;
	}
	
	public void getPossibleTargetOperators(HashSet<OperatorSignature> targets, OperatorSignature invocationSignature) {
	}
	
	public OperatorDefinition getParentOperatorDefinition() {
		nimp();
		return null;
	}

	public Slot getReference(String name) {
		nimp();
		return null;
	}
	
	public int getStartLine() {
		nimp();
		return 0;
	}
	
	public boolean isDefined(String name) {
		nimp();
		return false;
	}

}
