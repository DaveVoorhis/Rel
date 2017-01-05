package org.reldb.rel.v0.types;

import java.util.*;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.generator.SelectAttributes;

public class Heading implements Comparable<Heading> {
	
	private Vector<Attribute> attributes = new Vector<Attribute>();
	
	/** Create a new Heading. */
	public Heading() {
	}

	/** Create a new Heading based on an existing Heading. */
	public Heading(Heading oldHeading) {
		attributes.addAll(oldHeading.getAttributes());
	}
	
	/** Create a new Heading based on an existing TypeTuple. */
	public Heading(TypeTuple tupleType) {
		this(tupleType.getHeading());
	}

	/** Create a new Heading based on an existing TypeRelation. */
	public Heading(TypeRelation relationType) {
		this(relationType.getHeading());
	}
	
	/** Create a new Heading by the union (aka join) of this heading with another. Throw
	 * an exception if there are attributes in common. */
	public Heading unionDisjoint(Heading rightHeading) {
		Heading joinedHeading = new Heading(this);
		for (Attribute rightAttribute: rightHeading.attributes) {
			if (getIndexOf(rightAttribute.getName()) >= 0)
				throw new ExceptionSemantic("RS0242: Attribute '" + rightAttribute.getName() + "' is found in both operands.");
			joinedHeading.attributes.add(rightAttribute);
		}
		return joinedHeading;
	}

	/** Create a new Heading by the union (aka join) of this heading with another heading.  Common attributes
	 * appear once in the result Heading. */
	public Heading union(Heading rightHeading) {
		Heading joinedHeading = new Heading(this);
		for (Attribute rightAttribute: rightHeading.attributes) {
			Attribute leftAttribute = getAttribute(rightAttribute.getName());
			if (leftAttribute != null) {
				if (!leftAttribute.getType().canAccept(rightAttribute.getType()))
					throw new ExceptionSemantic("RS0243: An attribute named '" + rightAttribute.getName() + "' is found in both operands but differs in type.");
			} else
				joinedHeading.attributes.add(rightAttribute);
		}
		return joinedHeading;
	}
	
	/** Create a new Heading by intersecting this heading with another heading. */
	public Heading intersect(Heading rightHeading) {
		Heading intersectedHeading = new Heading();
		for (Attribute leftAttribute: attributes) {
			Attribute rightAttribute = rightHeading.getAttribute(leftAttribute.getName());
			if (rightAttribute != null) {
				if (!leftAttribute.getType().canAccept(rightAttribute.getType()))
					throw new ExceptionSemantic("RS0244: An attribute named '" + rightAttribute.getName() + "' is found in both operands but differs in type.");
				intersectedHeading.attributes.add(rightAttribute);
			}
		}
		return intersectedHeading;
	}
	
	/** Create a new Heading by returning the left heading minus common attributes in the right heading. */
	public Heading minus(Heading rightHeading) {
		Heading minusHeading = new Heading();
		for (Attribute leftAttribute: attributes) {
			Attribute rightAttribute = rightHeading.getAttribute(leftAttribute.getName());
			if (rightAttribute == null)
				minusHeading.attributes.add(leftAttribute);
			else if (!leftAttribute.getType().canAccept(rightAttribute.getType()))
				throw new ExceptionSemantic("RS0245: An attribute named '" + rightAttribute.getName() + "' is found in both operands but differs in type.");
		}
		return minusHeading;
	}
	
	/** Project this Heading into a new Heading given an AttributeSelection. */
	public Heading project(SelectAttributes selection) {
		Heading heading;
		if (selection.isAllBut()) {
			heading = new Heading(this);
			for (String name: selection.getNames())
				heading.remove(name);
		} else {
			heading = new Heading();
			for (String name: selection.getNames()) {
				Attribute attribute = getAttribute(name);
				if (attribute == null)
					throw new ExceptionSemantic("RS0246: Attribute '" + name + "' not found.");
				heading.attributes.add(attribute);
			}
		}
		return heading;
	}
	
	/** Remove a given attribute. */
	public void remove(String name) {
		int index = getIndexOf(name);
		if (index < 0)
			throw new ExceptionSemantic("RS0247: Attribute '" + name + "' not found.");
		attributes.remove(index);
	}
	
	/** Rename a given attribute. */
	private void rename(Attribute oldAttribute, String newName) {
		int index = getIndexOf(oldAttribute.getName());
		if (index == -1)
			throw new ExceptionSemantic("RS0248: Attribute '" + oldAttribute.getName() + "' not found.");
		if (getAttribute(newName) != null)
			throw new ExceptionSemantic("RS0249: Attribute '" + newName + "' has already been defined.");
		attributes.set(index, new Attribute(newName, oldAttribute.getType()));
	}
	
	/** Rename an attribute.  Return false if no match found. */
	public boolean rename(String from, String to) {
		Attribute oldAttribute = getAttribute(from);
		if (oldAttribute == null)
			return false;
		rename(oldAttribute, to);
		return true;
	}
	
	/** Rename all attributes with a given prefix.  Return false if no match found. */
	public boolean renamePrefix(String from, String to) {
		boolean renamed = false;
		for (Attribute attribute: attributes)
			if (attribute.getName().startsWith(from)) {
				rename(attribute, to + attribute.getName().substring(from.length()));
				renamed = true;
			}
		return renamed;
	}
	
	/** Rename all attributes with a given suffix.  Return false if no match found. */
	public boolean renameSuffix(String from, String to) {
		boolean renamed = false;
		for (Attribute attribute: attributes)
			if (attribute.getName().endsWith(from)) {
				rename(attribute, attribute.getName().substring(0, attribute.getName().length() - from.length()) + to);
				renamed = true;
			}
		return renamed;
	}
	
	public void add(String attributeName, Type attributeType) {
		if (getAttribute(attributeName) != null)
			throw new ExceptionSemantic("RS0250: Attribute '" + attributeName + "' has already been defined.");
		attributes.add(new Attribute(attributeName, attributeType));
	}

	public void add(Attribute attribute) {
		add(attribute.getName(), attribute.getType());
	}

	/** Return the attribute of a given name.  Return null if not found. */
	public Attribute getAttribute(String name) {
		for (Attribute attribute: attributes)
			if (attribute.getName().equals(name))
				return attribute;
		return null;
	}
	
	/** Return the index of a given attribute.  Return -1 if not found. */
	public int getIndexOf(String name) {
		int index = 0;
		for (Attribute attribute: attributes) {
			if (attribute.getName().equals(name))
				return index;
			index++;
		}
		return -1;
	}
	
	public Vector<Attribute> getAttributes() {
		return attributes;
	}
	
	/** Return true if this Heading can accept the given Heading. */
	public boolean canAccept(Heading heading) {
		if (getDegree() != heading.getDegree())
			return false;
		for (Attribute attribute: attributes) {
			Attribute foreignAttribute = heading.getAttribute(attribute.getName());
			if (foreignAttribute == null)
				return false;
			if (!attribute.getType().canAccept(foreignAttribute.getType()))
				return false;
		}
		return true;
	}
	
	/** Return true if the source must be reformatted for this to receive it. */
	public boolean requiresReformatOf(Heading source) {
		// Type failure requires (at the very least!) a reformat.
		if (!canAccept(source))
			return true;
		int i = 0;
		for (Attribute attribute: attributes) {
			if (attribute.getName().compareTo(source.attributes.get(i).getName()) != 0)
				return true;
			else if (attribute.getType().requiresReformatOf(source.attributes.get(i).getType()))
				return true;
			i++;
		}
		return false;
	}

	public Heading getMostSpecificCommonSupertype(Heading tupleHeading) {
		if (getDegree() != tupleHeading.getDegree())
			throw new ExceptionSemantic("RS0251: Heading degrees do not match.");
		Heading newHeading = new Heading();
		for (int i=0; i<attributes.size(); i++) {
			Attribute attribute = attributes.get(i);
			Type t1 = attribute.getType();
			String name = attribute.getName();
			Attribute attribute2 = tupleHeading.getAttribute(name);
			if (attribute2 == null)
				throw new ExceptionSemantic("RS0252: Attribute '" + name + "' not found.");
			Type t2 = attribute2.getType();
			if (!(t1 instanceof TypeAlpha))
				throw new ExceptionFatal("RS0380: getMostSpecificCommonSupertype: heading1 element " + i + " not TypeAlpha or derivative.");
			if (!(t2 instanceof TypeAlpha))
				throw new ExceptionFatal("RS0381: getMostSpecificCommonSupertype: heading2 element " + i + " not TypeAlpha or derivative.");
			TypeAlpha mostSpecificCommonSupertype = ((TypeAlpha)t1).getMostSpecificCommonSupertype((TypeAlpha)t2);
			if (mostSpecificCommonSupertype == null || mostSpecificCommonSupertype.getSignature().equals("ALPHA"))
				throw new ExceptionSemantic("RS0253: ALPHA is the most specific common supertype of " + t1.getSignature() + " and " + t2.getSignature() + ", but ALPHA can't be selected.");			
			newHeading.add(name, mostSpecificCommonSupertype);
		}
		return newHeading;
	}

	public void changeTypeOfAttribute(String attributeName, Type newType) {
		for (int i=0; i<attributes.size(); i++) {
			if (attributes.get(i).getName().equals(attributeName)) {
				attributes.set(i, new Attribute(attributeName, newType));
				return;
			}
		}
		throw new ExceptionSemantic("RS0252: Attribute '" + attributeName + "' not found.");
	}

	public int compareTo(Heading heading) {
		if (canAccept(heading))
			return 0;
		else
			return 1;
	}

	public boolean equals(Object object) {
		return (compareTo((Heading)object) == 0);
	}
	
	public int getDegree() {
		return attributes.size();
	}

	// Get list of names from the ith attribute onward
	public String getNameList(int i) {
		String out = null;
		int attributeNumber = 0;
		for (Attribute attribute: attributes) {
			if (attributeNumber >= i)
				out = (out == null) ? attribute.getName() : (out + ", " + attribute.getName());
			attributeNumber++;
		}
		return ((out == null) ? "" : out);
	}

	public String getNameList() {
		return getNameList(0);
	}
	
	public String getSpecification() {
		String out = null;
		for (Attribute attribute: attributes)
			out = (out == null) ? attribute.toString() : (out + ", " + attribute.toString());
		return ((out == null) ? "" : out);		
	}
	
	public String getSignature() {
		return "{" + getSpecification() + "}";
	}

	public String toString() {
		return getSignature();
	}

	public String getRandomFreeAttributeName() {
		String uuid = UUID.randomUUID().toString();
		while (getIndexOf(uuid) >= 0)
			uuid = UUID.randomUUID().toString();
		return uuid;
	}
}
