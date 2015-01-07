package ca.mb.armchair.rel3.types;

import java.io.Serializable;

import ca.mb.armchair.rel3.exceptions.*;
import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.values.*;

/** Defines a mapping between matching attributes in differing HeadingS, and methods for
 * copying common attributes from one tuple to another. */
public class AttributeMap implements Serializable {
	private static final long serialVersionUID = 1L;

	private int[] map;
	private AttributeMap[] nestmap;
	
	/** Create an AttributeMap to map attributes of the 'from' type to tuples of the 'to' type. 
	 * 
	 * The set of 'to' attributes must be a subset of 'from' or an exception will be thrown. 
	 */
	public AttributeMap(Heading to, Heading from) {
		map = new int[to.getDegree()];
		nestmap = new AttributeMap[to.getDegree()];
		int toAttributeIndex = 0;
		for (Attribute attribute: to.getAttributes()) {
			int attributeIndex = from.getIndexOf(attribute.getName());
			if (attributeIndex < 0)
				throw new ExceptionSemantic("RS0240: Attribute " + attribute.getName() + " not found in " + from);
			Attribute fromAttribute = from.getAttributes().get(attributeIndex);
			if (!attribute.getType().canAccept(fromAttribute.getType()))
				throw new ExceptionSemantic("RS0241: Attribute " + attribute.getName() + " mismatch between " + attribute.getType() + " and " + fromAttribute.getType());
			if (attribute.getType().requiresReformatOf(fromAttribute.getType()))
				nestmap[toAttributeIndex] = new AttributeMap(((TypeHeading)attribute.getType()).getHeading(), ((TypeHeading)fromAttribute.getType()).getHeading());
			map[toAttributeIndex++] = attributeIndex;
		}
	}

	/** Use this map to assign values from the 'from' tuple to the given tuple's array of ValueS. */
	public void assign(Value[] values, ValueTuple from) {
		Value[] source = from.getValues();
		for (int i=0; i<map.length; i++) {
			if (nestmap[i] != null)
				values[map[i]] = ((Projectable)source[i]).project(nestmap[i]);
			else
				values[map[i]] = source[i];
		}
	}
	
	/** Use this map to project the 'from' tuple to a new tuple.  
	 * 'From' is a superset of the target. */
	public ValueTuple project(Generator generator, ValueTuple from) {
		Value[] source = from.getValues();
		Value[] target = new Value[map.length];
		for (int i=0; i<target.length; i++) {
			if (nestmap[i] != null)
				target[i] = ((Projectable)source[map[i]]).project(nestmap[i]);
			else
				target[i] = source[map[i]];
		}
		return new ValueTuple(generator, target);		
	}
	
	public String toString() {
		String out = null;
		for (int i=0; i<map.length; i++)
			if (nestmap[i] == null)
				out = ((out==null) ? "" : out + ", ") + (i + "=" + map[i]);
			else
				out = ((out==null) ? "" : out + ", ") + (i + "=" + map[i] + " (" + nestmap[i] + ")");				
		return "AttributeMap [" + ((out == null) ? "" : out) + "]";
	}
}
