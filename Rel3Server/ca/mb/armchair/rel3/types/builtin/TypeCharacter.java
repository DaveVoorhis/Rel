package ca.mb.armchair.rel3.types.builtin;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.types.TypeAlpha;
import ca.mb.armchair.rel3.types.userdefined.Possrep;
import ca.mb.armchair.rel3.types.userdefined.PossrepComponent;
import ca.mb.armchair.rel3.values.*;

public class TypeCharacter extends TypeAlpha {

	public static final String Name = "CHARACTER";
	
	private static TypeCharacter instance = new TypeCharacter();
	
	protected TypeCharacter() {
		super(Name);
		new PossrepComponent(new Possrep(this, Name), "VALUE", this);
	}
	
	public static TypeCharacter getInstance() {
		return instance;
	}
	
	public boolean isBuiltin() {
		return true;
	}
	
	/** Obtain a default value of this type. */
	public Value getDefaultValue(Generator generator) {
		return ValueCharacter.select(generator, "");
	}

}
