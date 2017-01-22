package org.reldb.rel.v0.storage.relvars.external;

public abstract class InfoComponent {
	private int componentNumber;
	
	public InfoComponent(int componentNumber) {
		this.componentNumber = componentNumber;
	}
	
	// Component number.
	public int getComponentNumber() {
		return componentNumber;
	}
	
	// True if this component is optional.
	public abstract boolean isOptional();
	
	// True if this component specifies a file
	public abstract boolean isAFile();
	
	// Return suggested file extensions. Null if any file is allowable or if isAFile() returns false.
	public abstract String[] getAppropriateFileExtension();

	// Documentation for this component.
	public abstract String getDocumentation();
	
	// If this component can belong to a set of one or options, specify them here. Otherwise, return null.
	public abstract InfoComponentOption[] getOptions();
}
