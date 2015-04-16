package org.reldb.rel.v1.storage.catalog;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.storage.RelDatabase;
import org.reldb.rel.v1.values.*;

public class RelvarCatalog extends RelvarSpecial {
	
	RelvarCatalog(RelDatabase database) {
		super(Catalog.relvarCatalog, database);
	}

	// Get a TupleIterator
	public TupleIterator iterator(Generator generator) {
		return getDatabase().getCatalogTupleIterator(generator);
	}
	
}
