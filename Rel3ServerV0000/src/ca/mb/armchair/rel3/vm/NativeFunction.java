package ca.mb.armchair.rel3.vm;

import ca.mb.armchair.rel3.values.Value;

public abstract class NativeFunction {
	public abstract Value evaluate(Value arguments[]);
}
