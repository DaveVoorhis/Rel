package ca.mb.armchair.rel3.client;

public class ScalarType extends Type {

	private String name;
	
	ScalarType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return getName();
	}
	
}
