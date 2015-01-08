package org.reldb.rel.generator;

import org.reldb.rel.types.Type;

public abstract class OperatorDefinitionAbstract implements OperatorDefinition {
	private OperatorSignature signature;
	private boolean isSpecial = false;
	private String sourceCode = "";
	private String owner = "";
	private String createdByType = "";
	private References references = new References();
	private boolean definedReturnValue = false;
	
	public OperatorDefinitionAbstract(String name) {
		signature = new OperatorSignature(name);
	}

	/** Set whether or not this operator has defined a return value via a RETURN statement. */
	public void setDefinedReturnValue(boolean flag) {
		definedReturnValue = flag;
	}
	
	/** Return true if this operator has defined a return value via a RETURN statement. */
	public boolean hasDefinedReturnValue() {
		return definedReturnValue;
	}
	
	/** Set references. */
	public void setReferences(References refs) {
		references = refs;
	}
	
	/** Get references. */
	public References getReferences() {
		return references;
	}
	
	/** Set owner. */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	/** Get owner. */
	public String getOwner() {
		return owner;
	}
	
	/** Set source code. */
	public void setSourceCode(String source) {
		sourceCode = source;
	}
	
	/** Get source code. */
	public String getSourceCode() {
		return sourceCode;
	}
	
	/** Set defining type name. */
	public void setCreatedByType(String typeName) {
		createdByType = typeName;
	}
	
	/** Get defining type name. */
	public String getCreatedByType() {
		return createdByType;
	}
	
	/** Get the signature of this operator. */
	public OperatorSignature getSignature() {
		return signature;
	}
	
	/** Mark this as a 'special' hidden operator definition. */
	public void setSpecial(boolean flag) {
		isSpecial = flag;
	}
	
	/** True if this is a 'special' hidden operator definition. */
	public boolean isSpecial() {
		return isSpecial;
	}

	/** Set operator return type. */
	public void setDeclaredReturnType(Type type) {
		signature.setReturnType(type);
	}
	
	/** Return this operator's declared return type.  Return null if it hasn't been declared. */
	public Type getDeclaredReturnType() {
		return signature.getReturnType();
	}
	
	/** True if this operator has a return value. */
	public boolean hasReturnDeclaration() {
		return (signature.getReturnType() != null);
	}
	
	public String toString() {
		return signature.toString();
	}
}
