package org.reldb.rel.v1.storage.catalog;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.storage.RelDatabase;
import org.reldb.rel.v1.storage.relvars.RelvarRealMetadata;
import org.reldb.rel.v1.values.Value;
import org.reldb.rel.v1.values.ValueCharacter;
import org.reldb.rel.v1.values.ValueTuple;

public class RelvarSystemDependencies extends RelvarSystem {
	
	public RelvarSystemDependencies(String name, RelDatabase database, RelvarRealMetadata metadata) {
		super(name, database, metadata);
	}
	
	private ValueTuple getTupleFor(Generator generator, String object, String uses) {
		Value[] rawTuple = new Value[] {
			ValueCharacter.select(generator, object),
			ValueCharacter.select(generator, uses)
		};
		return new ValueTuple(generator, rawTuple);
	}
	
	protected ValueTuple getKeyTuple(Generator generator, String object, String uses) {
		return getTupleFor(generator, object, uses);
	}
	
	public void insertInternal(Generator generator, String object, String uses) {
		insertInternal(generator, getTupleFor(generator, object, uses));
	}
	
	public void deleteInternal(Generator generator, String object, String uses) {
		deleteInternal(generator, getKeyTuple(generator, object, uses));
	}
	
}
