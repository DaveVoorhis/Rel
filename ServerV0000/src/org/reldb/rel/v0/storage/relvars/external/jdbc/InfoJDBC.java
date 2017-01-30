package org.reldb.rel.v0.storage.relvars.external.jdbc;

import org.reldb.rel.v0.storage.relvars.external.Info;
import org.reldb.rel.v0.storage.relvars.external.InfoComponent;
import org.reldb.rel.v0.storage.relvars.external.InfoComponentOption;

public class InfoJDBC extends Info {
	
	@Override
	public String getIdentifier() {
		return "JDBC";
	}

	@Override
	public String getDescription() {
		return "Connection to a specified table in a SQL database.";
	}

	@Override
	public String getConnectionStringDocumentation() {
		return
	      "Built-in JDBC support is provided for MySQL, MariaDB, PostgreSQL, Oracle and Microsoft SQL Server:\n" +
	      "\tVAR myvar EXTERNAL JDBC \"jdbc:mysql://localhost/database,sqluser,sqluserpw,MyTable\";\n" +
	      "\tVAR myvar EXTERNAL JDBC \"jdbc:mariadb://localhost/database,sqluser,sqluserpw,MyTable\";\n" + 
	      "\tVAR myvar EXTERNAL JDBC \"jdbc:postgresql://localhost/database,sqluser,sqluserpw,MyTable\";\n" +      
	      "\tVAR myvar EXTERNAL JDBC \"jdbc:oracle:thin:@localhost:1521:database,sqluser,sqluserpw,MyTable\";\n" +       
	      "\tVAR myvar EXTERNAL JDBC \"jdbc:sqlserver://localhost:1433;databaseName=database,sqluser,sqluserpw,MyTable\";\n";
	}

	private static class InfoComponentJDBC extends InfoComponent {
		private String documentation;
		
		InfoComponentJDBC(int componentNumber, String documentation) {
			super(componentNumber);
			this.documentation = documentation;
		}
		
		@Override
		public boolean isOptional() {
			return false;
		}

		@Override
		public boolean isAFile() {
			return false;
		}

		@Override
		public String[] getAppropriateFileExtension() {
			return null;
		}
		
		@Override
		public InfoComponentOption[] getOptions() {
			return null;
		}

		@Override
		public String getDocumentation() {
			return documentation;
		}
	}
	
	@Override
	public InfoComponent[] getConnectionStringComponents() {
		return new InfoComponent[] {
			new InfoComponentJDBC(0, "JDBC connection string"),
			new InfoComponentJDBC(1, "SQL user name"),
			new InfoComponentJDBC(2, "SQL password"),
			new InfoComponentJDBC(3, "SQL table name")
		};
	}
	
}
