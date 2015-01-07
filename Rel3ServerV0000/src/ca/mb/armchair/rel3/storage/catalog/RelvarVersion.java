package ca.mb.armchair.rel3.storage.catalog;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.values.*;
import ca.mb.armchair.rel3.version.Version;

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
					Value rawTuple[] = new Value[] {
						ValueInteger.select(generator, Version.getMinor()),
						ValueInteger.select(generator, Version.getMajor()),
						ValueInteger.select(generator, Version.getRevision()),
						ValueCharacter.select(generator, Version.getCopyright()),
						ValueCharacter.select(generator, Version.getLicense()),
						ValueCharacter.select(generator, Version.getNumericVersion()),
						ValueCharacter.select(generator, Version.getRedistribution()),
						ValueCharacter.select(generator, Version.getRelease()),
						ValueCharacter.select(generator, Version.getVersion()),
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
