package ca.mb.armchair.rel3.types;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.values.*;

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
