package org.reldb.rel.storage.catalog;

import org.reldb.rel.generator.Generator;
import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.values.*;

public class RelvarOperatorsBuiltin extends RelvarSpecial {
	
	RelvarOperatorsBuiltin(RelDatabase database) {
		super(Catalog.relvarOperatorsBuiltin, database);
	}

	// Get a TupleIterator
	public TupleIterator iterator(Generator generator) {
		return getDatabase().getBuiltinOperators(generator).iterator();
	}
}
