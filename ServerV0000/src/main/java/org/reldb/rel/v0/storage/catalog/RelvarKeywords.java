package org.reldb.rel.v0.storage.catalog;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.languages.tutoriald.Keywords;
import org.reldb.rel.v0.values.*;

public class RelvarKeywords extends RelvarSpecial {
	
	RelvarKeywords(RelDatabase database) {
		super(Catalog.relvarKeywords, database);
	}

	public long getCardinality(Generator generator) {
		return Keywords.getCardinality();
	}
	
	// Get a TupleIterator
	public TupleIterator iterator(final Generator generator) {
		return Keywords.getKeywords(generator);
	}
}
