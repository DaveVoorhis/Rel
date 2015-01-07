package ca.mb.armchair.rel3.storage.catalog;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.values.*;

public class RelvarCatalog extends RelvarSpecial {
	
	RelvarCatalog(RelDatabase database) {
		super(Catalog.relvarCatalog, database);
	}

	// Get a TupleIterator
	public TupleIterator iterator(Generator generator) {
		return getDatabase().getCatalogTupleIterator(generator);
	}
	
}
