package ca.mb.armchair.rel3.values;

import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;

public abstract class TupleFoldFirstIsIdentity extends TupleFold {

	private String cardinalityZeroError;
	
	public TupleFoldFirstIsIdentity(String cardinalityZeroError, TupleIterator iterator, int attributeIndex) {
		super(iterator, attributeIndex);
		this.cardinalityZeroError = cardinalityZeroError;
	}

	@Override
	public Value getIdentity() {
		if (!hasNext())
			throw new ExceptionSemantic("RS0263: " + cardinalityZeroError);
		return next().getValues()[getAttributeIndex()];
	}

}
