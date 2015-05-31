package org.reldb.dbrowser.ui.content.rev;

import java.io.IOException;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.reldb.rel.client.Connection;
import org.reldb.rel.client.Error;
import org.reldb.rel.client.NullTuples;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;
import org.reldb.rel.client.Connection.HTMLReceiver;

public class DatabaseAbstractionLayer {

	public static final int EXPECTED_REV_VERSION = 0;
	public static final int QUERY_WAIT_MILLISECONDS = 5000;
	
	private Connection connection;
	
	public DatabaseAbstractionLayer(Connection connection) {
		this.connection = connection;
	}
	
	private synchronized boolean execute(String query) {
		Value response;
		try {
			response = connection.execute(query).awaitResult(QUERY_WAIT_MILLISECONDS);
		} catch (IOException e1) {
			System.out.println("DatabaseAbstraction: execute() error: " + e1);
			e1.printStackTrace();
			return false;
		}
		if (response instanceof Error && response.toString().length() > 0) {
			System.out.println("DatabaseAbstraction: execute() query returns error in " + query + ": " + response.toString());
			return false;
		}
		if (response == null) {
			System.out.println("DatabaseAbstraction: execute() unable to obtain query results.");
			return false;
		}
		return true;
	}

	private synchronized Tuples getTuples(String query) {
		Value response;
		try {
			response = connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS);
		} catch (IOException e) {
			System.out.println("DatabaseAbstraction: getTuples() error: " + e);
			e.printStackTrace();
			return null;
		}
		if (response instanceof Error && response.toString().length() > 0) {
			System.out.println("DatabaseAbstraction: getTuples() query returns error in " + query + ": " + response.toString());
			return null;
		}
		if (response == null) {
			System.out.println("DatabaseAbstraction: getTuples() unable to obtain query results.");
			return null;
		}
		return (Tuples)response;
	}

	public synchronized void evaluate(String query, HTMLReceiver htmlReceiver) {
		connection.evaluate(query, htmlReceiver);
	}

	public synchronized Value evaluate(String query) {
		try {
			Value result = connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS);
			if (result instanceof Error) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", result.toString());
				return new NullTuples();
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return new NullTuples();
		}
	}

	public synchronized int hasRevExtensions() {
		String query = "sys.rev.Version";
		try {
			Value response = (Value)connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS);
			if (response instanceof Tuples) {
				int version = -1;
				for (Tuple tuple: (Tuples)response)
					version = tuple.get("ver").toInt();
				return version;
			}
			return -1;
		} catch (IOException e) {
			System.out.println("Unable to obtain version of Rev extensions.  Are they not installed?");
			return -1;
		}
	}

	public synchronized long getUniqueNumber() {
		String query = "GET_UNIQUE_NUMBER()";
		try {
			return connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS).toLong();
		} catch (Exception e) {
			System.out.println("Rev failed to get a unique number: " + e);
			return 0;
		}
	}
	
	public boolean installRevExtensions() {
		String query = 
				"var sys.rev.Version real relation {" +
				"	ver INTEGER" +
				"} INIT(relation {tuple {ver " + EXPECTED_REV_VERSION + "}}) key {ver};" +
				
			    "var sys.rev.Relvar real relation {" +
			    "   Name CHAR, " +
			    "   relvarName CHAR, " +
			    "	xpos INTEGER, " +
			    "	ypos INTEGER, " +
			    "	model CHAR" +
			    "} key {Name};" +
			    
			    "var sys.rev.Query real relation {" +
			    "   Name CHAR, " +
			    "   xpos INTEGER, " +
			    "   ypos INTEGER, " +
			    "   kind CHAR, " +
			    "   connections RELATION {" +
			    "      parameter INTEGER, " +
			    "      Name CHAR" +
			    "   }," +
			    "	model CHAR" +
			    "} key {Name};" +
			    
			    "var sys.rev.Operator real relation {" +
				"   Name CHAR, " +
			    "   Definition CHAR" +
				"} key {Name};" +
				
				"var sys.rev.Op_Extend real relation {" +
				"   Name CHAR, " +
				"   Definition RELATION {" +
				"	  ID INTEGER," +
				"     attribute CHAR," +
				"	  expression CHAR" +
				"   }" +
				"} key {Name};" +

				"var sys.rev.Op_Summarize real relation {" +
				"   Name CHAR, " +
				"   isby BOOLEAN, " +
				"   byList CHAR, " +
			    "	Definition RELATION {" +
				"	  ID INTEGER, " +
				"     asAttribute CHAR, " +
				"     aggregateOp CHAR, " +
				"	  expression1 CHAR, " +
				"	  expression2 CHAR " +
				"   }" +
				"} key {Name};";
		
		return execute(query);
	}

	public boolean removeRevExtensions() {
		String query = 
				"drop var sys.rev.Operator;" +
				"drop var sys.rev.Op_Extend;" +
				"drop var sys.rev.Op_Summarize;" +
			    "drop var sys.rev.Query;" +
				"drop var sys.rev.Relvar;" +
				"drop var sys.rev.Version;";
		return execute(query);
	}
	
	public Tuples getRelvars() {
		String query = "sys.Catalog {Name, Owner}";
		return getTuples(query);
	}
	
	public Tuples getRelvars(String model) {
		String query = "sys.rev.Relvar WHERE model = '" + model + "'";
		return getTuples(query);
	}

	public Tuples getQueries(String model) {
		String query = "sys.rev.Query WHERE model = '" + model + "'";
		return getTuples(query);
	}
	
	// Update relvar position
	public void updateRelvarPosition(String name, String relvarName, int x, int y, String model) {
		String query = 
				"DELETE sys.rev.Relvar where Name='" + name + "', " + 
                "INSERT sys.rev.Relvar relation {tuple {Name '" + name + "', relvarName '" + relvarName + "', xpos " + x + ", ypos " + y + ", model '" + model + "'}};";
		execute(query);
	}
	
	// Update query operator position
	public void updateQueryPosition(String name, int x, int y, String kind, String connections, String model) {
		String query = 
				"DELETE sys.rev.Query where Name='" + name + "', " + 
                "INSERT sys.rev.Query relation {tuple {" +
						"Name '" + name + "', " +
						"xpos " + x + ", " +
						"ypos " + y + ", " +
						"kind '" + kind + "', " + 
						"connections " + connections + ", " + 
						"model '" + model + "'" +
					"}};";
		execute(query);
	}

	// Preserved States
	
	// Most operators
	public Tuples getPreservedStateOperator(String name) {
		String query = "sys.rev.Operator WHERE Name = '" + name + "'";
		return getTuples(query);
	}
	
	public void updatePreservedStateOperator(String name, String definition) {
		String query = "DELETE sys.rev.Operator WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Operator RELATION {" +
		                  "TUPLE {Name '" + name + "', Definition '" + definition + "'}" +
		               "};";
		execute(query);
	}
	
	// Extend
	public Tuples getPreservedStateExtend(String name) {
		String query = "(sys.rev.Op_Extend WHERE Name = '" + name + "') UNGROUP Definition ORDER(ASC ID)";
		return getTuples(query);
	}
	
	public void updatePreservedStateExtend(String name, String definition) {
		String query = "DELETE sys.rev.Op_Extend WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Extend RELATION {" +
		               "  TUPLE {Name '" + name + "', Definition " + definition + "}" +
		               "};";
		execute(query);
	}
	
	// Summarize
	public Tuples getPreservedStateSummarize(String name) {
		String query = "(sys.rev.Op_Summarize WHERE Name = '" + name + "') UNGROUP Definition ORDER(ASC ID)";
		return getTuples(query);
	}
	
	public void updatePreservedStateSummarize(String name, String definition) {
		String query = "DELETE sys.rev.Op_Summarize WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Summarize RELATION {" +
		               "  TUPLE {Name '" + name + "', isby false, byList '', Definition " + definition + "}" +
		               "};";
		execute(query);
	}
	
	public void updatePreservedStateSummarize(String name, String byList, String definition) {
		String query = "DELETE sys.rev.Op_Summarize WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Summarize RELATION {" +
		               "  TUPLE {Name '" + name + "', isby true, byList '" + byList + "', Definition " + definition + "}" +
		               "};";
		execute(query);
	}
	
	public void removeQuery(String name) {
		String query = "DELETE sys.rev.Query WHERE Name = '" + name + "';"; 
		execute(query);
	}
	
	public void removeRelvar(String name) {
		String query = "DELETE sys.rev.Relvar WHERE Name = '" + name + "';"; 
		execute(query);
	}
	
	public void removeOperator(String name) {
		String query = "DELETE sys.rev.Operator WHERE Name = '" + name + "';";
		execute(query);
	}
	
	public void removeOperator_Extend(String name) {
		String query = "DELETE sys.rev.Op_Extend WHERE Name = '" + name + "';";
		execute(query);
	}
	
	public void removeOperator_Summarize(String name) {
		String query = "DELETE sys.rev.Op_Summarize WHERE Name = '" + name + "';";
		execute(query);
	}

	public boolean modelExists(String name) {
		String query = "COUNT(((sys.rev.Query {model}) UNION (sys.rev.Relvar {model})) WHERE model = '" + name + "') > 0";
		return evaluate(query).toBoolean();
	}

	public void modelRename(String oldName, String newName) {
		if (oldName.equals(newName))
			return;
		String query = "DELETE sys.rev.Query WHERE model = '" + newName + "', " +
				       "DELETE sys.rev.Relvar WHERE model = '" + newName + "', " +
				       "UPDATE sys.rev.Query WHERE model = '" + oldName + "': {model := '" + newName + "'}, " +
				       "UPDATE sys.rev.Relvar WHERE model = '" + oldName + "': {model := '" + newName + "'};";
		execute(query);
	}
	
	public void modelCopyTo(String oldName, String newName) {
		if (oldName.equals(newName))
			return;
		String query = "DELETE sys.rev.Query WHERE model = '" + newName + "', " +
			           "DELETE sys.rev.Relvar WHERE model = '" + newName + "', " +
			           "INSERT sys.rev.Operator UPDATE sys.rev.Operator JOIN (((sys.rev.Query WHERE model = '" + oldName + "') {Name}) UNION ((sys.rev.Relvar WHERE model = '" + oldName + "') {Name})): {Name := Name || 'copy'}, " +
			           "INSERT sys.rev.Op_Extend UPDATE sys.rev.Op_Extend JOIN (((sys.rev.Query WHERE model = '" + oldName + "') {Name}) UNION ((sys.rev.Relvar WHERE model = '" + oldName + "') {Name})): {Name := Name || 'copy'}, " +
			           "INSERT sys.rev.Op_Summarize UPDATE sys.rev.Op_Summarize JOIN (((sys.rev.Query WHERE model = '" + oldName + "') {Name}) UNION ((sys.rev.Relvar WHERE model = '" + oldName + "') {Name})): {Name := Name || 'copy'}, " +
			           "INSERT sys.rev.Query UPDATE sys.rev.Query WHERE model = '" + oldName + "': {model := '" + newName + "', Name := Name || 'copy', connections := UPDATE connections: {Name := Name || 'copy'}}, " +	        
			           "INSERT sys.rev.Relvar UPDATE sys.rev.Relvar WHERE model = '" + oldName + "': {model := '" + newName + "', Name := Name || 'copy'};";
		execute(query);
	}
	
	public Vector<String> getModels() {
		String query = "(sys.rev.Query {model}) UNION (sys.rev.Relvar {model})";
		Tuples tuples = (Tuples)evaluate(query);
		Vector<String> models = new Vector<String>();
		for (Tuple tuple: tuples)
			 models.add(tuple.get("model").toString());
		return models;
	}
	
}
