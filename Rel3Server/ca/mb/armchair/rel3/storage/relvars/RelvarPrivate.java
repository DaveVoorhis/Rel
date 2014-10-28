package ca.mb.armchair.rel3.storage.relvars;

import ca.mb.armchair.rel3.generator.*;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.types.*;
import ca.mb.armchair.rel3.vm.*;
import ca.mb.armchair.rel3.vm.instructions.core.*;

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
