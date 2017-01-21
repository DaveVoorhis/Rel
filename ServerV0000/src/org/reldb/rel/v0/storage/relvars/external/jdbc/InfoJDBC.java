package org.reldb.rel.v0.storage.relvars.external.jdbc;

import org.reldb.rel.v0.storage.relvars.external.Info;

public class InfoJDBC extends Info {
	
	@Override
	public String getIdentifier() {
		return "JDBC";
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

	@Override
	public boolean isConnectionStringAFile() {
		return false;
	}

	@Override
	public String[] getAppropriateFileExtension() {
		return null;
	}
	
}
