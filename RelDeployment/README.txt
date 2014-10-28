Rel -- An implementation of Date and Darwen's Tutorial D.

NOTE: As of version 1.0.11, Rel requires Oracle Java 
      version 1.7.x (also known as Java 7) or a higher version, 
      or a compatible equivalent.


*** IMPORTANT INSTRUCTIONS FOR UPGRADING: ***

1.  If you are upgrading from an older version of Rel, you *MUST* back
up your database *BEFORE* you install an upgrade!  

If you're using Rel version 0.3.8 or higher, you can make a backup
via DBrowser's "Backup" button.

If you're using Rel version 0.3.0 to 0.3.7, run the DatabaseToScript.d 
script in the Scripts directory.  This script will
emit your database as a new Tutorial D script.  To back up your
database, save the generated script.  To restore your database, run
the saved script.

If you are upgrading from a version of Rel prior to 0.3.0 (aka Rel2),
under the old version of Rel run the DatabaseToScript.d found in the
old version's Samples directory.  The script will emit your database
in a format that can be read by the 0.3.x versions of Rel, though it
may require some modifications.

2.  Once you have made a back-up, you must delete the current
database.  Delete all the "*.jdb" files in versions prior to
0.3.5.  Delete the Database subdirectory and its contents in
version 0.3.5 or higher.

3.  Install the upgrade.  A new database will be created.

4.  To restore your database, run the script you created and saved in
Step 1, above.


*** GETTING STARTED ***

To get started immediately, run DBrowser.  DBrowser will automatically
start a Rel server.

** To run DBrowser from the command line:

     DBrowser

** DBrowser provides various options.  To see these, type:

     DBrowser -?

** To run the stand-alone Rel server from the command line:

     RelServer

** The Rel server provides various options.  To see these,
   type:

     Rel -?

** To back up a Rel database, run the DatabaseToScript.d script
   in the Samples directory, or use the Backup button in DBrowser. **

For more information, please see http://dbappbuilder.sourceforge.net

Copyright (c) 2004-2014 Dave Voorhis
All Rights Reserved

