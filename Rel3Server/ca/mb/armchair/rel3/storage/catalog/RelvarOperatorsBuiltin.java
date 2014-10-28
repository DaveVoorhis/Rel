package ca.mb.armchair.rel3.storage.catalog;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.values.*;

public class RelvarOperatorsBuiltin extends RelvarSpecial {
	
	RelvarOperatorsBuiltin(RelDatabase database) {
		super(Catalog.relvarOperatorsBuiltin, database);
	}

	// Get a TupleIterator
	public TupleIterator iterator(Generator generator) {
		return getDatabase().getBuiltinOperators(generator).iterator();
	}
}
