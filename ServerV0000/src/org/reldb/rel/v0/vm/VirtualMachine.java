package org.reldb.rel.v0.vm;

import java.io.PrintStream;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.*;
import org.reldb.rel.v0.values.*;

/**
 * The Rel virtual machine.
 * 
 * @author dave
 */

public class VirtualMachine {
	
	// currently executing Context
	private Context currentContext;
	
	// root execution Context.
	private Context rootContext;
	
	// Active database
	private RelDatabase database;
	
	// Generator
	private Generator generator;
	
	// Output stream
	private PrintStream printStream;
	
	// Update notice counts
	private long inserts;
	private long updates;
	private long deletes;
	private boolean inserted;
	private boolean updated;
	private boolean deleted;
	
	/** Create a virtual machine. */
	public VirtualMachine(Generator generator, RelDatabase database, PrintStream printStream) {
		this.generator = generator;
		this.database = database;
		this.printStream = printStream;
		reset();
	}
	
	/** Clear tuple update notices. */
	public void clearTupleUpdateNotices() {
		inserts = 0;
		updates = 0;
		deletes = 0;
		inserted = false;
		updated = false;
		deleted = false;
	}

	public void noticeInsert(long insert) {
		inserts += insert;
		inserted = true;
	}
	
	public void noticeUpdate(long update) {
		updates += update;
		updated = true;
	}
	
	public void noticeDelete(long delete) {
		deletes += delete;
		deleted = true;
	}
	
	public void outputTupleUpdateNotices() {
		if (inserted || updated || deleted) {
			if (inserted)
				printStream.println("NOTICE: Inserted " + inserts + " tuple" + ((inserts == 1) ? "" : "s") + ".");
			if (updated)
				printStream.println("NOTICE: Updated " + updates + " tuple" + ((updates == 1) ? "" : "s") + ".");
			if (deleted)
				printStream.println("NOTICE: Deleted " + deletes + " tuple" + ((deletes == 1) ? "" : "s") + ".");
			clearTupleUpdateNotices();
		}
	}
	
	/** Get the active database */
	public RelDatabase getRelDatabase() {
		return database;
	}

	/** Get the output stream assigned to this VM. */
	public PrintStream getPrintStream() {
		return printStream;
	}
	
	/** Reset the VM. */
	public void reset() {
		Context halting = currentContext;
		while (halting != null) {
			halting.halt();
			halting = halting.getCaller();
		}
		rootContext = new Context(generator, this);
		clearTupleUpdateNotices();
	}
	
	/** Get the currently-executing Instruction. */
	public Instruction getCurrentInstruction() {
		return currentContext.getCurrentInstruction();
	}
	
	final void setCurrentContext(Context context) {
		currentContext = context;
	}

	public Context getCurrentContext() {
		return currentContext;
	}
	
	/** Execute the given Operator in the root Context. */
	public final void execute(Operator op) {
		rootContext.call(op);
	}
	
	/** Pop a value from the root Context. */
	public final Value pop() {
		return rootContext.pop();
	}

	/** Get the number of items on the stack in the root Context. */
	public final int getStackCount() {
		return rootContext.getStackCount();
	}

}
