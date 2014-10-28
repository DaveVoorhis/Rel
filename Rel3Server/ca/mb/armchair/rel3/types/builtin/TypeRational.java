package ca.mb.armchair.rel3.types.builtin;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.types.TypeAlpha;
import ca.mb.armchair.rel3.types.userdefined.Possrep;
import ca.mb.armchair.rel3.types.userdefined.PossrepComponent;
import ca.mb.armchair.rel3.values.*;

public class TypeRational extends TypeAlpha {

	public static final String Name = "RATIONAL";
	
	private static TypeRational instance = new TypeRational();
	
	protected TypeRational() {
		super(Name);
		new PossrepComponent(new Possrep(this, Name), "VALUE", this);
	}
	
	public static TypeRational getInstance() {
		return instance;
	}
	
	public boolean isBuiltin() {
		return true;
	}
	
	/** Obtain a default value of this type. */
	public Value getDefaultValue(Generator generator) {
		return ValueRational.select(generator, 0.0);
	}

}
