package org.reldb.rel.v1.types;

import org.reldb.rel.exceptions.ExceptionSemantic;

/** Abstract base class for TypeTuple and TypeRelation. */
public abstract class TypeHeading extends TypeAbstract {

	protected Heading heading;
	
	/** Create new TypeHeading from a given Heading. */
	public TypeHeading(Heading heading) {
		this.heading = heading;
	}

	public boolean requiresReformatOf(Type type) {
		if (!(type instanceof TypeHeading))
			throw new ExceptionSemantic("RS0260: Expected something with a heading but got a " + type + " in an operator invocation.");
		return heading.requiresReformatOf(((TypeHeading)type).getHeading());
	}
	
	public boolean canAccept(Type type) {
		if (!(type instanceof TypeHeading))
			throw new ExceptionSemantic("RS0261: Expected something with a heading but got a " + type + " in an operator invocation.");
		return heading.canAccept(((TypeHeading)type).getHeading());
	}
	
	public Heading getHeading() {
		return heading;
	}
}
