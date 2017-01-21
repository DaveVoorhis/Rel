package org.reldb.rel.v0.storage.relvars.external.csv;

import org.reldb.rel.v0.storage.relvars.external.Info;

public class InfoCSV extends Info {

	@Override
	public String getIdentifier() {
		return "CSV";
	}

	@Override
	public String getConnectionStringDocumentation() {
		return
			"Specify the file name.\n" +
			"Examples:\n" +
			"\tVAR myvar EXTERNAL CSV \"/home/dave/test.csv\"\n" +
			"\tVAR myvar EXTERNAL CSV \"/home/dave/test.csv,HOHEADING\";\n" +
			"Use optional NOHEADING to not treat first row as a heading row and use it as a data row.";
	}

	@Override
	public boolean isConnectionStringAFile() {
		return true;
	}

	@Override
	public String[] getAppropriateFileExtension() {
		return new String[] {"csv"};
	}

}
