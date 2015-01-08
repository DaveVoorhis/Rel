package org.reldb.rel.types;

import org.reldb.rel.generator.Generator;
import org.reldb.rel.values.Value;

public interface Type {

	/** Obtain this type's signature. */
	public String getSignature();
	
	/** Return true if source can be assigned to variables of this type. */
	public boolean canAccept(Type source);
	
	/** Return true if source must be reformated before being assigned to variables
	 * of this type.  This should only be true on Projectable ValueS of TypeHeading. */
	public boolean requiresReformatOf(Type source);
	
	/** Obtain a default value of this type. */
	public Value getDefaultValue(Generator generator);
	
	/** Return the name of the Java class that represents values of this type. */
    public String getValueClassname(Generator generator);

	public String toString();
}
