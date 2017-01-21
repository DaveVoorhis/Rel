package org.reldb.rel.v0.storage.catalog;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.RelvarGlobal;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.relvars.RelvarMetadata;
import org.reldb.rel.v0.storage.relvars.external.Registry;
import org.reldb.rel.v0.types.*;

public class RelvarExternalRelvarTypesMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	static Heading getNewHeading() {
		return Registry.getHeading();
	}
	
	static RelvarHeading getNewKeyDefinition() {
		RelvarHeading keydef = new RelvarHeading(getNewHeading());
		SelectAttributes keyAttributes = new SelectAttributes();
		keyAttributes.add("Identifier");
		keydef.addKey(keyAttributes);
		return keydef;
	}
	
	public RelvarExternalRelvarTypesMetadata(RelDatabase database) {
		super(database, getNewKeyDefinition(), RelDatabase.systemOwner);
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarExternalRelvarTypes(database);
	}

	public void dropRelvar(RelDatabase database) {
		throw new ExceptionSemantic("RS0480: The " + Catalog.relvarExternalRelvarTypes + " relvar may not be dropped.");		
	}
}
