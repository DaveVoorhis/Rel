package ca.mb.armchair.rel3.types.builtin;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.types.TypeAlpha;
import ca.mb.armchair.rel3.types.userdefined.Possrep;
import ca.mb.armchair.rel3.types.userdefined.PossrepComponent;
import ca.mb.armchair.rel3.values.*;

public class TypeBoolean extends TypeAlpha {

	public static final String Name = "BOOLEAN";
	
	private static TypeBoolean instance = new TypeBoolean();
	
	protected TypeBoolean() {
		super(Name);
		new PossrepComponent(new Possrep(this, Name), "VALUE", this);
	}
	
	public static TypeBoolean getInstance() {
		return instance;
	}
	
	public boolean isBuiltin() {
		return true;
	}
	
	/** Obtain a default value of this type. */
	public Value getDefaultValue(Generator generator) {
		return ValueBoolean.select(generator, false);
	}

}
