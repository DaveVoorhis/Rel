package org.reldb.rel.storage.catalog;

import org.reldb.rel.generator.SelectAttributes;
import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.storage.relvars.RelvarGlobal;
import org.reldb.rel.storage.relvars.RelvarHeading;
import org.reldb.rel.types.*;
import org.reldb.rel.types.builtin.TypeCharacter;
import org.reldb.rel.types.builtin.TypeInteger;

public class RelvarTypesMetadata extends RelvarSystemMetadata {
	public static final long serialVersionUID = 0;
	
	static Heading getTypeReferenceHeading() {
		Heading subtypeHeading = new Heading();
		subtypeHeading.add("Name", TypeCharacter.getInstance());
		return subtypeHeading;
	}
	
	static Heading getNewHeading() {
		Heading heading = new Heading();
		heading.add("Name", TypeCharacter.getInstance());
		heading.add("Definition", TypeCharacter.getInstance());
		heading.add("Owner", TypeCharacter.getInstance());
		heading.add("CreationSequence", TypeInteger.getInstance());
		heading.add("Language", TypeCharacter.getInstance());
		heading.add("Subtypes", new TypeRelation(getTypeReferenceHeading()));
		heading.add("Supertypes", new TypeRelation(getTypeReferenceHeading()));
		return heading;
	}
	
	static RelvarHeading getNewKeyDefinition() {
		SelectAttributes attributes = new SelectAttributes();
		attributes.add("Name");
		RelvarHeading keyDefinition = new RelvarHeading(getNewHeading());
		keyDefinition.addKey(attributes);
		return keyDefinition;
	}
	
	public RelvarTypesMetadata(RelDatabase database, String name) {
		super(database, name, getNewKeyDefinition());
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarTypes(name, database, this);
	}
	
}
