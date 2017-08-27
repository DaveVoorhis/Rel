#!/bin/bash

relversion=3.008
javaversion=8u144
jredir=~/Documents/JavaJREs
proddir=~/git/Rel/_Deployment/product

# Grammar
~/bin/jjdoc ../ServerV0000/src/org/reldb/rel/v0/languages/tutoriald/parser/TutorialD.jj
mv TutorialD.html $proddir

# Scripts
rm -rf Scripts/.DS_Store
cp -R Scripts $proddir/RelScripts
pushd $proddir/
zip -9r Rel_ExamplesAndUtilities_$relversion.zip RelScripts
popd

# Linux GTK 64bit
cp -R $jredir/jre-$javaversion-linux-x64 $proddir/linux.gtk.x86_64/Rel.app/jre
mv $proddir/linux.gtk.x86_64/Rel.app $proddir/linux.gtk.x86_64/Rel
chmod +x $proddir/linux.gtk.x86_64/Rel/jre/bin/*
pushd $proddir/linux.gtk.x86_64
rm ../Rel$relversion.linux.gtk.x86_84.tar.gz
tar cfz ../Rel$relversion.linux.gtk.x86_84.tar.gz Rel
popd

# Windows 32bit
cp -R $jredir/jre-$javaversion-windows-i586 $proddir/win32.win32.x86/Rel.app/jre
mv $proddir/win32.win32.x86/Rel.app $proddir/win32.win32.x86/Rel
pushd $proddir/win32.win32.x86
rm ../Rel$relversion.win32.win32.x86.zip
zip -9r ../Rel$relversion.win32.win32.x86.zip Rel
popd

# Windows 64bit
cp -R $jredir/jre-$javaversion-windows-x64 $proddir/win32.win32.x86_64/Rel.app/jre
mv $proddir/win32.win32.x86_64/Rel.app $proddir/win32.win32.x86_64/Rel
pushd $proddir/win32.win32.x86_64
rm ../Rel$relversion.win32.win32.x86_64.zip
zip -9r ../Rel$relversion.win32.win32.x86_64.zip Rel
popd

# OS X (64bit)
cp -R $jredir/jre-$javaversion-macosx-x64/Contents/Home $proddir/macosx.cocoa.x86_64/Rel.app/Contents/MacOS/jre
xsltproc -o tmp.plist ./productMacOS_updatePlist.xslt $proddir/macosx.cocoa.x86_64/Rel.app/Contents/Info.plist
mv tmp.plist $proddir/macosx.cocoa.x86_64/Rel.app/Contents/Info.plist
cp OSXPackager/Background.png $proddir/macosx.cocoa.x86_64
cp OSXPackager/Package.command $proddir/macosx.cocoa.x86_64
rm $proddir/*.dmg
pushd $proddir/macosx.cocoa.x86_64
./Package.command $relversion
mv *.dmg $proddir
rm Background.png
rm Package.command
popd

# Standalone Rel DBMS (Java)
tar cf $proddir/Rel$relversion.DBMS.tar RelDBMS RelDBMS.bat RelDBMSServer RelDBMSServer.bat RelTest RelTest.bat LICENSE.txt AUTHORS.txt CHANGES.txt LIBRARIES.txt TODO.txt README.txt lib/[a-z]*.jar lib/RelDBMS.jar lib/RelTest.jar
pushd $proddir
gzip -9 Rel$relversion.DBMS.tar
popd
