package org.reldb.rel.v0.storage.relvars.external.relvar;

import org.reldb.rel.v0.storage.relvars.external.Info;
import org.reldb.rel.v0.storage.relvars.external.InfoComponent;
import org.reldb.rel.v0.storage.relvars.external.InfoComponentOption;

public class InfoRELVAR extends Info {
	
	@Override
	public String getIdentifier() {
		return "RELVAR";
	}

	@Override
	public String getDescription() {
		return "Connection to a specified relation-valued variable in a Rel database.";
	}

	@Override
	public String getConnectionStringDocumentation() {
		return
		  "Examples:\n" +
	      "\tVAR myvar EXTERNAL RELVAR \"host,reluser,reluser,relvar\";\n" +
	      "\tVAR myvar EXTERNAL RELVAR \"host,reluser,reluser,relvar,port\";\n" +
	      "DUP_COUNT and AUTOKEY are ignored. DUP_REMOVE is the default.";
	}

	private static class InfoComponentREL extends InfoComponent {
		private String documentation;
		
		InfoComponentREL(int componentNumber, String documentation) {
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
			new InfoComponentREL(0, "Host IP or domain name"),
			new InfoComponentREL(1, "Rel user name"),
			new InfoComponentREL(2, "Rel password"),
			new InfoComponentREL(3, "Rel relation-valued variable name"),
			new InfoComponentREL(4, "Host port") {
				public boolean isOptional() {return true;}
			}
		};
	}

	@Override
	public boolean isGuaranteedUnique() {
		return true;
	}
	
}
