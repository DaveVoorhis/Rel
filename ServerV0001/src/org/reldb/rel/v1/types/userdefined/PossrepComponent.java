package org.reldb.rel.v1.types.userdefined;

import org.reldb.rel.v1.types.Type;

public class PossrepComponent {
	private String name;
	private Type type;
	private int componentIndex;
	
	public PossrepComponent(Possrep possrep, int componentIndex, String name, Type type) {
		this.componentIndex = componentIndex;
		this.name = name;
		this.type = type;
		possrep.addComponent(this);
	}
	
	public PossrepComponent(Possrep possrep, String name, Type type) {
		this(possrep, possrep.getNextComponentIndex(), name, type);
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public int getComponentIndex() {
		return componentIndex;
	}
}
