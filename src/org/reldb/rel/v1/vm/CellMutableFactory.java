package org.reldb.rel.v1.vm;

public class CellMutableFactory implements CellFactory {

	public Cell getNewCell() {
		return new CellMutable();
	}

}
