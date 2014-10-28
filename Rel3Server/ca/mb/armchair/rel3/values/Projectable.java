package ca.mb.armchair.rel3.values;

import ca.mb.armchair.rel3.types.AttributeMap;

/** An interface that represents ValueS that support projection using an AttributeMap. */
public interface Projectable {
	public Value project(AttributeMap map);
}
