package org.reldb.rel.types.userdefined;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.types.TypeAlpha;

public class DerivedPossrep extends Possrep {

	public DerivedPossrep(TypeAlpha type, String name) {
		super(type, name);
	}

	public void addDerivation(String newIdentifier, String oldIdentifier, String supertypeName) {
		if (!oldIdentifier.startsWith("THE_"))
			throw new ExceptionSemantic("RS0236: Derived POSSREP component must reference a THE_ operator.");
		oldIdentifier = oldIdentifier.substring(4);
		TypeAlpha supertype = this.getType().getSupertype();
		while (!supertype.getTypeName().equals(supertypeName)) {
			supertype = supertype.getSupertype();
			if (supertype == null)
				throw new ExceptionSemantic("RS0237: Type '" + supertypeName + "' is not a supertype of '" + getType().getTypeName() + "'.");
		}
		for (int i=0; i<supertype.getPossrepCount(); i++) {
			Possrep possrep = supertype.getPossrep(i);
			PossrepComponent component = possrep.locateComponent(oldIdentifier);
			if (component != null) {
				new PossrepComponent(this, component.getComponentIndex(), newIdentifier, component.getType());
				return;
			}
		}
		throw new ExceptionSemantic("RS0238: A component named '" + oldIdentifier + "' could not be found in type '" + supertypeName + "'.");
	}

}
