package org.reldb.rel.v1.storage.catalog;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.storage.RelDatabase;
import org.reldb.rel.v1.values.*;

public class RelvarOperatorsBuiltin extends RelvarSpecial {
	
	RelvarOperatorsBuiltin(RelDatabase database) {
		super(Catalog.relvarOperatorsBuiltin, database);
	}

	// Get a TupleIterator
	public TupleIterator iterator(Generator generator) {
		return getDatabase().getBuiltinOperators(generator).iterator();
	}
}
