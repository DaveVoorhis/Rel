package org.reldb.rel.types.builtin;

import org.reldb.rel.generator.Generator;
import org.reldb.rel.types.TypeAlpha;
import org.reldb.rel.types.userdefined.Possrep;
import org.reldb.rel.types.userdefined.PossrepComponent;
import org.reldb.rel.values.*;

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
