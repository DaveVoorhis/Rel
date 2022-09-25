package org.reldb.rel.v0.values;

import java.util.Comparator;

import org.reldb.rel.v0.generator.SelectOrder;
import org.reldb.rel.v0.types.OrderMap;

public  class Sorter implements Comparator<Value> {
	private int map[];
	private SelectOrder.Order order[];
	
	public Sorter(OrderMap orderMap) {
		map = orderMap.getMap();
		order = orderMap.getOrder();
	}
	
	public int compare(Value t1, Value t2) {
		Value[] v1 = ((ValueTuple)t1).getValues();
		Value[] v2 = ((ValueTuple)t2).getValues();
		for (int i=0; i<map.length; i++) {
			int attributeIndex = map[i];
			int c = v1[attributeIndex].compareTo(v2[attributeIndex]);
			if (c != 0)
				return (order[i] == SelectOrder.Order.ASC) ? c : -c;
		}
		return 0;
	}
}
