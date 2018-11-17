Rel -- An implementation of Date and Darwen's Tutorial D.

Rel is a self-contained, complete, desktop database management system
with a user-friendly graphical user interface, ideal for creating and
maintaining personal, workgroup, and classroom databases.

*** GETTING STARTED ***

1. Download the file appropriate for your platform:

   Rel3.xxx.win32.win32.x86_64.zip      Windows 64 bit
   Rel3.xxx.dmg                         MacOS 64 bit
   Rel3.xxx.linux.gtk.x86_64.tar.gz     Linux 64 bit

2. Open the downloaded archive to extract the Rel folder.
   Move the Rel folder to a convenient location.
   
   On MacOS, it will running the .dmg will start a
   minimal installer to help you copy the Rel application to 
   your Applications folder.

3. Download and unzip the Rel / Tutorial D source code examples to a
   convenient location:

   RelExamplesAndUtilities_3.xxx.zip

== If you are upgrading from a previous version of Rel, please ==
== see IMPORTANT INSTRUCTIONS FOR UPGRADING, below. ==

4. To run Rel, open the Rel folder or directory and run the Rel
   executable. It's normally shown with a Rel icon.

*** USING THE STAND-ALONE REL DBMS ***

Whilst Rel is a self-contained, complete, desktop database management
system with a user-friendly graphical user interface that is ideal for
creating and maintaining personal databases, you can optionally also
run a standalone, "headless" Rel DBMS server that can be accessed from
multiple Rel desktop database management systems and/or from software
you write. This is ideal for providing access to shared databases for
workgroups and classrooms.
   
If you wish to use the standalone Rel DBMS -- which is not needed to
use Rel -- download the following and extract it to a suitable
directory on your host machine:

   Rel3.xxx.DBMS.tar.gz

It requires that a Java 11 runtime already be installed on the
machine. You can download a Java runtime from https://jdk.java.net/

** To run the stand-alone Rel DBMS server from the command line:

   RelDBMSServer

** The stand-alone Rel DBMS server provides various options.  To see
   these, type:

   RelDBMSServer -?

** To run the stand-alone command-line Rel language interpreter from
   your operating system command-line, go to the Rel folder or
   directory and run:

   RelDBMS

** The stand-alone command-line Rel language interpreter provides
   various options.  To see these, type:
   
   RelDBMS -?

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

Copyright (c) 2004-2018 Dave Voorhis
All Rights Reserved
