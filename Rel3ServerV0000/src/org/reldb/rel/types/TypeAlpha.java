package org.reldb.rel.types;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.generator.Generator;
import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.types.userdefined.MultipleInheritance;
import org.reldb.rel.types.userdefined.Possrep;
import org.reldb.rel.values.*;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Operator;
import org.reldb.rel.vm.VirtualMachine;

public class TypeAlpha extends TypeAbstract implements Comparable<TypeAlpha> {

	private static TypeAlpha emptyAlpha = new TypeAlpha("ALPHA");
	
	private String typeName;
	private boolean isOrdinal;
	private boolean isOrdered;	
	private boolean isUnion;
	private TypeAlpha superType = null;
	private MultipleInheritance multipleInheritanceDefinition = null;
	private Operator specialisationConstraintFn = null;
	private HashSet<TypeAlpha> subTypes = new HashSet<TypeAlpha>(); 
	private Vector<Possrep> possreps = new Vector<Possrep>();
	private int nextComponentIndex = 0;

	public TypeAlpha(String typeName) {
		this.typeName = typeName;
	}

	public static Type getEmptyAlphaType() {
		return emptyAlpha;
	}
	
	public boolean isBuiltin() {
		return false;
	}
	
	public String getTypeName() {
		return this.typeName;
	}
	
	public void setOrdinal(boolean ordinal) {
		isOrdinal = ordinal;
	}
	
	public boolean isOrdinal() {
		return isOrdinal;
	}

	public void setOrdered(boolean ordered) {
		isOrdered = ordered;
	}
	
	public boolean isOrdered() {
		return isOrdered;
	}
	
	public void setUnion(boolean union) {
		isUnion = union;
	}
	
	public boolean isUnion() {
		return isUnion;
	}
	
	public Type getRootType() {
		if (superType == null)
			return this;
		else
			return superType.getRootType();
	}
	
	public void setSupertype(TypeAlpha superType) {
		this.superType = superType;
		superType.addSubtype(this);
		multipleInheritanceDefinition = null;
	}
	
	public TypeAlpha getSupertype() {
		return superType;
	}
	
	public LinkedList<TypeAlpha> getSupertypes() {
		LinkedList<TypeAlpha> list = new LinkedList<TypeAlpha>();
		TypeAlpha type = superType;
		while (type != null) {
			list.add(type);
			type = type.superType;
		}
		return list;
	}
	
	public TypeAlpha getMostSpecificCommonSupertype(TypeAlpha t) {
		if (this == t)
			return this;
		// TODO - optimise this some day (maybe)
		if (superType == t.superType)
			return superType;
		LinkedList<TypeAlpha> parentsOfThisNode = getSupertypes();
		TypeAlpha parent = t.superType;
		while (parent != null) {
			for (TypeAlpha listItem: parentsOfThisNode)
				if (listItem == parent)
					return listItem;
			parent = parent.superType;
		}
		throw new ExceptionFatal("RS0383: Unable to find most specific common supertype.");
	}
	
	public void addSubtype(TypeAlpha subType) {
		subTypes.add(subType);
	}
	
	public void removeSubtype(TypeAlpha subType) {
		subTypes.remove(subType);
	}
	
	public void removeFromSupertype() {
		superType.removeSubtype(this);
	}
	
	public HashSet<TypeAlpha> getSubtypes() {
		return subTypes;
	}
	
	public void setMultipleInheritance(MultipleInheritance miDefinition) {
		multipleInheritanceDefinition = miDefinition;
		superType = null;
		possreps.clear();
	}
	
	public MultipleInheritance getMultipleInheritanceDefinition() {
		return multipleInheritanceDefinition;
	}
	
	public boolean isSubtype() {
		return superType != null || multipleInheritanceDefinition != null;
	}
	
	public int getNextComponentIndex() {
		if (isSubtype())
			return superType.getNextComponentIndex();
		return nextComponentIndex++;
	}
	
	/** Locate POSSREP by name.  Return null if not found. */
	public Possrep locatePossrep(String name) {
		for (Possrep p: possreps)
			if (p.getName().equals(name))
				return p; 
		return null;
	}
	
	public void addPossrep(Possrep possrep) {
		if (locatePossrep(possrep.getName()) != null)
			throw new ExceptionSemantic("RS0256: A POSSREP named '" + possrep.getName() + "' has already been defined in TYPE " + getSignature() + ".");
		for (int i=0; i<possrep.getComponentCount(); i++) {
			for (Possrep p: possreps) {
				String name = possrep.getComponent(i).getName();
				if (p.locateComponent(name) != null)
					throw new ExceptionSemantic("RS0257: Component '" + name + "' appears in POSSREP '" + p.getName() + "' and POSSREP '" + possrep.getName() + "'.");
			}
		}
		possreps.add(possrep);
	}
	
	public Possrep getPossrep(int i) {
		if (i>=possreps.size())
			return null;
		return possreps.get(i);
	}
	
	public int getPossrepCount() {
		return possreps.size();
	}
	
	public void clear() {
		possreps.clear();
	}
	
	// Make sure all POSSREPs have INITialisation.
	public void checkPossrepInitialisation() {
		for (int i=0; i<getPossrepCount(); i++) {
			Possrep possrep = getPossrep(i);
			if (!possrep.hasInitialiser())
				throw new ExceptionSemantic("RS0258: POSSREP '" + possrep.getName() + "' has no INITialisation.");
		}
	}
	
	private int getLocalComponentCount() {
		return nextComponentIndex;
	}
		
	public int getComponentCount() {
		if (superType != null)
			return getLocalComponentCount() + superType.getComponentCount();
		else if (multipleInheritanceDefinition != null)
				throw new ExceptionFatal("TypeUserdefined: Multiple inheritance is not yet supported.");
		else
			return getLocalComponentCount();
	}
	
	/** Obtain this type's signature. */
	public String getSignature() {
		return typeName;
	}
	
	/** Return true if source can be assigned to variables of this type. */
	public boolean canAccept(Type source) {
		// TODO - rewrite this canAccept to deal with multiple inheritance.
		if (!(source instanceof TypeAlpha))
			return false;
		if (((TypeAlpha)source).superType == null)
			return (source.getSignature().equals(getSignature()));
		TypeAlpha subtypeType = (TypeAlpha)source;
		while (subtypeType != null) {
			if (subtypeType.getSignature().equals(getSignature()))
				return true;
			subtypeType = subtypeType.superType;
		}
		return false;
	}
	
	/** Obtain a default value of this type. */
	public Value getDefaultValue(Generator generator) {
		return new ValueAlpha(generator, this);
	}
	
	public boolean checkSpecialisationConstraint(Generator generator, ValueAlpha value, RelDatabase database) {
		if (specialisationConstraintFn == null)
			return true;
		// TODO - optimise by not creating new VirtualMachine and Context on each invocation
		VirtualMachine vm = new VirtualMachine(generator, database, System.out);
		Context context = new Context(generator, vm);
		context.push(value);
		context.call(specialisationConstraintFn);
		return ((ValueBoolean)context.pop()).booleanValue();		
	}

	public void setSpecialisationConstraint(Operator operator) {
		specialisationConstraintFn = operator;
	}
	
	public boolean hasSpecialisationConstraint() {
		return specialisationConstraintFn != null;
	}
	
	public int hashCode() {
		return typeName.hashCode();
	}

	public int compareTo(TypeAlpha arg0) {
		return typeName.compareTo(arg0.typeName);
	}

}
