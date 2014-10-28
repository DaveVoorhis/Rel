package ca.mb.armchair.rel3.types;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.values.*;

public class TypeRelation extends TypeHeading {

	private static TypeRelation emptyType = new TypeRelation(new Heading());
	
	/** Create new relation type from a given Heading. */
	public TypeRelation(Heading heading) {
		super(heading);
	}
	
	/** Get TABLE_DUM or TABLE_DEE relation type. */
	public static TypeRelation getEmptyRelationType() {
		return emptyType;
	}
	
	public Value getDefaultValue(Generator generator) {
		return ValueRelation.getDum(generator);
	}

	public String getSignature() {
		return "RELATION " + heading.getSignature();
	}
}
