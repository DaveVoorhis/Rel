package org.reldb.rel.v0.storage.relvars;

import java.io.Serializable;

import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.types.TypeRelation;

public abstract class RelvarMetadata implements Serializable {
	public static final long serialVersionUID = 0;

	private RelvarHeadingMetadata headingDefinitionMetadata;
	private transient RelvarHeading headingDefinition = null;
	private String owner;
	private long creationSequence;
	private String source;

	private void obtainSource(RelDatabase database) {
		source = (new TypeRelation(getHeadingDefinition(database).getHeading())).toString() + " " + getHeadingDefinition(database).toString();		
	}
	
	public RelvarMetadata(RelDatabase database, RelvarHeading headingDefinition, String owner) {
		this.headingDefinition = headingDefinition;
		headingDefinitionMetadata = new RelvarHeadingMetadata(headingDefinition);
		this.owner = owner;
		this.creationSequence = -1;
		obtainSource(database);
	}
	
	public RelvarHeading getHeadingDefinition(RelDatabase database) {
		if (headingDefinition == null)
			headingDefinition = headingDefinitionMetadata.getHeadingDefinition(database);
		return headingDefinition;
	}

	public void setHeadingDefinition(RelDatabase database, RelvarHeading newHeading) {
		headingDefinitionMetadata = new RelvarHeadingMetadata(newHeading);
		headingDefinition = newHeading;
		obtainSource(database);
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
		
	public boolean isExternal() {
		return false;
	}
	
	public String getSourceDefinition() {
		return "REAL " + source;
	}
	
	public abstract RelvarGlobal getRelvar(String name, RelDatabase database);
	
	public abstract void dropRelvar(RelDatabase database);
}
