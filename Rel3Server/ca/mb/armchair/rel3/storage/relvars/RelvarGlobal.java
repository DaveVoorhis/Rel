package ca.mb.armchair.rel3.storage.relvars;

import com.sleepycat.je.*;

import ca.mb.armchair.rel3.exceptions.*;
import ca.mb.armchair.rel3.values.*;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.storage.TransactionRunner;
import ca.mb.armchair.rel3.storage.ValueRelationRelvar;
import ca.mb.armchair.rel3.types.*;
import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.generator.Slot;
import ca.mb.armchair.rel3.vm.instructions.relvar.*;

public abstract class RelvarGlobal implements Slot, Relvar {

	private RelDatabase database;
	private String name;
	
	public RelvarGlobal(String name, RelDatabase database) {
		this.name = name;
		this.database = database;
	}

	public String getName() {
		return name;
	}

	public RelDatabase getDatabase() {
		return database;
	}
	
	RelvarMetadata getRelvarMetadata() {
		try {
	    	return (RelvarMetadata)(new TransactionRunner() {
	    		public Object run(Transaction txn) throws Throwable {
	    			return database.getRelvarMetadata(txn, name);
	    		}
	    	}).execute(database);
		} catch (Throwable de) {
			throw new ExceptionFatal("RS0367: getRelvarMetadata failed: " + de);
		}
	}
	
	public RelvarHeading getHeadingDefinition() {
		return getRelvarMetadata().getHeadingDefinition(database);
	}
	
	public Type getType() {
		return new TypeRelation(getHeadingDefinition().getHeading());
	}
	
	public ValueRelation getValue(Generator generator) {
		return new ValueRelationRelvar(generator, this);
	}

	public void compileGet(Generator generator) {
		generator.compileInstruction(new OpRelvarGlobalGet(name, getHeadingDefinition()));
	}
	
	public void compileSet(Generator generator) {
		generator.compileInstruction(new OpRelvarGlobalSet(name, getHeadingDefinition()));
	}
		
	public void compileInitialise(Generator generator) {
		generator.compileInstruction(new OpRelvarGlobalSet(getName(), getHeadingDefinition()));
	}
	
	public boolean isParameter() {
		return false;
	}
}
