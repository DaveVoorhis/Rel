Rel -- An implementation of Date and Darwen's Tutorial D.

1. Download the file appropriate for your platform:

   Rel3.xxx.win32.win32.x86_64.zip      Windows 64 bit
   Rel3.xxx.win32.win32.x86.zip         Windows 32 bit
   Rel3.xxx.dmg                         Mac OS X 64 bit
   Rel3.xxx.linux.gtk.x86_64.tar.gz     Linux 64 bit

2. Open the downloaded archive to extract the Rel folder.
   Move the Rel folder to a convenient location.
   
   On Mac OS X, it will help you copy the application to 
   your Applications folder.

3. Download the Rel / Tutorial D source code examples:

   RelExamples_3.xxx.zip

4. (Optional) Rel is a self-contained, complete, desktop database
   management system with a user-friendly graphical user
   interface. You can optionally also run a standalone, "headless" Rel
   DBMS server that can be accessed from the Rel desktop database
   management system and/or from software you write. If you wish to
   use the standalone Rel DBMS -- which is not needed to use Rel --
   download the following and extract it to a suitable directory on
   your host machine:

   Rel3.xxx.DBMS.tar.gz

   It requires a Java 8 runtime.

== If you are upgrading from a previous version of Rel, please ==
== see IMPORTANT INSTRUCTIONS FOR UPGRADING, below. ==

*** GETTING STARTED ***

** To run Rel, open the Rel folder or directory and run the Rel
   executable. It's normally shown with a Rel icon.

** To run the stand-alone command-line Rel language interpreter from
   your operating system command-line, go to the Rel folder or
   directory and run:

   RelDBMS

** The stand-alone command-line Rel language interpreter provides
   various options.  To see these, type:
   
   RelDBMS -?

** To run the stand-alone Rel DBMS server from the command line:

   RelDBMSServer

** The stand-alone Rel DBMS server provides various options.  To see
   these, type:

   RelDBMSServer -?

NOTE: As of version 1.0.12, the standalone Rel DBMS requires Oracle
      Java version 1.8.x (also known as Java 8) or a higher version,
      or a compatible equivalent. The full Rel distribution --
      including the graphical user interface (aka "DBrowser") --
      includes Java, so there's usually no need to download
      Java. However, the first time you run Rel on Mac OS X, you may
      be asked to install Java 1.6, which (oddly) is not actually used
      by Rel, but is apparently required in order to launch it.

*** IMPORTANT INSTRUCTIONS FOR UPGRADING: ***

== If you are upgrading from a previous version of Rel, please ==
== pay attention to the following! ==

1.  You *MUST* back up your database *BEFORE* you install an upgrade!

If you are using Rel version 0.3.8 or higher, you can make a backup
via the graphical user interface's (aka DBrowser) "Backup" button.

If you are using Rel version 0.3.0 to 0.3.7, run the
DatabaseToScript.d script in the Scripts directory. This script will
emit your database as a new Tutorial D script. To back up your
database, save the generated script. To restore your database, run
the saved script.

If you are upgrading from a version of Rel prior to 0.3.0 (aka Rel2),
under the old version of Rel run the DatabaseToScript.d found in the
old version's Samples directory.  The script will emit your database
in a format that can be read by later versions of Rel, though it may
require some modifications.

Rel user interface (aka DBrowser) version 3.001 or above (which
includes the Rel DBMS version 1.0.13 or above) will automatically
attempt to upgrade any database created using Rel user interface
version 3.000 or above, or with the standalone Rel DBMS version 1.0.13
or above. Normally, the database upgrade is completely
automatic. However, in some cases the upgrade may not be able to
complete automatically. If this happens, it will tell you, and you may
need to edit the database backup script before you can successfully
restore the database.

However, Rel user interface version 3.000 and above cannot
automatically upgrade databases made with older Rel versions. You must
go to the command-line, load the backup script created in Step 1
above, and execute it.

For more information, please see http://reldb.org

Copyright (c) 2004-2017 Dave Voorhis
All Rights Reserved
