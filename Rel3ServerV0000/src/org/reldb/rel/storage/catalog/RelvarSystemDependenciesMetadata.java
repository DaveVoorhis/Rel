package org.reldb.rel.storage.catalog;

import org.reldb.rel.generator.SelectAttributes;
import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.storage.relvars.RelvarGlobal;
import org.reldb.rel.storage.relvars.RelvarHeading;
import org.reldb.rel.types.Heading;
import org.reldb.rel.types.builtin.TypeCharacter;

public class RelvarSystemDependenciesMetadata extends RelvarSystemMetadata {
	public static final long serialVersionUID = 0;
	
	static Heading getNewHeading() {
		Heading heading = new Heading();
		heading.add("Object", TypeCharacter.getInstance());
		heading.add("Uses", TypeCharacter.getInstance());
		return heading;
	}
	
	static RelvarHeading getNewKeyDefinition() {
		SelectAttributes attributes = new SelectAttributes();
		attributes.add("Object");
		attributes.add("Uses");
		RelvarHeading keyDefinition = new RelvarHeading(getNewHeading());
		keyDefinition.addKey(attributes);
		return keyDefinition;
	}
	
	public RelvarSystemDependenciesMetadata(RelDatabase database, String name) {
		super(database, name, getNewKeyDefinition());
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarSystemDependencies(name, database, this);
	}

}
