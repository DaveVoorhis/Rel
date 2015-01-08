package org.reldb.rel.v0.storage.relvars;

import org.reldb.rel.v0.generator.References;

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
