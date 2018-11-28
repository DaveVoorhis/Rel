package org.reldb.rel.v0.storage.relvars;

import java.util.Vector;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.Renaming;
import org.reldb.rel.v0.types.Type;

public class RelvarHeading {

	private Vector<SelectAttributes> keys = new Vector<SelectAttributes>(); 	
	private Heading heading;
	
	public RelvarHeading(Heading heading) {
		this.heading = heading;
	}
	
	public Heading getHeading() {
		return heading;
	}
	
	public String getKeyString() {
		StringBuffer outstr = new StringBuffer();
		if (keys.size() == 0)
			outstr.append("KEY {ALL BUT}");
		else
			for (SelectAttributes attributes: keys) {
				outstr.append("KEY {");
				outstr.append(attributes.toString());
				outstr.append("} ");
			}
		return outstr.toString().trim();		
	}
	
	public String toString() {
		return getKeyString();
	}

	/** Return the number of KEY definitionS in this RelvarHeading. */
	public int getKeyCount() {
		return keys.size();
	}
	
	/** Return the ith KEY definition. */
	public SelectAttributes getKey(int n) {
		return keys.get(n);
	}
	
	/** Add a KEY definition as a set of attribute selections. */
	public void addKey(SelectAttributes attributes) {
		for (String attribute: attributes.getNames())
			if (heading.getIndexOf(attribute) == -1)
				throw new ExceptionSemantic("RS0225: Attribute '" + attribute + "' does not exist in heading " + heading);
		attributes.makeNamesExplicit(heading);
		for (SelectAttributes existingKey: keys) {
			if (attributes.isASubsetOf(existingKey))
				throw new ExceptionSemantic("RS0226: KEY {" + attributes + "} is a subset of KEY {" + existingKey + "}");
			if (existingKey.isASubsetOf(attributes))
				throw new ExceptionSemantic("RS0227: KEY {" + existingKey + "} is a subset of KEY {" + attributes + "}");
		}
		keys.add(attributes);
	}

	@SuppressWarnings("unchecked")
	public Vector<SelectAttributes> getKeys() {
		return (Vector<SelectAttributes>) keys.clone();
	}

	public void setKeys(Vector<SelectAttributes> keys) {
		this.keys = keys;
	}

	public boolean isKeyUsing(String attributeName) {
		if (keys.size() == 0)
			return true;
		for (SelectAttributes key: keys)
			if (key.getNames().contains(attributeName))
				return true;
		return false;
	}

	public void renameAttribute(String oldAttributeName, String newAttributeName) {
		Renaming renaming = new Renaming();
		renaming.addRename(oldAttributeName, newAttributeName);
		heading.rename(renaming);
		if (keys.size() == 0)
			return;
		for (SelectAttributes key: keys)
			key.rename(oldAttributeName, newAttributeName);
	}

	public void changeTypeAttribute(String attributeName, Type newType) {
		heading.changeTypeOfAttribute(attributeName, newType);
	}

	public void insertAttributes(Heading addHeading) {
		heading = heading.unionDisjoint(addHeading);
	}

	public void dropAttribute(String attributeName) {
		if (isKeyUsing(attributeName))
			throw new ExceptionSemantic("RS0438: Attribute '" + attributeName + "' is referenced in " + getKeyString() + ".");
		heading.remove(attributeName);
	}

}
