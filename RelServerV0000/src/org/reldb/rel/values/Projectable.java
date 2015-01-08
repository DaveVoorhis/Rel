package org.reldb.rel.values;

import org.reldb.rel.types.AttributeMap;

/** An interface that represents ValueS that support projection using an AttributeMap. */
public interface Projectable {
	public Value project(AttributeMap map);
}
