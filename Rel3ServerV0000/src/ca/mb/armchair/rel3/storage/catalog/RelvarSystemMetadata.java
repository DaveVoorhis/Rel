package ca.mb.armchair.rel3.storage.catalog;

import ca.mb.armchair.rel3.storage.*;
import ca.mb.armchair.rel3.storage.relvars.RelvarGlobal;
import ca.mb.armchair.rel3.storage.relvars.RelvarHeading;
import ca.mb.armchair.rel3.storage.relvars.RelvarRealMetadata;
import ca.mb.armchair.rel3.types.*;
import ca.mb.armchair.rel3.types.builtin.TypeCharacter;
import ca.mb.armchair.rel3.types.builtin.TypeInteger;
import ca.mb.armchair.rel3.generator.SelectAttributes;
import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;

public class RelvarSystemMetadata extends RelvarRealMetadata {
	public static final long serialVersionUID = 0;
	
	private String name;
	
	static Heading getNewHeading() {
		Heading heading = new Heading();
		heading.add("Name", TypeCharacter.getInstance());
		heading.add("Definition", TypeCharacter.getInstance());
		heading.add("Owner", TypeCharacter.getInstance());
		heading.add("CreationSequence", TypeInteger.getInstance());
		return heading;
	}
	
	static RelvarHeading getNewKeyDefinition() {
		SelectAttributes attributes = new SelectAttributes();
		attributes.add("Name");
		RelvarHeading keyDefinition = new RelvarHeading(getNewHeading());
		keyDefinition.addKey(attributes);
		return keyDefinition;
	}
	
	protected RelvarSystemMetadata(RelDatabase database, String name, RelvarHeading definition) {
		super(database, definition, RelDatabase.systemOwner);
		this.name = name;
	}
	
	public RelvarSystemMetadata(RelDatabase database, String name) {
		this(database, name, getNewKeyDefinition());
	}
	
	public String getName() {
		return name;
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarSystem(name, database, this);
	}
	
	public void dropRelvar(RelDatabase database) {
		throw new ExceptionSemantic("RS0205: The " + name + " relvar may not be dropped.");		
	}	
	
}
