package org.reldb.rel.v0.storage.catalog;

import java.util.Vector;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.*;
import org.reldb.rel.v0.storage.relvars.RelvarRealMetadata;
import org.reldb.rel.v0.values.*;

public class RelvarTypes extends RelvarSystem {
	
	public RelvarTypes(String name, RelDatabase database, RelvarRealMetadata metadata) {
		super(name, database, metadata);
	}
	
	private ValueTuple getTupleFor(Generator generator, String name, String definition, String owner, String language, Vector<String> superTypes) {
		ValueRelationLiteral superTypesRelation = new ValueRelationLiteral(generator);
		for (String typeName: superTypes)
			superTypesRelation.insert(new ValueTuple(generator, new Value[] {ValueCharacter.select(generator, typeName)}));
		Value[] rawTuple = new Value[] {
			ValueCharacter.select(generator, name),
			ValueCharacter.select(generator, definition),
			ValueCharacter.select(generator, owner),
			ValueInteger.select(generator, getDatabase().getUniqueID()),
			ValueCharacter.select(generator, language),
			new ValueRelationLiteral(generator),						// Subtypes (5th attribute)
			superTypesRelation											// Supertypes (6th attribute)
		};
		return new ValueTuple(generator, rawTuple);
	}
	
	protected ValueTuple getKeyTuple(Generator generator, String name) {
		return getTupleFor(generator, name, "", "", "", new Vector<String>());
	}
	
	public void insertInternal(Generator generator, String name, String definition, String owner, String language, Vector<String> superTypes) {
		insertInternal(generator, getTupleFor(generator, name, definition, owner, language, superTypes));
	}
	
	public void addSubtype(Generator generator, String superTypeName, String subTypeName) {
		ValueTuple superTypeData = getTupleForKey(generator, superTypeName);
		if (superTypeData == null)
			throw new ExceptionFatal("RS0316: Unable to obtain data for " + superTypeName);
		ValueRelationLiteral subtypes = (ValueRelationLiteral)superTypeData.getValues()[5];		// 5th attribute is Subtypes
		subtypes.insert(new ValueTuple(generator, new Value[] {ValueCharacter.select(generator, subTypeName)}));
		deleteInternal(generator, superTypeName);
		insertInternal(generator, superTypeData);
	}
	
	public void removeSubtype(Generator generator, String superTypeName, String subTypeName) {
		ValueTuple superTypeData = getTupleForKey(generator, superTypeName);
		if (superTypeData == null)
			throw new ExceptionFatal("RS0317: Unable to obtain data for " + superTypeName);
		ValueRelationLiteral subtypes = (ValueRelationLiteral)superTypeData.getValues()[5];		// 5th attribute is Subtypes
		subtypes.remove(new ValueTuple(generator, new Value[] {ValueCharacter.select(generator, subTypeName)}));
		deleteInternal(generator, superTypeName);
		insertInternal(generator, superTypeData);		
	}
	
}
