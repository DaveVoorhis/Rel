package org.reldb.rel.v0.types.builtin;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.types.TypeAlpha;
import org.reldb.rel.v0.types.userdefined.Possrep;
import org.reldb.rel.v0.types.userdefined.PossrepComponent;
import org.reldb.rel.v0.values.*;

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
