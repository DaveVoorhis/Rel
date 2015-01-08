package org.reldb.rel.vm;

public class CellMutableFactory implements CellFactory {

	public Cell getNewCell() {
		return new CellMutable();
	}

}
