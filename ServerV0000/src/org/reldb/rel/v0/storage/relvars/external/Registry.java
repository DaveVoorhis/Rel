package org.reldb.rel.v0.storage.relvars.external;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.relvars.external.accdb.InfoACCDB;
import org.reldb.rel.v0.storage.relvars.external.csv.InfoCSV;
import org.reldb.rel.v0.storage.relvars.external.jdbc.InfoJDBC;
import org.reldb.rel.v0.storage.relvars.external.xls.InfoXLS;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.TypeRelation;
import org.reldb.rel.v0.types.builtin.TypeBoolean;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.Value;
import org.reldb.rel.v0.values.ValueBoolean;
import org.reldb.rel.v0.values.ValueCharacter;
import org.reldb.rel.v0.values.ValueRelationLiteral;
import org.reldb.rel.v0.values.ValueTuple;

public class Registry {
	private static List<Info> registry; 

	static {
		add(new InfoACCDB());
		add(new InfoCSV());
		add(new InfoJDBC());
		add(new InfoXLS());
	}
	
	private static void ensureRegistryExists() {
		if (registry == null)
			registry = new LinkedList<Info>();		
	}
	
	private static void add(Info relvarCustomInfo) {
		ensureRegistryExists();
		registry.add(relvarCustomInfo);
	}
	
	/** Obtain heading for registry relvar.
	 * REL {Identifier CHAR, Documentation CHAR, isFileConnectionString BOOLEAN, FileExtensions REL {Extension CHAR}}
	 */
	public static Heading getHeading() {
		Heading heading = new Heading();
		heading.add("Identifier", TypeCharacter.getInstance());
		heading.add("Documentation", TypeCharacter.getInstance());
		heading.add("isFileConnectionString", TypeBoolean.getInstance());
		Heading fileExtensionsHeading = new Heading();
		fileExtensionsHeading.add("Extension", TypeCharacter.getInstance());
		heading.add("FileExtensions", new TypeRelation(fileExtensionsHeading));
		return heading;
	}
	
	/** Obtain registry details as a TupleIterator for the above.
	 */
	public static TupleIterator getRegistry(Generator generator) {
		ensureRegistryExists();
		return new TupleIterator() {
			Iterator<Info> iterator = registry.iterator();
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
			@Override
			public ValueTuple next() {
				Value rawTuple[];
				Info info = iterator.next();
				ValueRelationLiteral extensions = new ValueRelationLiteral(generator);
				if (info.getAppropriateFileExtension() != null)
					for (String extension: info.getAppropriateFileExtension()) {
						ValueCharacter extensionChar = ValueCharacter.select(generator, extension);
						extensions.insert(new ValueTuple(generator, new Value[] {extensionChar}));
					}
				rawTuple = new Value[] {
					ValueCharacter.select(generator, info.getIdentifier()),
					ValueCharacter.select(generator, info.getConnectionStringDocumentation()),
					ValueBoolean.select(generator, info.isConnectionStringAFile()),
					extensions
				};
				return new ValueTuple(generator, rawTuple);
			}
			@Override
			public void close() {}
		};
	}

	public static long getCardinality() {
		return registry.size();
	}
}
