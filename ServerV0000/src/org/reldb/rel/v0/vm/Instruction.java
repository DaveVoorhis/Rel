package org.reldb.rel.v0.vm;

import org.reldb.rel.v0.debuginfo.DebugInfo;

/** Base class for VM operators. */
public abstract class Instruction {

	private DebugInfo errorContext = null;
	
	public void setDebugInfo(DebugInfo errorContext) {
		this.errorContext = errorContext; 
	}
	
	public final DebugInfo getDebugInfo() {
		return errorContext;
	}
	
    /** Get this instruction's name. */
    public final String getName() {
        return getClass().getSimpleName();
    }
    
    /** Stringify */
    public String toString() {
        return getName();
    }
    
    /** Execute this instruction on a given Context. */ 
    public abstract void execute(Context context);
}