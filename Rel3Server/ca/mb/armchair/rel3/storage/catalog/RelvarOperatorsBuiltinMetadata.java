package ca.mb.armchair.rel3.storage.catalog;

import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.storage.relvars.RelvarGlobal;
import ca.mb.armchair.rel3.storage.relvars.RelvarHeading;
import ca.mb.armchair.rel3.storage.relvars.RelvarMetadata;
import ca.mb.armchair.rel3.types.*;
import ca.mb.armchair.rel3.types.builtin.TypeCharacter;
import ca.mb.armchair.rel3.generator.SelectAttributes;
import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;

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
