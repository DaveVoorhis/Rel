This is the live development repository for Rel, an implementation of Date & Darwen's "Tutorial D" database language.

If you wish to work with the source code, do this:

1. Download and install Eclipse for RCP and RAP Developers.  Run Eclipse.

2. Install the JavaCC Eclipse plugin via "Help | Install New Software".  The update site (i.e., the URL to put in "Work with", then press "Add") is http://eclipse-javacc.sourceforge.net/

3. Install NatTable (https://eclipse.org/nattable) -- used by the RelUI subproject -- via "Help | Install New Software".  The update site is http://download.eclipse.org/nattable/releases/1.3.0/repository/

4. Select "File | Import... | Git | Projects from Git" and pick "Clone URI".  The URI is https://github.com/DaveVoorhis/Rel.git

You're ready to develop Rel.  Submit a pull request once you've done something good.

For further information, see the Rel home page at http://reldb.org

See the _Deployment directory for conventional installable versions of Rel.  These are currently executable Java .jar files, with names of the form RelInstall-x.y.z.jar  As of version 1.0.13 or later this might be replaced by a Web Start installer (see below), or something else.

See the _Deployment/WebDeployment directory for a Web Start installable version of Rel.  Download Rel.jnlp and invoke it with
javaws (e.g., javaws Rel.jnlp) to run the Java Web Start installation, or use the Launch button on http://reldb.org/

Read the _Deployment/README.txt file for further information on running Rel.

For support or to discuss Rel, see the Rel Forum at http://reldb.org/forum
