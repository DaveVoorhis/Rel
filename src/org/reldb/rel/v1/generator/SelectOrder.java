package org.reldb.rel.v1.generator;

import java.util.Vector;

/** Specification of attribute orders for ORDER */
public class SelectOrder {
	
	public enum Order {ASC, DESC};
	
	public static class Item {
		String attribute;
		Order order;
		public Item(String attribute, Order order) {
			this.attribute = attribute;
			this.order = order;
		}
		public String getName() {
			return attribute;
		}
		public Order getOrder() {
			return order;
		}
	}

	private Vector<Item> items = new Vector<Item>();
	
	public void add(String name, Order order) {
		items.add(new Item(name, order));
	}
	
	public int getCount() {
		return items.size();
	}

	public Item getItem(int index) {
		return items.get(index);
	}
	
}
