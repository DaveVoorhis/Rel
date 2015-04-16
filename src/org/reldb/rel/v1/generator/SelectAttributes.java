package org.reldb.rel.v1.generator;

import java.util.Vector;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v1.types.*;

public class SelectAttributes {
	
	private Vector<String> names = new Vector<String>();
	private boolean isAllBut = false;
	
	public SelectAttributes() {}
	
	public SelectAttributes(Heading heading) {
		add(heading.getAttributes());
	}
	
	public void setAllBut(boolean flag) {
		isAllBut = flag;
	}
	
	public boolean isAllBut() {
		return isAllBut;
	}
	
	public boolean isEverything() {
		return isAllBut && names.size() == 0;
	}
	
	/** Turns any 'ALL BUT' selection into an equivalent set of explicit names. */
	public void makeNamesExplicit(Heading heading) {
		if (isAllBut) {
			Vector<String> newNames = new Vector<String>();
			for (Attribute attribute: heading.getAttributes())
				if (!names.contains(attribute.getName()))
					newNames.add(attribute.getName());
			names = newNames;
			isAllBut = false;
		}
	}

	// True if a given SelectAttributes is a subset of another
	public boolean isASubsetOf(SelectAttributes key2) {
		if (getNames().size() == 0)
			return true;
		for (String attribute: getNames())
			if (key2.getNames().indexOf(attribute) < 0)
				return false;
		return true;
	}
	
	public void add(String name) {
		if (names.contains(name))
			throw new ExceptionSemantic("RS0098: Duplicate reference to " + name + ".");
		names.add(name);
	}
	
	public void add(Vector<Attribute> attributes) {
		for (Attribute attribute: attributes)
			add(attribute.getName());
	}
	
	public Vector<String> getNames() {
		return names;
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer();
		out.append((isAllBut) ? "ALL BUT " : "");
		boolean atLeastOneProcessed = false;
		for (String name: names) {
			if (atLeastOneProcessed)
				out.append(", ");
			else
				atLeastOneProcessed = true;				
			out.append(name);
		}
		return out.toString();
	}
}
