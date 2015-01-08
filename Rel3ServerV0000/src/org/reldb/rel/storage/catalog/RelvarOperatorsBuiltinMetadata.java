package org.reldb.rel.storage.catalog;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.generator.SelectAttributes;
import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.storage.relvars.RelvarGlobal;
import org.reldb.rel.storage.relvars.RelvarHeading;
import org.reldb.rel.storage.relvars.RelvarMetadata;
import org.reldb.rel.types.*;
import org.reldb.rel.types.builtin.TypeCharacter;

public class RelvarOperatorsBuiltinMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	static Heading getNewHeading() {
		Heading heading = new Heading();
		heading.add("Name", TypeCharacter.getInstance());
		return heading;
	}
	
	static RelvarHeading getNewKeyDefinition() {
		SelectAttributes attributes = new SelectAttributes();
		attributes.add("Name");
		RelvarHeading keyDefinition = new RelvarHeading(getNewHeading());
		keyDefinition.addKey(attributes);
		return keyDefinition;
	}
	
	public RelvarOperatorsBuiltinMetadata(RelDatabase database) {
		super(database, getNewKeyDefinition(), RelDatabase.systemOwner);
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarOperatorsBuiltin(database);
	}

	public void dropRelvar(RelDatabase database) {
		throw new ExceptionSemantic("RS0202: The " + Catalog.relvarOperatorsBuiltin + " relvar may not be dropped.");		
	}	
}
