package org.reldb.rel.v0.values;

import org.reldb.rel.v0.types.AttributeMap;

/** An interface that represents ValueS that support projection using an AttributeMap. */
public interface Projectable {
	public Value project(AttributeMap map);
}
