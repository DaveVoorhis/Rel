This is the live development repository for Rel, an implementation of Date & Darwen's "Tutorial D" database language.

If you wish to work with the source code, do this:

1. Download and install Eclipse for RCP and RAP Developers. Eclipse Neon or newer is required.  Run Eclipse.

2. Install the JavaCC Eclipse plugin via "Help | Install New Software".  The update site (i.e., the URL to put in "Work with", then press "Add") is http://eclipse-javacc.sourceforge.net/

3. Install NatTable (https://eclipse.org/nattable) -- used by the RelUI subproject -- via "Help | Install New Software".  The update site is http://download.eclipse.org/nattable/releases/1.4.0/repository/

4. (Optional) If you wish to support multi-platform builds, go to https://wiki.eclipse.org/Building and follow the instructions under "Preferred way of doing multi-platform builds". Version 4.6 or newer is required.

5. Select "File | Import... | Git | Projects from Git" and pick "Clone URI".  The URI is https://github.com/DaveVoorhis/Rel.git  Or, better yet, fork it on GitHub and send me a pull request when you've done something good!

6. To ensure that the relevant libraries are up-to-date in the RelUI subproject, run the /RelUI/lib/copy.bat script on Windows or /RelUI/lib/link.sh on Linux or OS X.  Build should be automatic, but it wouldn't hurt to do a "Project | Clean..." to ensure everything is freshly built.  If you're developing on Windows, you'll need to run the /RelUI/lib/copy.bat file whenever the referenced libraries are changed.

You're ready to develop Rel.

For further information, see the Rel home page at http://reldb.org

See the _Deployment directory for conventional installable versions of Rel.  These are currently executable Java .jar files, with names of the form RelInstall-x.y.z.jar  As of version 1.0.13 or later this might be replaced by a Web Start installer (see below), or something else.

Read the _Deployment/README.txt file for further information on running Rel.

For support or to discuss Rel, see the Rel Forum at http://reldb.org/forum
