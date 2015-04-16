package org.reldb.rel.v1.values;

import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Operator;

public class RelTupleMap extends TupleMap {

	private Context context;
	private Operator mapOperator;
	
	public RelTupleMap(Context context, Operator mapOperator) {
		this.context = context;
		this.mapOperator = mapOperator;
	}
	
	@Override
	public ValueTuple map(ValueTuple tuple) {
		context.push(tuple);
		context.call(mapOperator);
		return (ValueTuple)context.pop();				
	}

}
