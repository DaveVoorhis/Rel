package org.reldb.rel.v0.storage.relvars.external.accdb;

import org.reldb.rel.v0.storage.relvars.external.Info;

public class InfoACCDB extends Info {
	
	@Override
	public String getIdentifier() {
		return "ACCDB";
	}

	@Override
	public String getConnectionStringDocumentation() {
		return
			"Connect to a specified table, 'mytable', in a Microsoft Acccess database.\n" +
			"Examples:\n" +
			"\tVAR myvar EXTERNAL ACCDB \"c:\\users\\me\\mydb.accdb,mytable\";\n" +
			"\tVAR myvar EXTERNAL ACCDB \"c:\\users\\me\\mydb.mdb,mytable\";\n";
	}

	@Override
	public boolean isConnectionStringAFile() {
		return true;
	}

	@Override
	public String[] getAppropriateFileExtension() {
		return new String[] {"mdb", "accdb"};
	}
	
}
