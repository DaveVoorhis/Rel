/**
 * 
 */
package ca.mb.armchair.rel3.generator;

import ca.mb.armchair.rel3.types.Type;

/** Contains information about a variable, parameter, or other Value-holder. */
public interface Slot {

	public Type getType();
	
	/** Compile setter, which is invoked by assignment operation.  Value to be assigned is on stack. */
	public abstract void compileSet(Generator generator);
	
	/** Compile getter, which is invoked by identifier dereference.  Value will be pushed onto stack. */
	public abstract void compileGet(Generator generator);	
	
	/** Compile initialiser, which is invoked by the code that initialises a variable Cell that holds this slot at run-time. */
	public abstract void compileInitialise(Generator generator);
	
	/** Return true if this slot is an operator parameter. */
	public abstract boolean isParameter();
}
