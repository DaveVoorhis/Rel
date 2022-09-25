package org.reldb.rel.v0.storage.catalog;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.values.*;

public class RelvarOperatorsBuiltin extends RelvarSpecial {
	
	RelvarOperatorsBuiltin(RelDatabase database) {
		super(Catalog.relvarOperatorsBuiltin, database);
	}

	// Get a TupleIterator
	public TupleIterator iterator(Generator generator) {
		return getDatabase().getBuiltinOperators(generator).iterator();
	}
}
