package org.reldb.dbrowser.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.reldb.rel.client.Connection.ExecuteResult;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;
import org.reldb.rel.utilities.StringUtils;

public class RevDatabase {

	public static final int EXPECTED_REV_VERSION = 0;
	public static final int QUERY_WAIT_MILLISECONDS = 5000;
	
	private DbConnection connection;
	
	public RevDatabase(DbConnection connection) {
		this.connection = connection;
	}

	public Value evaluate(String query) {
		return connection.evaluate(query);
	}

	public int hasRevExtensions() {
		return connection.hasRevExtensions();
	}

	public long getUniqueNumber() {
		return connection.evaluate("GET_UNIQUE_NUMBER()").toLong();
	}
	
	private boolean execute(String query) {
		ExecuteResult result = connection.execute(query);
		if (result.failed()) {
			System.out.println("Rev: Error: " + result.getErrorMessage());
			System.out.println("Rev: Query: " + query);
			return false;
		}
		return true;
	}
	
	private Tuples getTuples(String query) {
		return connection.getTuples(query);
	}
	
	public boolean installRevExtensions() {
		String query = 
				"var sys.rev.Version real relation {" +
				"	ver INTEGER" +
				"} INIT(relation {tuple {ver " + EXPECTED_REV_VERSION + "}}) key {ver};" +
				
				"var sys.rev.Settings real relation {" +
				"   Name CHAR, " +
				"   value CHAR " +
				"} key {Name};" +
			    
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
			    
			    "var sys.rev.Script real relation {" +
			    "   Name CHAR, " +
			    "   text CHAR " +
			    "} key {Name};" +
			    
			    "var sys.rev.ScriptHistory real relation {" +
			    "   Name CHAR, " +
			    "   text CHAR, " +
			    "   timestamp CHAR " +
			    "} key {Name, timestamp};" +
			    
			    "var sys.rev.Operator real relation {" +
				"   Name CHAR, " +
			    "   Definition CHAR" +
				"} key {Name};" +
			    
				"var sys.rev.Op_Update real relation {" +
				"   Name CHAR, " +
				"   Definition RELATION {" +
				"	  ID INTEGER," +
				"     attribute CHAR," +
				"	  expression CHAR" +
				"   }" +
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
				"drop var sys.rev.Op_Update;" +
				"drop var sys.rev.Op_Extend;" +
				"drop var sys.rev.Op_Summarize;" +
				"drop var sys.rev.Script;" +
				"drop var sys.rev.ScriptHistory;" +
			    "drop var sys.rev.Query;" +
				"drop var sys.rev.Relvar;" +
			    "drop var sys.rev.Settings;" +
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
	
	// Update
	public Tuples getPreservedStateUpdate(String name) {
		String query = "(sys.rev.Op_Update WHERE Name = '" + name + "') UNGROUP Definition ORDER(ASC ID)";
		return getTuples(query);
	}
	
	public void updatePreservedStateUpdate(String name, String definition) {
		String query = "DELETE sys.rev.Op_Update WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Update RELATION {" +
		               "  TUPLE {Name '" + name + "', Definition " + definition + "}" +
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
	
	public void removeOperator_Update(String name) {
		String query = "DELETE sys.rev.Op_Update WHERE Name = '" + name + "';";
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
			           "INSERT sys.rev.Op_Update UPDATE sys.rev.Op_Update JOIN (((sys.rev.Query WHERE model = '" + oldName + "') {Name}) UNION ((sys.rev.Relvar WHERE model = '" + oldName + "') {Name})): {Name := Name || 'copy'}, " +
			           "INSERT sys.rev.Op_Extend UPDATE sys.rev.Op_Extend JOIN (((sys.rev.Query WHERE model = '" + oldName + "') {Name}) UNION ((sys.rev.Relvar WHERE model = '" + oldName + "') {Name})): {Name := Name || 'copy'}, " +
			           "INSERT sys.rev.Op_Summarize UPDATE sys.rev.Op_Summarize JOIN (((sys.rev.Query WHERE model = '" + oldName + "') {Name}) UNION ((sys.rev.Relvar WHERE model = '" + oldName + "') {Name})): {Name := Name || 'copy'}, " +
			           "INSERT sys.rev.Query UPDATE sys.rev.Query WHERE model = '" + oldName + "': {model := '" + newName + "', Name := Name || 'copy', connections := UPDATE connections: {Name := Name || 'copy'}}, " +	        
			           "INSERT sys.rev.Relvar UPDATE sys.rev.Relvar WHERE model = '" + oldName + "': {model := '" + newName + "', Name := Name || 'copy'};";
		execute(query);
	}
	
	public boolean modelDelete(String name) {
		String query = 
				"DELETE sys.rev.Operator WHERE TUPLE {Name Name} IN ((sys.rev.Query WHERE model = '" + name + "') {Name}) UNION ((sys.rev.Relvar WHERE model = '" + name + "') {Name}), " +
				"DELETE sys.rev.Op_Update WHERE TUPLE {Name Name} IN ((sys.rev.Query WHERE model = '" + name + "') {Name}) UNION ((sys.rev.Relvar WHERE model = '" + name + "') {Name}), " +
				"DELETE sys.rev.Op_Extend WHERE TUPLE {Name Name} IN ((sys.rev.Query WHERE model = '" + name + "') {Name}) UNION ((sys.rev.Relvar WHERE model = '" + name + "') {Name}), " +
				"DELETE sys.rev.Op_Summarize WHERE TUPLE {Name Name} IN ((sys.rev.Query WHERE model = '" + name + "') {Name}) UNION ((sys.rev.Relvar WHERE model = '" + name + "') {Name}), " +
				"DELETE sys.rev.Query WHERE model = '" + name + "', " +
				"DELETE sys.rev.Relvar WHERE model = '" + name + "';";
		return execute(query);
	}
	
	public Vector<String> getModels() {
		String query = "(sys.rev.Query {model}) UNION (sys.rev.Relvar {model})";
		Tuples tuples = (Tuples)evaluate(query);
		Vector<String> models = new Vector<String>();
		for (Tuple tuple: tuples)
			 models.add(tuple.get("model").toString());
		return models;
	}

	public boolean relvarExists(String name) {
		String query = "COUNT(sys.Catalog WHERE Name = '" + name + "') > 0";
		Value result = (Value)evaluate(query);
		return result.toBoolean();
	}

	public boolean scriptExists(String name) {
		String query = "COUNT(sys.rev.Script WHERE Name = '" + name + "') > 0";
		Value result = (Value)evaluate(query);
		return result.toBoolean();		
	}

	public boolean createScript(String name) {
		String query = "INSERT sys.rev.Script RELATION {TUPLE {Name '" + name + "', text ''}};";
		return execute(query);
	}

	public static class Script {
		private String content;
		private Vector<String> history;
		public Script(String content, Vector<String> history) {
			this.content = content;
			this.history = history;
		}
		public Vector<String> getHistory() {
			return history;
		}
		public String getContent() {
			return content;
		}
	}
	
	public Script getScript(String name) {
		String query = "sys.rev.Script WHERE Name='" + name + "'";
		String content = "";
		Tuples tuples = (Tuples)evaluate(query);
		for (Tuple tuple: tuples)
			content = StringUtils.unquote(tuple.get("text").toString());
		query = "sys.rev.ScriptHistory WHERE Name='" + name + "' ORDER (ASC timestamp)";
		tuples = (Tuples)evaluate(query);
		Vector<String> history = new Vector<String>();
		for (Tuple tuple: tuples)
			history.add(StringUtils.unquote(tuple.get("text").toString()));
		return new Script(content, history);
	}

	public void setScript(String name, String content) {
		String text = StringUtils.quote(content);
		String query = 
			"IF COUNT(sys.rev.Script WHERE Name='" + name + "') = 0 THEN " +
			"  INSERT sys.rev.Script REL {TUP {Name '" + name + "', text '" + text + "'}}; " +
			"ELSE " +
			"  UPDATE sys.rev.Script WHERE Name='" + name + "': {text := '" + text + "'}; " +
			"END IF;";
		execute(query);
	}
	
	public void addScriptHistory(String name, String historyItem) {
		String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
		String query = 
			"INSERT sys.rev.ScriptHistory " + "REL {TUP {" +
			"  Name '" + name + "', " +
			"  text '" + StringUtils.quote(historyItem) + "', " +
			"  timestamp '" + timestamp + "'" +
			"}};";
		execute(query);
	}

	public boolean scriptDelete(String name) {
		String query = "DELETE sys.rev.Script WHERE Name='" + name + "';";
		return execute(query);
	}

	public boolean renameScript(String nameFrom, String nameTo) {
		String query = 
			"UPDATE sys.rev.Script WHERE Name='" + nameFrom + "': {Name := '" + nameTo + "'}, " +
			"UPDATE sys.rev.ScriptHistory WHERE Name='" + nameFrom + "': {Name := '" + nameTo + "'};";
		return execute(query);
	}
	
	public void setSetting(String name, String value) {
		String query = 
			"IF COUNT(sys.rev.Settings WHERE Name='" + name + "') = 0 THEN " +
			"   INSERT sys.rev.Settings REL {TUP {Name '" + name + "', value '" + StringUtils.quote(value) + "'}}; " +
			"ELSE " +
			"   UPDATE sys.rev.Settings WHERE Name='" + name + "': {value := '" + StringUtils.quote(value) + "'}; " +
			"END IF;";
		execute(query);
	}
	
	public String getSetting(String name) {
		String query = "sys.rev.Settings WHERE Name='" + name + "'";
		Tuples tuples = (Tuples)evaluate(query);
		for (Tuple tuple: tuples)
			 return StringUtils.unquote(tuple.get("value").toString());
		return "";
	}

	public static class Overview {
		private String content;
		private boolean revPrompt;
		public Overview(String content, boolean revPrompt) {
			this.content = content;
			this.revPrompt = revPrompt;
		}
		public String getContent() {
			return content;
		}
		public boolean getRevPrompt() {
			return revPrompt;
		}
	}
	
	public Overview getOverview() {
		String query = "pub.Overview";
		Tuples tuples = (Tuples)evaluate(query);
		try {
			for (Tuple tuple: tuples) {
				String content = StringUtils.unquote(tuple.get("content").toString());
				boolean revPrompt = tuple.get("revPrompt").toBoolean();
				return new Overview(content, revPrompt);
			}
		} catch (Exception e) {}
		return new Overview("", true);
	}

	public boolean createOverview() {
		String query = 
			"VAR pub.Overview REAL RELATION {content CHAR, revPrompt BOOLEAN} KEY {}; " +
			"INSERT pub.Overview RELATION {TUP {content '" + 
				StringUtils.quote(
					"Edit the pub.Overview variable to change this text.\n" +
					"The 'contents' attribute value will appear here.\n" +
					"Set the 'revPrompt' attribute to FALSE to only display this overview."
				) + "', revPrompt TRUE}};";
		return execute(query);
	}

	public String[] getKeywords() {
		return connection.getKeywords();
	}

	public String[] getRelvarTypes() {
		String query = "UNION {sys.ExternalRelvarTypes {Identifier, Description}, REL {TUP {Identifier 'REAL', Description 'REAL relation-valued variable.'}}}  ORDER (ASC Identifier)";
		Tuples tuples = (Tuples)evaluate(query);
		Vector<String> types = new Vector<String>(); 
		try {
			for (Tuple tuple: tuples) {
				String identifier = StringUtils.unquote(tuple.get("Identifier").toString());
				String description = StringUtils.unquote(tuple.get("Description").toString());
				types.add(identifier + ": " + description);
			}
		} catch (Exception e) {}
		return types.toArray(new String[0]);
	}

	public Tuple getExternalRelvarTypeInfo(String variableType) {
		String query = "sys.ExternalRelvarTypes WHERE Identifier='" + variableType + "'";
		Tuples tuples = (Tuples)evaluate(query);
		for (Tuple tuple: tuples)
			return tuple;
		return null;
	}
	
}
