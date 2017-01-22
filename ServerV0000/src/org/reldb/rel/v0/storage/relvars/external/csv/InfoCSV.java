package org.reldb.rel.v0.storage.relvars.external.csv;

import org.reldb.rel.v0.storage.relvars.external.Info;
import org.reldb.rel.v0.storage.relvars.external.InfoComponent;
import org.reldb.rel.v0.storage.relvars.external.InfoComponentOption;

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
					return new String[] {"csv"};
				}
				@Override
				public String getDocumentation() {
					return "Path to CSV file.";
				}
				@Override
				public InfoComponentOption[] getOptions() {
					return null;
				}
			},
			new InfoComponent(1) {
				@Override
				public boolean isOptional() {
					return true;
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
					return "Select NOHEADING if the file does not start with a heading row.";
				}
				@Override
				public InfoComponentOption[] getOptions() {
					return new InfoComponentOption[] {
						new InfoComponentOption() {
							@Override
							public String getDocumentation() {
								return "File does not start with a heading row.";
							}
							@Override
							public String getOptionText() {
								return "NOHEADING";
							}
						}
					};
				}
			}
		};
	}

}
