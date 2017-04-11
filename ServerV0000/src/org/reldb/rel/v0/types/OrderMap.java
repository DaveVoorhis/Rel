package org.reldb.rel.v0.types;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.generator.SelectOrder;
import org.reldb.rel.v0.values.Value;

public class OrderMap {
	
	private int[] map;
	private SelectOrder.Order[] order;
	
	public OrderMap(Heading heading, SelectOrder orderItems) {
		int count = orderItems.getCount();
		map = new int[count];
		order = new SelectOrder.Order[count];
		for (int i=0; i<count; i++) {
			SelectOrder.Item item = orderItems.getItem(i);
			map[i] = heading.getIndexOf(item.getName());
			if (map[i] < 0)
				throw new ExceptionSemantic("RS0255: Attribute " + item.getName() + " is not found in " + heading);
			order[i] = item.getOrder();
		}
	}

	public int[] getMap() {
		return map;
	}
	
	public SelectOrder.Order[] getOrder() {
		return order;
	}
	
	public String toString() {
		String out = "";
		for (int i=0; i<map.length; i++) {
			if (i > 0)
				out += ", ";
			out += map[i] + "=" + order[i];
		}
		return "OrderMap [" + out + "]";
	}

	public boolean isDifferentSortKey(Value[] oldTuple, Value[] newTuple) {
		for (int i=0; i<map.length; i++)
			if (oldTuple[map[i]].compareTo(newTuple[map[i]]) != 0)
				return true;
		return false;
	}
	
}
