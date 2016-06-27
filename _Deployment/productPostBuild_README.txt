For Mac OS X:
	Copy JRE to Rel.app/Contents/MacOS/jre
	Edit Rel.app/Contents/Info.plist to add
		<string>-vm</string><string>jre/bin/java</string> inside <array> ... </array> after <key>Eclipse</key>

For Linux:
	Copy JRE to Rel.app/jre
	
For Windows:
	Copy JRE to Rel.app/jre

	