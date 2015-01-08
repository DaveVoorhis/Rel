package org.reldb.rel.v0.storage.relvars;

import org.reldb.rel.v0.generator.*;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.vm.*;
import org.reldb.rel.v0.vm.instructions.core.*;

public class RelvarPrivate extends Variable implements CellFactory {

	private RelDatabase database;
	private RelvarHeading headingDefinition;
	
	public RelvarPrivate(int depth, int offset, RelDatabase database, RelvarHeading headingDefinition) {
		super(depth, offset);
		setType(new TypeRelation(headingDefinition.getHeading()));
		this.database = database;
		this.headingDefinition = headingDefinition;
	}
	
	public Cell getNewCell() {
		return new RelvarPrivateCell(database.getTempTable(headingDefinition));
	}
	
	@Override
	public void compileInitialise(Generator generator) {
		generator.compileInstruction(new OpVariableInitialise(getDepth(), getOffset(), this));
	}
}
