This is the live development repository for Rel, an implementation of Date & Darwen's "Tutorial D" database language.

If you wish to work with the source code, it can be imported directly
into Eclipse.  

First, you'll need Eclipse.  RelUI development requires the 
Eclipse for RCP and RAP Developers download.  All the other subprojects
will compile with the Eclipse IDE for Java developers.

You'll need the JavaCC Eclipse plugin.  Download
and install it in Eclipse from http://eclipse-javacc.sourceforge.net/

Then, in Eclipse, select "File | Import... | Git | Projects from Git"
and pick "Clone URI".  The URI is
https://github.com/DaveVoorhis/Rel.git

See the Rel home page at http://reldb.org

See the _Deployment directory for conventional installable versions
of Rel.  These are executable Java .jar files, with names of the form
RelInstall-x.y.z.jar

See the _Deployment/WebDeployment directory for a Web Start
installable version of Rel.  Download Rel.jnlp and invoke it with
javaws (e.g., javaws Rel.jnlp) to run the Java Web Start installation,
or use the Launch button on http://reldb.org/

Read the _Deployment/README.txt file for further information on
running Rel.

For support or to discuss Rel, see the Rel Forum at
http://reldb.org/forum
