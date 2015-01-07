package ca.mb.armchair.rel3.generator;

import java.util.*;

/** Reference to global relvars, operators, and/or types. */
public class References {
	HashSet<String> relvars = new HashSet<String>();
	HashSet<String> operators = new HashSet<String>();
	HashSet<String> types = new HashSet<String>();
	
	public void addReferenceToRelvar(String relvarName) {
		relvars.add(relvarName);
	}
	
	public void removeReferenceToRelvar(String relvarName) {
		relvars.remove(relvarName);
	}
	
	public void addReferenceToOperator(String operatorName) {
		operators.add(operatorName);
	}
	
	public void removeReferenceToOperator(String operatorName) {
		operators.remove(operatorName);
	}
	
	public void addReferenceToType(String typeName) {
		types.add(typeName);
	}
	
	public void removeReferenceToType(String typeName) {
		types.remove(typeName);
	}
	
	public Collection<String> getReferencedRelvars() {
		return relvars;
	}
	
	public Collection<String> getReferencedOperators() {
		return operators;
	}
	
	public Collection<String> getReferencedTypes() {
		return types;
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer();
		for (String s: relvars)
			out.append("VAR " + s + "\n");
		for (String s: operators)
			out.append("OPERATOR " + s + "\n");
		for (String s: types)
			out.append("TYPE " + s + "\n");
		return out.toString();
	}
}
