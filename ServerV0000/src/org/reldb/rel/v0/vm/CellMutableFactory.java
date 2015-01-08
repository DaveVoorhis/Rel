package org.reldb.rel.v0.vm;

public class CellMutableFactory implements CellFactory {

	public Cell getNewCell() {
		return new CellMutable();
	}

}
