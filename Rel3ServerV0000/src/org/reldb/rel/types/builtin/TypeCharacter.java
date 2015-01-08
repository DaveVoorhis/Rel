package org.reldb.rel.types.builtin;

import org.reldb.rel.generator.Generator;
import org.reldb.rel.types.TypeAlpha;
import org.reldb.rel.types.userdefined.Possrep;
import org.reldb.rel.types.userdefined.PossrepComponent;
import org.reldb.rel.values.*;

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
