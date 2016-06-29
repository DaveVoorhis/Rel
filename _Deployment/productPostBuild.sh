#!/bin/bash

version=8u92
jredir=~/Documents/JavaJREs
proddir=~/git/Rel/_Deployment/product

# Linux GTK 32bit
cp -R $jredir/jre-$version-linux-i586 $proddir/linux.gtk.x86/Rel.app/jre
mv $proddir/linux.gtk.x86/Rel.app $proddir/linux.gtk.x86/Rel
chmod +x $proddir/linux.gtk.x86/Rel/jre/bin/*

# Linux GTK 64bit
cp -R $jredir/jre-$version-linux-x64 $proddir/linux.gtk.x86_64/Rel.app/jre
mv $proddir/linux.gtk.x86_64/Rel.app $proddir/linux.gtk.x86_64/Rel
chmod +x $proddir/linux.gtk.x86_64/Rel/jre/bin/*

# Windows 32bit
cp -R $jredir/jre-$version-windows-i586 $proddir/win32.win32.x86/Rel.app/jre
mv $proddir/win32.win32.x86/Rel.app $proddir/win32.win32.x86/Rel

# Windows 64bit
cp -R $jredir/jre-$version-windows-x64 $proddir/win32.win32.x86_64/Rel.app/jre
mv $proddir/win32.win32.x86_64/Rel.app $proddir/win32.win32.x86_64/Rel

# OS X (64bit)
cp -R $jredir/jre-$version-macosx-x64/Contents/Home $proddir/macosx.cocoa.x86_64/Rel.app/Contents/MacOS/jre
xsltproc -o tmp.plist ./productMacOS_updatePlist.xslt $proddir/macosx.cocoa.x86_64/Rel.app/Contents/Info.plist
mv tmp.plist $proddir/macosx.cocoa.x86_64/Rel.app/Contents/Info.plist


