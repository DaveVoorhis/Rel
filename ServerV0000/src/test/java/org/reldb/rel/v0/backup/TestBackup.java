package org.reldb.rel.v0.backup;

import org.junit.Test;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;
import org.reldb.rel.v0.interpreter.Instance;

public class TestBackup {
    @Test
    public void backupFileCanBeFound() throws DatabaseFormatVersionException {
        var instance = new Instance("./target/testdb1", true, System.out);
        var database = instance.getDatabase();

        var backupScript = Backup.obtainBackupScript(database);
        assert(backupScript.length() > 0);
    }
}
