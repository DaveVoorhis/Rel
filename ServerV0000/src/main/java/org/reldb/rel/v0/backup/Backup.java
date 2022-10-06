package org.reldb.rel.v0.backup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.reldb.rel.exceptions.ExceptionFatal;

import org.reldb.rel.v0.interpreter.Interpreter;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.version.Version;

public class Backup {
	public static String obtainBackupScript(RelDatabase database) {
		try {
			var loader = database.getClass().getClassLoader();
			var scriptLocation = "scripts/v" + Version.getDatabaseVersion() + "/DatabaseToScript.rel";
			try (var backupScriptStream = loader.getResourceAsStream(scriptLocation)) {
				if (backupScriptStream == null) {
					throw new ExceptionFatal("RS0414: Unable to find backup script at " + scriptLocation);
				}
				var buffer = new ByteArrayOutputStream();
				backupScriptStream.transferTo(buffer);
				return buffer.toString(StandardCharsets.UTF_8);
			}
		} catch (IOException ioe) {
			throw new ExceptionFatal("RS0412: Unable to load backup script", ioe);
		}
	}

	public static boolean backup(RelDatabase database, PrintStream outputStream) {
		var backupScript = obtainBackupScript(database);
		try {
			Interpreter.executeStatement(database, backupScript, outputStream);
			return true;
		} catch (Exception e) {
			throw new ExceptionFatal("RS0413: Unable to run backup script", e);
		}
	}
}
