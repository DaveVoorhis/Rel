package org.reldb.rel.storage.catalog;

import org.reldb.rel.generator.Generator;
import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.values.*;

public class RelvarCatalog extends RelvarSpecial {
	
	RelvarCatalog(RelDatabase database) {
		super(Catalog.relvarCatalog, database);
	}

	// Get a TupleIterator
	public TupleIterator iterator(Generator generator) {
		return getDatabase().getCatalogTupleIterator(generator);
	}
	
}
