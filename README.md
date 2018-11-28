This is the live development repository for *Rel*, an implementation of Date & Darwen's **Tutorial D** database language.

If you wish to work with the source code, do this:

1. Install a Java 11 JDK from (for example) https://jdk.java.net

2. Download Eclipse for Java Developers version 2018-09 or above.

3. Run Eclipse.

4. Go to "Preferences | Java | Installed JREs" and install the above JDK and set it to be the default.

5. Install the JavaCC plugin from the Eclipse Marketplace.

6. Right-click in the Package Explorer and select "Import | Git | Projects from Git" to clone this project.

7. If you're developing on Windows or Linux, go to the DBrowser subproject and select "Properties | Java Build Path". Then:

    a) Select the "Projects" tab and remove the swt_macos Project and replace it with the swt_\<arch\> Project where \<arch\> is *linux* or *win* depending on your development platform.
    
    b) Select the "Libraries" tab and remove the swt_reldb.jar and swt.jar library dependencies -- currently set to MacOS versions -- and replace them with swt_reldb.jar and swt.jar found in \_Deployment/lib/swt/\<arch\>, where \<arch\> is *linux_64* or *win_64* depending on your development platform.

8. Run DBrowser in the DBrowser subproject to launch the *Rel* graphical user interface.

You're ready to develop *Rel*.

For further information, see the *Rel* home page at http://reldb.org

Ready-to-run distributions of *Rel* are available on SourceForge at https://sourceforge.net/projects/dbappbuilder/files/Rel/

Read the \_Deployment/README.txt file for further information on running *Rel*.

For support or to discuss *Rel*, see the *Rel* Forum at http://reldb.org/forum
