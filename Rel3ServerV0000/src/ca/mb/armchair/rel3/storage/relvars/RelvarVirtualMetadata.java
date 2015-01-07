package ca.mb.armchair.rel3.storage.relvars;

import ca.mb.armchair.rel3.storage.RelDatabase;

public class RelvarVirtualMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	private String sourceCode;
	
	public RelvarVirtualMetadata(RelDatabase database, String sourceCode, RelvarHeading headingDefinition, String owner) {
		super(database, headingDefinition, owner);
		this.sourceCode = sourceCode;
	}
	
	public String getSourceCode() {
		return sourceCode;
	}
	
	public String getSourceDefinition() {
		return "VIRTUAL " + sourceCode;
	}
	
	public boolean isVirtual() {
		return true;
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarVirtual(name, database);
	}
	
	public void dropRelvar(RelDatabase database) {
	}
}
