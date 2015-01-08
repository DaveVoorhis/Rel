package org.reldb.rel.v0.generator;

import java.util.*;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.types.TypeAlpha;

/** This class provides mechanisms to define an object signature and convert it to a string. */
public class OperatorSignature implements Comparable<OperatorSignature> {

	private Vector<Type> parameterTypes;
	private Vector<String> parameterNames = null;
	private Type returnType;
	private String name;
	
	public OperatorSignature(String name) {
		parameterTypes = new Vector<Type>();
		this.name = name;
	}
	
	/* Return true if an operator with this signature can be invoked by the specified invocation signature. */
	public boolean canBeInvokedBy(OperatorSignature signature) {
		if (name == null && signature.name != null)
			return false;
		if (name != null && signature.name == null)
			return false;
		if (name != null && signature.name != null && !name.equals(signature.name))
			return false;
		if (parameterTypes.size() != signature.parameterTypes.size())
			return false;
		for (int i=0; i<parameterTypes.size(); i++)
			if (!parameterTypes.get(i).canAccept(signature.parameterTypes.get(i)))
				return false;
		return true;
	}

	/** Return the ranked difference (based on inheritance) between this signature and the specified invocation signature. */ 
	public int getInvocationDistance(OperatorSignature signature) {
		if (parameterTypes.size() != signature.parameterTypes.size())
			return Integer.MAX_VALUE;
		int totalDistance = 0;
		for (int i=0; i<parameterTypes.size(); i++) {
			if (!signature.parameterTypes.get(i).canAccept(parameterTypes.get(i)))
				return Integer.MAX_VALUE;
			if (!(parameterTypes.get(i) instanceof TypeAlpha))
				totalDistance += 0;
			else {
				TypeAlpha t = (TypeAlpha)signature.getParameterType(i);
				while (t != null && !t.getTypeName().equals(((TypeAlpha)parameterTypes.get(i)).getTypeName())) {
					totalDistance += 1;
					t = t.getSupertype();
				}
			}
		}
		return totalDistance;
	}
	
	public String getName() {
		return name;
	}
	
	/** Return true if this signature might reference more than one operator when taking inheritance and subtyping into account. */
	public boolean isPossiblyDynamicDispatch() {
		for (Type parmType: parameterTypes)
			if (parmType instanceof TypeAlpha)
				return true;
		return false;
	}
	
	public void addParameter(String name, Type t) {
		if (name == null)
			throw new ExceptionFatal("RS0303: Attempt to define null parameter name.");
		if (t == null)
			throw new ExceptionFatal("RS0304: Attempt to define null parameter type.");
		if (parameterNames == null)
			parameterNames = new Vector<String>();
		parameterNames.add(name);
		parameterTypes.add(t);
	}
	
	public void addParameterType(Type t) {
		parameterTypes.add(t);
		if (t == null)
			throw new ExceptionFatal("RS0305: Attempt to define null parameter type.");
	}
	
	public int getParmCount() {
		return parameterTypes.size();
	}
	
	public void setParameterType(int index, Type t) {
		if (t == null)
			throw new ExceptionFatal("RS0306: Attempt to define null parameter type.");
		while (parameterTypes.size() <= index)
			parameterTypes.add(null);
		parameterTypes.set(index, t);
	}
	
	public Type getParameterType(int index) {
		return parameterTypes.get(index);
	}
	
	public String getParameterName(int index) {
		return parameterNames.get(index);
	}
	
	public Type[] getParameterTypes() {
		return parameterTypes.toArray(new Type[0]);
	}
	
	public void setReturnType(Type type) {
		returnType = type;
	}
	
	public Type getReturnType() {
		return returnType;
	}
		
	/** Comparison for signature lookup. */
	public int compareTo(OperatorSignature o) {
		return toRelLookupString().compareToIgnoreCase(o.toRelLookupString());
	}
	
	public boolean equals(Object o) {
		return compareTo((OperatorSignature)o) == 0;
	}
	
	public int hashCode() {
		return toRelLookupString().hashCode();
	}

	public boolean isAnonymous() {
		return (name == null);
	}
	
	/** Convert to string for display purposes.  Includes return type. */
	public String toString() {
		if (getReturnType() == null)
			return toRelLookupString();
		return toRelLookupString() + " RETURNS " + getReturnType().getSignature();
	}
	
	// Construct signature.
	private String toFinalSignature(String parmSignature) {
		return (isAnonymous() ? "" : getName()) + "(" + parmSignature + ")";
	}
	
	/** Convert to string for native lookup purposes.  Does not include return type. */
	public String toNativeLookupString() {
		String parmSignature = "";
		for (Type type: parameterTypes)
			parmSignature += (parmSignature.length() == 0) ? type.getClass().getCanonicalName() : (", " + type.getClass().getCanonicalName()); 
		return toFinalSignature(parmSignature);
	}
	
	/** Convert to string for Rel operator lookup purposes.  Does not include return type. */
	public String toRelLookupString() {
		String parmSignature = "";
		for (Type type: parameterTypes)
			parmSignature += (parmSignature.length() == 0) ? type.getSignature() : (", " + type.getSignature()); 
		return toFinalSignature(parmSignature);
	}
	
	/** Get operator declaration */
	public String getOperatorDeclaration() {
		String parmSignature = "";
		int nameIndex = 0;
		for (Type type: parameterTypes) {
			String parmDef = parameterNames.get(nameIndex) + " " + type.getSignature();
			parmSignature += (parmSignature.length() == 0) ? parmDef : (", " + parmDef); 			
			nameIndex++;
		}
		return "OPERATOR " + toFinalSignature(parmSignature) + ((returnType != null) ? " RETURNS " + returnType.getSignature() : "");
	}
	
    /** Given a string, convert any non identifier-allowable character to
     * an underscore. */
    private static String classNameMung(String s) {
        char [] strbuf = s.toCharArray();
        for (int i=0; i<strbuf.length; i++)
            if (!Character.isJavaIdentifierPart(strbuf[i]))
                strbuf[i] = '_';
        return new String(strbuf);
    }
	
    /** Get name for use in producing a Java class name. */
    public String getClassSignature() {
        String s = "op_" + getName() + "_";
        for (int i=0; i<getParmCount(); i++)
            s += "_" + classNameMung(getParameterType(i).toString());
        return s;
    }

}
