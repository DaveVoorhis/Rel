package org.reldb.rel.v0.storage.relvars.external.accdb;

import org.reldb.rel.v0.storage.relvars.external.Info;
import org.reldb.rel.v0.storage.relvars.external.InfoComponent;
import org.reldb.rel.v0.storage.relvars.external.InfoComponentOption;

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
	public InfoComponent[] getConnectionStringComponents() {
		return new InfoComponent[] {
			new InfoComponent(0) {
				@Override
				public boolean isOptional() {
					return false;
				}
				@Override
				public boolean isAFile() {
					return true;
				}
				@Override
				public String[] getAppropriateFileExtension() {
					return new String[] {"accdb", "mdb"};
				}
				@Override
				public String getDocumentation() {
					return "Path to Microsoft Access database file.";
				}
				@Override
				public InfoComponentOption[] getOptions() {
					return null;
				}
			},
			new InfoComponent(1) {
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
				public String getDocumentation() {
					return "Microsoft Access database table name.";
				}
				@Override
				public InfoComponentOption[] getOptions() {
					return null;
				}
			}
		};
	}
	
}
