package org.reldb.rel.v0.storage.relvars;

import java.util.Vector;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.tables.StorageNames;
import org.reldb.rel.v0.types.Heading;


public class RelvarRealMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	private StorageNames tableName;
	
	public RelvarRealMetadata(RelDatabase database, RelvarHeading headingDefinition, String owner) {
		super(database, headingDefinition, owner);
	}
	
	public void setTableName(StorageNames tableName) {
		this.tableName = tableName;
	}
	
	public StorageNames getTableName() {
		return tableName;
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
	
}
