/**
 * 
 */
package org.reldb.rel.generator;

import org.reldb.rel.types.Type;

/** Contains information about a variable, parameter, or other scopable identifier. */
public abstract class SlotScoped implements Slot {
	private int depth;
	private int offset;
	private Type type;

	public SlotScoped(int depth, int offset, Type type) {
		this(depth, offset);
		setType(type);
	}
	
	public SlotScoped(int depth, int offset) {
		this.depth = depth;
		this.offset = offset;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getDepth() {
		return depth;
	}
	
	/** Compile setter, which is invoked by assignment operation.  Value to be assigned is on stack. */
	public abstract void compileSet(Generator generator);
	
	/** Compile getter, which is invoked by identifier dereference.  Value will be pushed onto stack. */
	public abstract void compileGet(Generator generator);	
	
	/** Compile initialisation, which is invoked by variable initialisation. */
	public abstract void compileInitialise(Generator generator);
	
	public boolean isParameter() {
		return false;
	}
	
	public String toString() {
		return "Slot: offset=" + offset + " depth=" + depth + " type=" + type;
	}
}
