package org.reldb.dbrowser.ui.backup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.reldb.dbrowser.DBrowser;
import org.reldb.dbrowser.ui.crash.CrashTrap;
import org.reldb.rel.client.connection.string.ClientFromURL;
import org.reldb.rel.client.connection.string.StringReceiverClient;

public class Backup {

	private static FileDialog backupDialog;
	
	static String getSuggestedBackupFileName(String dbURL) {
		String fname;
		if (dbURL.startsWith("local:"))
			fname = dbURL.substring(6).replace('.', '_').replace('/', '_').replace('\\', '_').replace(':', '_').replace(' ', '_');
		else
			fname = dbURL.replace('.', '_').replace('/', '_').replace('\\', '_').replace(':', '_').replace(' ', '_');
		fname = fname.replace("__", "_");
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMMMM_dd_hh_mm_aaa");
		String timestamp = sdf.format(cal.getTime());
		return "relbackup_" + fname + "_" + timestamp + ".rel";
	}
	
	static BackupResponse backupToFile(String dbURL, File outputFile, CrashTrap crashTrap) {
		BufferedWriter outf;
		try {
			outf = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException ioe) {
			return new BackupResponse(ioe.toString(), "Unable to open output file", BackupResponse.ResponseType.ERROR, false);			
		}
		boolean receivedOk = false;
		long linesWritten = 0;
		StringReceiverClient client = null;
		try {
			// Fourth parameter can be additional jar file locations used by embedded Java compiler in Rel.  Not needed here.
			client = ClientFromURL.openConnection(dbURL, false, crashTrap, null);
			String r;
			client.sendExecute("BACKUP;");
			try {
				while ((r = client.receive()) != null) {
					if (r.equals("\n")) {
						continue;
					} else if (r.equals("Ok.")) {
						receivedOk = true;
						break;
					} else if (r.startsWith("ERROR:")) {
						outf.close();
						client.close();
						return new BackupResponse(r, "Error executing backup script", BackupResponse.ResponseType.ERROR, false);
					} else if (r.startsWith("NOTICE")) {
					} else {
						linesWritten++;
						outf.write(r);
						outf.newLine();
					}
				}
			} catch (IOException ioe) {
				outf.close();
				client.close();
				return new BackupResponse(ioe.toString(), "Error executing backup script", BackupResponse.ResponseType.ERROR, false);
			}			
			client.close();
		} catch (Throwable t) {
			try {
				outf.close();
				if (client != null)
					client.close();
			} catch (IOException e) {
				return new BackupResponse(e.toString(), "Error closing output file", BackupResponse.ResponseType.ERROR, false);
			}
			return new BackupResponse(t.toString(), "Unable to open database for backup", BackupResponse.ResponseType.ERROR, false);
		}
		try {
			outf.close();
		} catch (IOException ioe) {
			return new BackupResponse(ioe.toString(), "Unable to close output file", BackupResponse.ResponseType.ERROR, false);			
		}
		if (receivedOk) {
			if (linesWritten == 0)
				return new BackupResponse("No data was written to the backup.", "Backup failed", BackupResponse.ResponseType.ERROR, false);
			else {
				BufferedReader inf = null;
				long linesRead = 0;
				try {
					inf = new BufferedReader(new FileReader(outputFile));
					while (inf.readLine() != null)
						linesRead++;
				} catch (IOException ioe) {
					try {
						if (inf != null)
							inf.close();
					} catch (IOException ioe2) {
						return new BackupResponse(ioe2.toString(), "Unable to close backup file after reading it", BackupResponse.ResponseType.ERROR, false);			
					}
					return new BackupResponse(ioe.toString(), "Unable to read backup file", BackupResponse.ResponseType.ERROR, false);
				}
				try {
					if (inf != null)
						inf.close();
				} catch (IOException ioe2) {
					return new BackupResponse(ioe2.toString(), "Unable to close backup file after reading it", BackupResponse.ResponseType.ERROR, false);
				}
				if (linesWritten == linesRead)				
					return new BackupResponse("Backup Successful!  " + linesWritten + " lines were written to\n" + outputFile + "\n\nHowever, you should examine the file to make sure it is complete.", "Backup Complete", BackupResponse.ResponseType.INFORMATION, true);
				else
					return new BackupResponse("Backup Failed:  " + linesWritten + " lines were written to\n" + outputFile + "\nbut only " + linesRead + " could be read back.", "Backup Failed", BackupResponse.ResponseType.ERROR, false);
			}
		} else
			return new BackupResponse("The backup may be incomplete.  Please examine the backup!", "Backup Incomplete", BackupResponse.ResponseType.ERROR, false);
	}

	public static void makeBackup(String dbURL, CrashTrap crashTrap) {
		if (backupDialog == null) {
			backupDialog = new FileDialog(DBrowser.getShell(), SWT.SAVE);
			backupDialog.setFilterPath(System.getProperty("user.home"));
			backupDialog.setFilterExtensions(new String[] {"*.rel", "*.*"});
			backupDialog.setFilterNames(new String[] {"Rel script", "All Files"});
			backupDialog.setText("Save Backup");
			backupDialog.setOverwrite(true);
		}
		backupDialog.setFileName(getSuggestedBackupFileName(dbURL));
		String fname = backupDialog.open();
		if (fname == null)
			return;
		BackupResponse response = backupToFile(dbURL, new File(fname), crashTrap);
		response.showMessage();
	}

}