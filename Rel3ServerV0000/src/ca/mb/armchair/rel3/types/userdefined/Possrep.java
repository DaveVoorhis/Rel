package ca.mb.armchair.rel3.types.userdefined;

import java.util.ArrayList;

import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;
import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.types.TypeAlpha;
import ca.mb.armchair.rel3.values.ValueBoolean;
import ca.mb.armchair.rel3.values.ValueAlpha;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Operator;
import ca.mb.armchair.rel3.vm.VirtualMachine;

public class Possrep {
	private String name;
	private ArrayList<PossrepComponent> components = new ArrayList<PossrepComponent>();
	private TypeAlpha type = null;
	private Operator constraintFn = null;
	private Operator initialiserFn = null;
	
	public Possrep(TypeAlpha type, String name) {
		this.type = type;
		this.name = name;
		type.addPossrep(this);
	}
	
	public TypeAlpha getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setConstraint(Operator constraintFn) {
		this.constraintFn = constraintFn;
	}
	
	public boolean hasConstraint() {
		return constraintFn != null;
	}
	
	public boolean checkConstraint(Generator generator, ValueAlpha value, RelDatabase database) {
		if (constraintFn == null)
			return true;
		// TODO - optimise by not creating new VirtualMachine and Context on each invocation
		VirtualMachine vm = new VirtualMachine(generator, database, System.out);
		Context context = new Context(generator, vm);
		context.push(value);
		context.call(constraintFn);
		return ((ValueBoolean)context.pop()).booleanValue();		
	}

	public void setInitialiser(Operator operator) {
		initialiserFn = operator;
	}

	public boolean hasInitialiser() {
		return initialiserFn != null;
	}

	public void runInitialiser(Generator generator, ValueAlpha value, RelDatabase database) {
		if (!hasInitialiser())
			return;
		// TODO - optimise by not creating new VirtualMachine and Context on each invocation
		VirtualMachine vm = new VirtualMachine(generator, database, System.out);
		Context context = new Context(generator, vm);
		context.push(value);
		context.call(initialiserFn);
	}
	
	/** Locate component by name.  Return null if not found. */
	public PossrepComponent locateComponent(String name) {
		for (PossrepComponent component: components)
			if (component.getName().equals(name))
				return component; 
		return null;
	}
	
	public void addComponent(PossrepComponent component) {
		if (locateComponent(component.getName()) != null)
			throw new ExceptionSemantic("RS0239: A component named '" + component.getName() + "' has already been defined in POSSREP " + getName() + ".");
		components.add(component);
	}
	
	public PossrepComponent getComponent(int n) {
		return components.get(n);
	}
	
	public int getComponentCount() {
		return components.size();
	}

	public int getNextComponentIndex() {
		return type.getNextComponentIndex();
	}
	
}
