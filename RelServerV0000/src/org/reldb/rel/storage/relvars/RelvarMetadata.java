package org.reldb.rel.storage.relvars;

import java.io.Serializable;

import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.types.TypeRelation;

public abstract class RelvarMetadata implements Serializable {
	public static final long serialVersionUID = 0;

	private RelvarHeadingMetadata headingDefinitionMetadata;
	private transient RelvarHeading headingDefinition = null;
	private String owner;
	private long creationSequence;
	private String source;

	public RelvarMetadata(RelDatabase database, RelvarHeading headingDefinition, String owner) {
		this.headingDefinition = headingDefinition;
		headingDefinitionMetadata = new RelvarHeadingMetadata(headingDefinition);
		this.owner = owner;
		this.creationSequence = -1;
		source = (new TypeRelation(getHeadingDefinition(database).getHeading())).toString() + " " + getHeadingDefinition(database).toString();
	}
	
	public RelvarHeading getHeadingDefinition(RelDatabase database) {
		if (headingDefinition == null)
			headingDefinition = headingDefinitionMetadata.getHeadingDefinition(database);
		return headingDefinition;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public void setCreationSequence(long creationSequence) {
		this.creationSequence = creationSequence;
	}
	
	public long getCreationSequence() {
		return creationSequence;
	}
	
	public boolean isVirtual() {
		return false;
	}
		
	public String getSourceDefinition() {
		return "REAL " + source;
	}
	
	public abstract RelvarGlobal getRelvar(String name, RelDatabase database);
	
	public abstract void dropRelvar(RelDatabase database);
}
