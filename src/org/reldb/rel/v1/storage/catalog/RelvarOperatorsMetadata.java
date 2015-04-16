package org.reldb.rel.v1.storage.catalog;

import org.reldb.rel.v1.generator.SelectAttributes;
import org.reldb.rel.v1.storage.RelDatabase;
import org.reldb.rel.v1.storage.relvars.RelvarGlobal;
import org.reldb.rel.v1.storage.relvars.RelvarHeading;
import org.reldb.rel.v1.types.*;
import org.reldb.rel.v1.types.builtin.TypeCharacter;
import org.reldb.rel.v1.types.builtin.TypeInteger;

public class RelvarOperatorsMetadata extends RelvarSystemMetadata {
	public static final long serialVersionUID = 0;
	
	static Heading getNewImplementationHeading() {
		Heading heading = new Heading();
		heading.add("Signature", TypeCharacter.getInstance());
		heading.add("ReturnsType", TypeCharacter.getInstance());
		heading.add("Definition", TypeCharacter.getInstance());
		heading.add("Language", TypeCharacter.getInstance());
		heading.add("CreatedByType", TypeCharacter.getInstance());		
		heading.add("Owner", TypeCharacter.getInstance());
		heading.add("CreationSequence", TypeInteger.getInstance());
		return heading;
	}
	
	static Heading getNewHeading() {
		Heading heading = new Heading();
		heading.add("Name", TypeCharacter.getInstance());
		heading.add("Implementations", new TypeRelation(getNewImplementationHeading()));
		return heading;
	}
	
	static RelvarHeading getNewKeyDefinition() {
		SelectAttributes attributes = new SelectAttributes();
		attributes.add("Name");
		RelvarHeading keyDefinition = new RelvarHeading(getNewHeading());
		keyDefinition.addKey(attributes);
		return keyDefinition;
	}
	
	public RelvarOperatorsMetadata(RelDatabase database, String name) {
		super(database, name, getNewKeyDefinition());
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarOperators(name, database, this);
	}
	
}
