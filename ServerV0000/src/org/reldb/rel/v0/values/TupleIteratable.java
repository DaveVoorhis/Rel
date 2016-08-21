package org.reldb.rel.v0.values;

import org.reldb.rel.v0.types.AttributeMap;
import org.reldb.rel.v0.types.OrderMap;

public interface TupleIteratable extends Value {
	public TupleIterator iterator();
	public TupleIteratable map(TupleMap tupleMap);
	public Value project(AttributeMap map);
	public Value sort(OrderMap map);
}
