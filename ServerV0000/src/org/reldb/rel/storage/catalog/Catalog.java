package org.reldb.rel.storage.catalog;

import org.reldb.rel.generator.Generator;
import org.reldb.rel.generator.References;
import org.reldb.rel.storage.*;
import org.reldb.rel.storage.relvars.RelvarDefinition;
import org.reldb.rel.storage.relvars.RelvarMetadata;

import com.sleepycat.je.*;

public class Catalog {

	// System relvar names
	public final static String relvarCatalog = "sys.Catalog";
	public final static String relvarVersion = "sys.Version";
	public final static String relvarConstraints = "sys.Constraints";
	public final static String relvarOperators = "sys.Operators";
	public final static String relvarOperatorsBuiltin = "sys.OperatorsBuiltin";
	public final static String relvarTypes = "sys.Types";
	public final static String relvarDependenciesRelvarOperator = "sys.DependenciesRelvarOperator";
	public final static String relvarDependenciesRelvarRelvar = "sys.DependenciesRelvarRelvar";
	public final static String relvarDependenciesRelvarType = "sys.DependenciesRelvarType";
	public final static String relvarDependenciesTypeOperator = "sys.DependenciesTypeOperator";
	public final static String relvarDependenciesTypeType = "sys.DependenciesTypeType";
	public final static String relvarDependenciesTypeRelvar = "sys.DependenciesTypeRelvar";
	public final static String relvarDependenciesConstraintOperator = "sys.DependenciesConstraintOperator";
	public final static String relvarDependenciesConstraintRelvar = "sys.DependenciesConstraintRelvar";
	public final static String relvarDependenciesConstraintType = "sys.DependenciesConstraintType";
	public final static String relvarDependenciesOperatorOperator = "sys.DependenciesOperatorOperator";
	public final static String relvarDependenciesOperatorRelvar = "sys.DependenciesOperatorRelvar";
	public final static String relvarDependenciesOperatorType = "sys.DependenciesOperatorType";
	
	// Generate and initialise the catalog relvars
	public void generate(Generator generator) throws DatabaseException {
		(new RegisterSpecialRelvar(relvarCatalog) {
			RelvarMetadata getMetadata() {
				return new RelvarCatalogMetadata(database);
			}
		}).go();
		(new RegisterSpecialRelvar(relvarVersion) {
			RelvarMetadata getMetadata() {
				return new RelvarVersionMetadata(database);
			}
		}).go();
		(new CreateSystemRelvar(generator, relvarConstraints)).go();
		(new CreateSystemRelvar(generator, relvarOperators) {
			RelvarMetadata getMetadata() {
				return new RelvarOperatorsMetadata(database, name);
			}
		}).go();
		(new RegisterSpecialRelvar(relvarOperatorsBuiltin) {
			RelvarMetadata getMetadata() {
				return new RelvarOperatorsBuiltinMetadata(database);
			}
		}).go();
		(new CreateSystemRelvar(generator, relvarTypes) {
			RelvarMetadata getMetadata() {
				return new RelvarTypesMetadata(database, name);
			}
		}).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesRelvarOperator)).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesRelvarRelvar)).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesRelvarType)).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesTypeOperator)).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesTypeRelvar)).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesTypeType)).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesConstraintOperator)).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesConstraintRelvar)).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesConstraintType)).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesOperatorOperator)).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesOperatorRelvar)).go();
		(new CreateSystemDependenciesRelvar(generator, relvarDependenciesOperatorType)).go();        
	}
	
	private RelDatabase database;
	
	public Catalog(RelDatabase database) {
		this.database = database;
	}
	
	private void registerSpecialRelvar(String name, RelvarMetadata metadata) throws DatabaseException {
		database.putRelvarMetadata(null, name, metadata);
	}
	
	private void createSystemRelvar(Generator generator, RelvarSystemMetadata metadata) {
		String name = metadata.getName();
		database.createRealRelvar(generator, new RelvarDefinition(name, metadata, new References()));
	}
	
	private abstract class HandleRelvar {
		protected String name;
		HandleRelvar(String name) {
			this.name = name;
		}
		abstract RelvarMetadata getMetadata();
		abstract void process() throws DatabaseException;
		public void go() throws DatabaseException {
	        if (!database.isRelvarExists(name)) {
	        	System.out.println("Creating " + name + " relvar.");
	        	process();
	        }
	        // Make sure it's open
	        database.openGlobalRelvar(name);
		}
	}
	
	private abstract class RegisterSpecialRelvar extends HandleRelvar {
		RegisterSpecialRelvar(String name) {
			super(name);
		}
		public void process() throws DatabaseException {
        	registerSpecialRelvar(name, getMetadata());
		}
	}
	
	private class CreateSystemRelvar extends HandleRelvar {
		private Generator generator;
		CreateSystemRelvar(Generator generator, String name) {
			super(name);
			this.generator = generator;
		}
		RelvarMetadata getMetadata() {
			return new RelvarSystemMetadata(database, name);
		}
		public void process() throws DatabaseException {
        	createSystemRelvar(generator, (RelvarSystemMetadata)getMetadata());
		}
	}

	private class CreateSystemDependenciesRelvar extends CreateSystemRelvar {
		CreateSystemDependenciesRelvar(Generator generator, String name) {
			super(generator, name);
		}
		RelvarMetadata getMetadata() {
			return new RelvarSystemDependenciesMetadata(database, name);
		}
	}
	
}
