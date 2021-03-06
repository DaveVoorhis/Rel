package org.reldb.rel.v0.types;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.values.*;

public class TypeTuple extends TypeHeading {

	private static TypeTuple emptyTuple = new TypeTuple(new Heading());

	public TypeTuple(Heading heading) {
		super(heading);
	}
	
	/** Get an empty tuple type. */
	public static TypeTuple getEmptyTupleType() {
		return emptyTuple;
	}
	
	public Value getDefaultValue(Generator generator) {
		return new ValueTuple(generator, this);
	}

	public String getSignature() {
		return "TUPLE " + heading.getSignature();
	}
	
}
