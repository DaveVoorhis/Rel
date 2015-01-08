package org.reldb.rel.storage.relvars;

import org.reldb.rel.generator.*;
import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.types.*;
import org.reldb.rel.vm.*;
import org.reldb.rel.vm.instructions.core.*;

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
