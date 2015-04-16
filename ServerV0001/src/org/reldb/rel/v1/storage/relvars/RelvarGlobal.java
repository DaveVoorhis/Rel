package org.reldb.rel.v1.storage.relvars;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.generator.Slot;
import org.reldb.rel.v1.storage.RelDatabase;
import org.reldb.rel.v1.storage.TransactionRunner;
import org.reldb.rel.v1.storage.ValueRelationRelvar;
import org.reldb.rel.v1.types.*;
import org.reldb.rel.v1.values.*;
import org.reldb.rel.v1.vm.instructions.relvar.*;

import com.sleepycat.je.*;

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
