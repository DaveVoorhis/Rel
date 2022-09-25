package org.reldb.rel.v0.storage.catalog;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.RelvarGlobal;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.relvars.RelvarMetadata;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.types.builtin.TypeBoolean;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;

public class RelvarCatalogMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	// This must parallel the ValueTuple created by getCatalogTupleIterator() in RelDatabase.
	static Heading getNewHeading(Generator generator) {
		Heading heading = new Heading();
		heading.add("Name", TypeCharacter.getInstance());
		heading.add("Definition", TypeCharacter.getInstance());
		heading.add("Owner", TypeCharacter.getInstance());
		heading.add("CreationSequence", TypeInteger.getInstance());
		heading.add("isVirtual", TypeBoolean.getInstance());
		heading.add("isExternal", TypeBoolean.getInstance());
		heading.add("Attributes", generator.findType("NonScalar"));
		Heading keyHeading = new Heading();
		keyHeading.add("Name", TypeCharacter.getInstance());
		Heading keysHeading = new Heading();
		keysHeading.add("Attributes", new TypeRelation(keyHeading));
		heading.add("Keys", new TypeRelation(keysHeading));
		return heading;
	}
	
	static RelvarHeading getNewKeyDefinition(Generator generator) {
		SelectAttributes attributes = new SelectAttributes();
		attributes.add("Name");
		RelvarHeading keyDefinition = new RelvarHeading(getNewHeading(generator));
		keyDefinition.addKey(attributes);
		return keyDefinition;
	}
	
	public RelvarCatalogMetadata(RelDatabase database, Generator generator) {
		super(database, getNewKeyDefinition(generator), RelDatabase.systemOwner);
	}
	
	public RelvarHeading getHeadingDefinition(RelDatabase database) {
		return getNewKeyDefinition(new Generator(database, System.out));
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarCatalog(database);
	}	
	
	public void dropRelvar(RelDatabase database) {
		throw new ExceptionSemantic("RS0198: The " + Catalog.relvarCatalog + " relvar may not be dropped.");		
	}	
}
