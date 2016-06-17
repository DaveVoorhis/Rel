package org.reldb.rel.v0.storage.relvars;

import java.util.Vector;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.tables.StorageNames;
import org.reldb.rel.v0.types.Heading;


public class RelvarRealMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	private StorageNames storageNames;
	
	public RelvarRealMetadata(RelDatabase database, RelvarHeading headingDefinition, String owner) {
		super(database, headingDefinition, owner);
	}
	
	public void setStorageNames(StorageNames tableName) {
		this.storageNames = tableName;
	}
	
	public StorageNames getStorageNames() {
		return storageNames;
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarReal(name, database, this);
	}
	
	public void dropRelvar(RelDatabase database) {
	}

	public void renameAttribute(RelDatabase database, String oldAttributeName, String newAttributeName) {
		RelvarHeading relvarHeading = getHeadingDefinition(database);
		Heading heading = relvarHeading.getHeading();
		
    	if (oldAttributeName.equals(newAttributeName))
    		throw new ExceptionSemantic("RS0423: old attribute name and new attribute name are the same.");
		if (heading.getAttribute(newAttributeName) != null)
			throw new ExceptionSemantic("RS0424: new attribute name '" + newAttributeName + "' already exists.");
		
		// update metadata, renaming the old attribute name to the new one
		if (!heading.rename(oldAttributeName, newAttributeName))
			throw new ExceptionSemantic("RS0422: attribute '" + oldAttributeName + "' not found.");
		
		// create new relvar heading (including updated KEY info) from freshly-updated old heading
		RelvarHeading newHeading = new RelvarHeading(heading);
		for (int i=0; i<relvarHeading.getKeyCount(); i++) {
			SelectAttributes keyAttributes = new SelectAttributes();
			Vector<String> names = relvarHeading.getKey(i).getNames();
			for (int nameIndex=0; nameIndex<names.size(); nameIndex++) {
				String name = names.get(nameIndex);
				if (name.equals(oldAttributeName))
					keyAttributes.add(newAttributeName);
				else
					keyAttributes.add(name);
			}
			newHeading.addKey(keyAttributes);
		}

		// apply new relvar heading to existing metadata
		setHeadingDefinition(database, newHeading);
	}

	public void insertAttributes(RelDatabase database, Heading newAttributes) {
		RelvarHeading relvarHeading = getHeadingDefinition(database);
		Heading heading = relvarHeading.getHeading();
		Heading intersection = newAttributes.intersect(heading);
		if (intersection.getDegree() > 0)
			throw new ExceptionSemantic("RS0429: attempting to INSERT one or more attributes that already exist.");
		Heading newHeading = heading.unionDisjoint(newAttributes);
		// create new relvar heading
		RelvarHeading newRelvarHeading = new RelvarHeading(newHeading);
		newRelvarHeading.setKeys(relvarHeading.getKeys());
		setHeadingDefinition(database, newRelvarHeading);
	}

	// Drop attribute and return index in tuple of the dropped attribute
	public int dropAttribute(RelDatabase database, String attributeName) {
		RelvarHeading relvarHeading = getHeadingDefinition(database);
		Heading heading = relvarHeading.getHeading();
		int attributePosition = heading.getIndexOf(attributeName);
		if (attributePosition < 0)
			throw new ExceptionSemantic("RS0433: attribute '" + attributeName + "' not found.");
		if (relvarHeading.isKeyUsing(attributeName))
			throw new ExceptionSemantic("RS0434: attribute '" + attributeName + "' is referenced in a KEY.");
		SelectAttributes removeAttribute = new SelectAttributes();
		removeAttribute.add(attributeName);
		Heading removeAttributeHeading = heading.project(removeAttribute);
		Heading newHeading = heading.minus(removeAttributeHeading);
		// create new relvar heading
		RelvarHeading newRelvarHeading = new RelvarHeading(newHeading);
		newRelvarHeading.setKeys(relvarHeading.getKeys());
		setHeadingDefinition(database, newRelvarHeading);
		return attributePosition;
	}
	
}
