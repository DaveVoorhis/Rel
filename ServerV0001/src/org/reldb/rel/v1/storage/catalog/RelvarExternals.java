package org.reldb.rel.v1.storage.catalog;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.storage.*;
import org.reldb.rel.v1.storage.relvars.RelvarRealMetadata;
import org.reldb.rel.v1.values.*;

public class RelvarExternals extends RelvarSystem {
	
	public RelvarExternals(String name, RelDatabase database, RelvarRealMetadata metadata) {
		super(name, database, metadata);
	}
	
	private ValueTuple getTupleFor(Generator generator, String name, String definition, String owner, String language) {
		Value[] rawTuple = new Value[] {
			ValueCharacter.select(generator, name),
			ValueCharacter.select(generator, definition),
			ValueCharacter.select(generator, owner),
			ValueInteger.select(generator, getDatabase().getUniqueID()),
			ValueCharacter.select(generator, language),
			new ValueRelationLiteral(generator)						// Subtypes (5th attribute)
		};
		return new ValueTuple(generator, rawTuple);
	}
	
	protected ValueTuple getKeyTuple(Generator generator, String name) {
		return getTupleFor(generator, name, "", "", "");
	}
	
	public void insertInternal(Generator generator, String name, String definition, String owner, String language) {
		insertInternal(generator, getTupleFor(generator, name, definition, owner, language));
	}
	
	public void addSubtype(Generator generator, String superTypeName, String subTypeName) {
		ValueTuple superTypeData = getTupleForKey(generator, superTypeName);
		if (superTypeData == null)
			throw new ExceptionFatal("RS0314: Unable to obtain data for " + superTypeName);
		ValueRelationLiteral subtypes = (ValueRelationLiteral)superTypeData.getValues()[5];		// 5th attribute is Subtypes
		subtypes.insert(new ValueTuple(generator, new Value[] {ValueCharacter.select(generator, subTypeName)}));
		deleteInternal(generator, superTypeName);
		insertInternal(generator, superTypeData);
	}
	
	public void removeSubtype(Generator generator, String superTypeName, String subTypeName) {
		ValueTuple superTypeData = getTupleForKey(generator, superTypeName);
		if (superTypeData == null)
			throw new ExceptionFatal("RS0315: Unable to obtain data for " + superTypeName);
		ValueRelationLiteral subtypes = (ValueRelationLiteral)superTypeData.getValues()[5];		// 5th attribute is Subtypes
		subtypes.remove(new ValueTuple(generator, new Value[] {ValueCharacter.select(generator, subTypeName)}));
		deleteInternal(generator, superTypeName);
		insertInternal(generator, superTypeData);		
	}
	
}
