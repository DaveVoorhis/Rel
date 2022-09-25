package org.reldb.rel.v0.storage.relvars.external;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.relvars.external.accdb.InfoACCDB;
import org.reldb.rel.v0.storage.relvars.external.csv.InfoCSV;
import org.reldb.rel.v0.storage.relvars.external.jdbc.InfoJDBC;
import org.reldb.rel.v0.storage.relvars.external.relvar.InfoRELVAR;
import org.reldb.rel.v0.storage.relvars.external.xls.InfoXLS;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.TypeRelation;
import org.reldb.rel.v0.types.builtin.TypeBoolean;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.Value;
import org.reldb.rel.v0.values.ValueBoolean;
import org.reldb.rel.v0.values.ValueCharacter;
import org.reldb.rel.v0.values.ValueInteger;
import org.reldb.rel.v0.values.ValueRelationLiteral;
import org.reldb.rel.v0.values.ValueTuple;

public class Registry {
	private static List<Info> registry; 

	static {
		add(new InfoACCDB());
		add(new InfoCSV());
		add(new InfoJDBC());
		add(new InfoXLS());
		add(new InfoRELVAR());
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
	 * 
	 * REL {
	 * 		Identifier CHAR, 
	 * 		Description CHAR,
	 *      Documentation CHAR, 
	 *      Components REL {
	 *      	ComponentNumber INTEGER,
	 *      	isOptional BOOLEAN,
	 *      	isAFile BOOLEAN,
	 *      	FileExtensions REL {
	 *      		Extension CHAR
	 *      	},
	 *      	Documentation CHAR,
	 *      	ComponentOptions REL {
	 *      		Documentation CHAR,
	 *      		OptionText CHAR
	 *      },
	 *      GuaranteedUnique BOOLEAN
	 * }
	 */
	public static Heading getHeading() {
		Heading heading = new Heading();
		heading.add("Identifier", TypeCharacter.getInstance());
		heading.add("Description", TypeCharacter.getInstance());
		heading.add("Documentation", TypeCharacter.getInstance());
		Heading components = new Heading();
		components.add("ComponentNumber", TypeInteger.getInstance());
		components.add("isOptional", TypeBoolean.getInstance());
		components.add("isAFile", TypeBoolean.getInstance());
		Heading fileExtensions = new Heading();
		fileExtensions.add("Extension", TypeCharacter.getInstance());
		components.add("FileExtensions", new TypeRelation(fileExtensions));
		components.add("Documentation", TypeCharacter.getInstance());
		Heading componentOptions = new Heading();
		componentOptions.add("Documentation", TypeCharacter.getInstance());
		componentOptions.add("OptionText", TypeCharacter.getInstance());
		components.add("ComponentOptions", new TypeRelation(componentOptions));
		heading.add("Components", new TypeRelation(components));
		heading.add("GuaranteedUnique", TypeBoolean.getInstance());
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
				ValueCharacter identifier = ValueCharacter.select(generator, info.getIdentifier());
				ValueCharacter description = ValueCharacter.select(generator, info.getDescription());
				ValueCharacter documentation = ValueCharacter.select(generator, info.getConnectionStringDocumentation());
				ValueBoolean guaranteedUnique = ValueBoolean.select(generator, info.isGuaranteedUnique());
				ValueRelationLiteral components = new ValueRelationLiteral(generator);
				if (info.getConnectionStringComponents() != null)
					for (InfoComponent component: info.getConnectionStringComponents()) {
						ValueInteger componentNumber = ValueInteger.select(generator, component.getComponentNumber());
						ValueBoolean isOptional = ValueBoolean.select(generator, component.isOptional());
						ValueBoolean isAFile = ValueBoolean.select(generator, component.isAFile());
						ValueRelationLiteral extensions = new ValueRelationLiteral(generator);
						if (component.getAppropriateFileExtension() != null)
							for (String extension: component.getAppropriateFileExtension()) {
								ValueCharacter extensionChar = ValueCharacter.select(generator, extension);
								extensions.insert(new ValueTuple(generator, new Value[] {extensionChar}));
							}
						ValueCharacter componentDocumentation = ValueCharacter.select(generator, component.getDocumentation());
						ValueRelationLiteral componentOptions = new ValueRelationLiteral(generator);
						if (component.getOptions() != null)
							for (InfoComponentOption option: component.getOptions()) {
								ValueCharacter optionDocumentation = ValueCharacter.select(generator, option.getDocumentation());
								ValueCharacter optionText = ValueCharacter.select(generator, option.getOptionText());
								componentOptions.insert(new ValueTuple(generator, new Value[] {optionDocumentation, optionText}));
							}
						components.insert(new ValueTuple(generator, new Value[] {
								componentNumber,
								isOptional,
								isAFile,
								extensions,
								componentDocumentation,
								componentOptions
							}
						));
					}
				rawTuple = new Value[] {
					identifier,
					description,
					documentation,
					components,
					guaranteedUnique
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
