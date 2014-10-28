package ca.mb.armchair.rel3.types.builtin;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.types.TypeAlpha;
import ca.mb.armchair.rel3.types.userdefined.Possrep;
import ca.mb.armchair.rel3.types.userdefined.PossrepComponent;
import ca.mb.armchair.rel3.values.*;

public class TypeInteger extends TypeAlpha {

	public static final String Name = "INTEGER";
	
	private static TypeInteger instance = new TypeInteger();
	
	protected TypeInteger() {
		super(Name);
		new PossrepComponent(new Possrep(this, Name), "VALUE", this);
	}
	
	public static TypeInteger getInstance() {
		return instance;
	}
	
	public boolean isBuiltin() {
		return true;
	}
	
	/** Obtain a default value of this type. */
	public Value getDefaultValue(Generator generator) {
		return ValueInteger.select(generator, 0);
	}

}
