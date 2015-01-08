package org.reldb.rel.v0.storage.relvars;

import java.io.Serializable;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.v0.generator.SelectAttributes;
import org.reldb.rel.v0.interpreter.Interpreter;
import org.reldb.rel.v0.languages.tutoriald.parser.ParseException;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.types.Heading;

public class RelvarHeadingMetadata implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String headingText;
	private String[][] keys;
	
	public RelvarHeadingMetadata(RelvarHeading headingDefinition) {
		headingText = headingDefinition.getHeading().getSignature();
		keys = new String[headingDefinition.getKeyCount()][];
		for (int i=0; i<keys.length; i++)
			keys[i] = headingDefinition.getKey(i).getNames().toArray(new String[0]);
	}
	
	public RelvarHeading getHeadingDefinition(RelDatabase database) {
    	Interpreter interpreter = new Interpreter(database, System.out);
    	Heading heading;
		try {
			heading = interpreter.getHeading(headingText);
		} catch (ParseException pe) {
			throw new ExceptionFatal("RS0368: Failed loading heading " + headingText + ": " + pe.toString());
		}
		RelvarHeading relvarHeading = new RelvarHeading(heading);
		for (int i=0; i<keys.length; i++) {
			SelectAttributes keyAttributes = new SelectAttributes();
			for (int j=0; j<keys[i].length; j++)
				keyAttributes.add(keys[i][j]);
			relvarHeading.addKey(keyAttributes);
		}
		return relvarHeading;
	}
	
}
