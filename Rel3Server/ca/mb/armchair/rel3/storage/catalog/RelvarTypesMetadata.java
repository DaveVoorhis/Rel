package ca.mb.armchair.rel3.storage.catalog;

import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.storage.relvars.RelvarGlobal;
import ca.mb.armchair.rel3.storage.relvars.RelvarHeading;
import ca.mb.armchair.rel3.types.*;
import ca.mb.armchair.rel3.types.builtin.TypeCharacter;
import ca.mb.armchair.rel3.types.builtin.TypeInteger;
import ca.mb.armchair.rel3.generator.SelectAttributes;

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
