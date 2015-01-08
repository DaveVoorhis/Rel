package org.reldb.rel.v0.types.builtin;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.types.TypeAlpha;
import org.reldb.rel.v0.types.userdefined.Possrep;
import org.reldb.rel.v0.types.userdefined.PossrepComponent;
import org.reldb.rel.v0.values.*;

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
