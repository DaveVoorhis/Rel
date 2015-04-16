package org.reldb.rel.v1.types.builtin;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.types.TypeAlpha;
import org.reldb.rel.v1.types.userdefined.Possrep;
import org.reldb.rel.v1.types.userdefined.PossrepComponent;
import org.reldb.rel.v1.values.*;

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
