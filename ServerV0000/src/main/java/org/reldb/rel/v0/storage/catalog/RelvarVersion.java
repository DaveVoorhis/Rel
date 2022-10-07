package org.reldb.rel.v0.storage.catalog;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.version.Version;

public class RelvarVersion extends RelvarSpecial {
	
	RelvarVersion(RelDatabase database) {
		super(Catalog.relvarVersion, database);
	}

	public long getCardinality(Generator generator) {
		return 1;
	}
	
	// Get a TupleIterator
	public TupleIterator iterator(final Generator generator) {
	    return new TupleIterator() {
	    	boolean available = true;
			public boolean hasNext() {
				return available;
			}
			public ValueTuple next() {
				try {
					Value[] rawTuple = new Value[] {
						ValueInteger.select(generator, Version.getDatabaseFormatVersion()),
						ValueRational.select(generator, Version.getProductVersion()),
						ValueCharacter.select(generator, Version.getCopyright()),
						ValueCharacter.select(generator, Version.getLicense()),
						ValueCharacter.select(generator, Version.getRedistribution()),
						ValueCharacter.select(generator, Version.getWarranty())
					};
					return new ValueTuple(generator, rawTuple);
				} finally {
					available = false;
				}
			}
			public void close() {
			}
		};
	}
}
