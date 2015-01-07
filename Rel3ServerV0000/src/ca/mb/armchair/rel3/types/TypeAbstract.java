package ca.mb.armchair.rel3.types;

import ca.mb.armchair.rel3.generator.Generator;

public abstract class TypeAbstract implements Type {

	/** Return the name of the Java class that represents values of this type. */
    public String getValueClassname(Generator generator) {
    	return getDefaultValue(generator).getClass().getSimpleName();
    }
	
	public boolean requiresReformatOf(Type source) {
		return false;
	}
	
	public String toString() {
		return getSignature();
	}
	
}
