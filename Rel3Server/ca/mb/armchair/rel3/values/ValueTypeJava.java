package ca.mb.armchair.rel3.values;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.types.*;

public abstract class ValueTypeJava extends ValueAbstract implements Type {
	
	private static final long serialVersionUID = 1L;

	public ValueTypeJava(Generator generator) {
		super(generator);
	}
	
	public String getSignature() {
		return getTypeName();
	}
	
	public boolean canAccept(Type t) {
		return getClass().getCanonicalName().equals(t.getClass().getCanonicalName());
	}
	
	public String getTypeName() {
		return getClass().getSimpleName();
	}
	
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
