package org.reldb.rel.types;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.generator.Generator;
import org.reldb.rel.values.Value;
import org.reldb.rel.values.ValueTuple;

/** Defines a join mapping between a two source TypeTupleS into a destination TypeTuple. */
public class JoinMap {
	private int degree;
	private int[] leftCommonToRight;
	private boolean[] rightExclude;
	private int commonDegree;
	
	/** Create a JoinMap to join tuples of the source types, which may contain common attributes,
	 * to the destination type. */
	public JoinMap(Heading destinationType, Heading leftType, Heading rightType) {
		degree = destinationType.getDegree();
		Heading intersection = leftType.intersect(rightType);
		if (intersection.getDegree() == 0)
			throw new ExceptionFatal("RS0382: " + leftType + " and " + rightType + " have no common attributes, and therefore need not be mapped.");
		leftCommonToRight = new int[leftType.getDegree()];
		rightExclude = new boolean[rightType.getDegree()];
		int leftAttributeIndex = 0;
		for (Attribute attribute: leftType.getAttributes()) {
			int rightAttributeIndex = rightType.getIndexOf(attribute.getName());
			leftCommonToRight[leftAttributeIndex++] = rightAttributeIndex;
			if (rightAttributeIndex >= 0)
				rightExclude[rightAttributeIndex] = true;
		}
		commonDegree = 0;
		for (int i=0; i<leftCommonToRight.length; i++)
			commonDegree += (leftCommonToRight[i] >= 0) ? 1 : 0; 
	}

	/** Return true if the left and right tuples have matching common attributes. */
	public boolean isJoinable(ValueTuple left, ValueTuple right) {
		Value[] leftValues = left.getValues();
		Value[] rightValues = right.getValues();
		for (int i=0; i<leftCommonToRight.length; i++)
			if (leftCommonToRight[i] >= 0 && !(leftValues[i].compareTo(rightValues[leftCommonToRight[i]])==0))
				return false;
		return true;
	}

	/** Return a new tuple consisting of a left tuple's common attributes. */
	public ValueTuple getLeftTupleCommon(Generator generator, ValueTuple left) {
		Value[] leftValues = left.getValues();
		Value[] leftCommonValues = new Value[commonDegree];
		int leftIndex = 0;
		for (int i=0; i<leftCommonToRight.length; i++)
			if (leftCommonToRight[i] >= 0)
				leftCommonValues[leftIndex++] = leftValues[i];
		return new ValueTuple(generator, leftCommonValues);
	}
	
	/** Return a new tuple consisting of a right tuple's common attributes. */
	public ValueTuple getRightTupleCommon(Generator generator, ValueTuple right) {
		Value[] rightValues = right.getValues();
		Value[] rightCommonValues = new Value[commonDegree];
		int rightIndex = 0;
		for (int i=0; i<leftCommonToRight.length; i++)
			if (leftCommonToRight[i] >= 0)
				rightCommonValues[rightIndex++] = rightValues[leftCommonToRight[i]];
		return new ValueTuple(generator, rightCommonValues);
	}
	
	/** Create a new tuple by a join of the left and right ValueTupleS.  
	 * Assume that common attributes have matching values, as (perhaps) 
	 * determined by isJoinable(). */
	public ValueTuple join(Generator generator, ValueTuple left, ValueTuple right) {
		Value[] leftValues = left.getValues();
		Value[] rightValues = right.getValues();
		Value[] target = new Value[degree];
		System.arraycopy(leftValues, 0, target, 0, leftValues.length);		
		int destinationIndex = leftValues.length;
		for (int i=0; i<rightValues.length; i++)
			if (!rightExclude[i])
				target[destinationIndex++] = rightValues[i];
		return new ValueTuple(generator, target);
	}

	/** Create a new tuple by a join of the left and right ValueTupleS.  
	 * Throw an exception if common attributes do not have matching values. */
	public ValueTuple joinChecked(Generator generator, ValueTuple left, ValueTuple right) {
		Value[] leftValues = left.getValues();
		Value[] rightValues = right.getValues();
		Value[] target = new Value[degree];
		int destinationIndex = 0;
		for (int i=0; i<leftCommonToRight.length; i++) {
			if (leftCommonToRight[i] >= 0 && !(leftValues[i].compareTo(rightValues[leftCommonToRight[i]])==0))
				throw new ExceptionSemantic("RS0254: Common attributes differ in value.");
			target[destinationIndex++] = leftValues[i];
		}
		for (int i=0; i<rightValues.length; i++)
			if (!rightExclude[i])
				target[destinationIndex++] = rightValues[i];
		return new ValueTuple(generator, target);
	}

	public String toString() {
		String outLR = null;
		for (int i=0; i<leftCommonToRight.length; i++)
			if (leftCommonToRight[i] >= 0)
				outLR = ((outLR==null) ? "" : outLR + ", ") + i + "=" + leftCommonToRight[i];
		String outR = "";
		for (int i=0; i<rightExclude.length; i++)
			outR += ((rightExclude[i]) ? "x" : "_");
		return "JoinMap commonLR=[" + ((outLR == null) ? "" : outLR) + "] excludeR=[" + outR + "]";
	}
}
