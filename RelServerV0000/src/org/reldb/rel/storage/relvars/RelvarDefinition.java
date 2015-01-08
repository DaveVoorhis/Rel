package org.reldb.rel.storage.relvars;

import org.reldb.rel.generator.References;

public class RelvarDefinition {
	private String name;
	private References references;
	private RelvarMetadata metadata;

	public RelvarDefinition(String name, RelvarMetadata metadata, References references) {
		this.name = name;
		this.metadata = metadata;
		this.references = references;
	}
	
	public String getName() {
		return name;
	}
		
	public References getReferences() {
		return references;
	}
	
	public RelvarMetadata getRelvarMetadata() {
		return metadata;
	}
	
}
