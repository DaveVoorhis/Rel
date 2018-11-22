package org.reldb.rel.v0.storage.relvars.external.relvar;

import java.util.Vector;

import org.reldb.rel.client.connection.string.ClientNetwork;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.shared.Defaults;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.interpreter.Evaluation;
import org.reldb.rel.v0.interpreter.Interpreter;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.RelvarCustomMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarExternal;
import org.reldb.rel.v0.storage.relvars.RelvarGlobal;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.relvars.external.CSVLineParse;
import org.reldb.rel.v0.storage.tables.TableExternal.DuplicateHandling;
import org.reldb.rel.v0.types.Attribute;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.TypeRelation;

public class RelvarRELVARMetadata extends RelvarCustomMetadata {
	public static final long serialVersionUID = 0;

	private String connectionString;
	
	private String host;
	private String user;
	private String password;
	private String relvar;
	private int port = Defaults.getDefaultPort();

	public static RelvarHeading getHeading(RelDatabase database, String spec, DuplicateHandling duplicates) {
		String[] values = CSVLineParse.parseTrimmed(spec);	
		if (values.length != 4 && values.length != 5)
			throw new ExceptionSemantic("RS0482: Invalid arguments. Expected: HOST, USER, PASSWORD, RELVAR, [PORT] but got " + spec);
		String host = values[0];
		// String user = values[1];
		// String password = values[2];
		String relvar = values[3];
		int port = Defaults.getDefaultPort();
		if (values.length == 5) {
			String portValue = values[4];
			try {
				port = Integer.parseInt(portValue);
			} catch (NumberFormatException nfe) {
				throw new ExceptionSemantic("RS0488: Invalid port specification: " + portValue);
			}
		}
	
		// Send query to remote Rel DBMS
		String response = "";
		try {
			ClientNetwork connection = new ClientNetwork(host, port);
			connection.sendEvaluate(relvar + " WHERE false");
			String line;
			while ((line = connection.receive()) != null)
				response += line;
		} catch (Throwable ioe) {
			throw new ExceptionSemantic("RS0494: Error obtaining remote Rel relvar: " + ioe);
		}

		// Interpret response using local Rel DBMS
		try {
			Interpreter interpreter = new Interpreter(database, System.out);
			Evaluation result = interpreter.evaluate(response);
			if (!(result.getType() instanceof TypeRelation))
				throw new ExceptionSemantic("RS0493: Error obtaining remote Rel relvar.");
			TypeRelation typeRelation = (TypeRelation)result.getType();
			Heading heading = new Heading();
			Vector<Attribute> attributes = typeRelation.getHeading().getAttributes();
			for (Attribute attribute: attributes)
				heading.add(attribute);
			RelvarHeading relvarHeading = new RelvarHeading(heading);
			return relvarHeading;
		} catch (Throwable t) {
			throw new ExceptionSemantic("RS0492: Error obtaining remote Rel relvar: " + t);			
		}		
	}

	@Override
	public String getSourceDefinition() {
		return "EXTERNAL RELVAR \"" + host + "," + user + "," + password + "," + relvar + "," + port + "\"";
	}

	public RelvarRELVARMetadata(RelDatabase database, String owner, String spec, DuplicateHandling duplicates) {
		super(database, getHeading(database, spec, duplicates), owner);		
		String[] values = CSVLineParse.parseTrimmed(spec);
		host = values[0];
		user = values[1];
		password = values[2];
		relvar = values[3];
		if (values.length == 5) {
			String portValue = values[4];
			try {
				port = Integer.parseInt(portValue);
			} catch (NumberFormatException nfe) {
				throw new ExceptionSemantic("RS0495: Invalid port specification: " + portValue);
			}
		}
		connectionString = spec;
	}

	@Override
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarExternal(name, database, new Generator(database, System.out), this, DuplicateHandling.DUP_REMOVE);
	}

	@Override
	public void dropRelvar(RelDatabase database) {
	}

	public String getConnectionString() {
		return connectionString;
	}
	
	public String getHost() {
		return host;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getRelvar() {
		return relvar;
	}

	public int getPort() {
		return port;
	}
	
	@Override
	public String tableClassName() {
		return "TableRELVAR";
	}

	@Override
	public String getType() {
		return "RELVAR";
	}
}
