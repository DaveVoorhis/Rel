package ca.mb.armchair.rel3.vm;

public class CellMutableFactory implements CellFactory {

	public Cell getNewCell() {
		return new CellMutable();
	}

}
