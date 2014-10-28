package ca.mb.armchair.rel3.storage.catalog;

import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.storage.relvars.RelvarGlobal;
import ca.mb.armchair.rel3.storage.relvars.RelvarHeading;
import ca.mb.armchair.rel3.storage.relvars.RelvarMetadata;
import ca.mb.armchair.rel3.types.*;
import ca.mb.armchair.rel3.types.builtin.TypeBoolean;
import ca.mb.armchair.rel3.types.builtin.TypeCharacter;
import ca.mb.armchair.rel3.types.builtin.TypeInteger;
import ca.mb.armchair.rel3.generator.SelectAttributes;
import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;

public class RelvarCatalogMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	// This must parallel the ValueTuple created by getCatalogTupleIterator() in RelDatabase.
	static Heading getNewHeading() {
		Heading heading = new Heading();
		heading.add("Name", TypeCharacter.getInstance());
		heading.add("Definition", TypeCharacter.getInstance());
		heading.add("Owner", TypeCharacter.getInstance());
		heading.add("CreationSequence", TypeInteger.getInstance());
		heading.add("isVirtual", TypeBoolean.getInstance());
		return heading;
	}
	
	static RelvarHeading getNewKeyDefinition() {
		SelectAttributes attributes = new SelectAttributes();
		attributes.add("Name");
		RelvarHeading keyDefinition = new RelvarHeading(getNewHeading());
		keyDefinition.addKey(attributes);
		return keyDefinition;
	}
	
	public RelvarCatalogMetadata(RelDatabase database) {
		super(database, getNewKeyDefinition(), RelDatabase.systemOwner);
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarCatalog(database);
	}	
	
	public void dropRelvar(RelDatabase database) {
		throw new ExceptionSemantic("RS0198: The " + Catalog.relvarCatalog + " relvar may not be dropped.");		
	}	
}
