package org.reldb.rel.storage.relvars;

import java.util.Vector;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.generator.SelectAttributes;
import org.reldb.rel.types.Heading;

public class RelvarHeading {

	private Vector<SelectAttributes> keys = new Vector<SelectAttributes>(); 	
	private Heading heading;
	
	public RelvarHeading(Heading heading) {
		this.heading = heading;
	}
	
	public Heading getHeading() {
		return heading;
	}
	
	public String toString() {
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
}
