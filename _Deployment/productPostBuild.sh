#!/bin/bash

version=8u92
#cp -R ~/Documents/JavaJREs/jre-$version-linux-i586 ~/git/Rel/_Deployment/product/linux.gtk.x86/Rel.app/jre
#cp -R ~/Documents/JavaJREs/jre-$version-linux-x64 ~/git/Rel/_Deployment/product/linux.gtk.x86_64/Rel.app/jre
cp -R ~/Documents/JavaJREs/jre-$version-macosx-x64/Contents/Home ~/git/Rel/_Deployment/product/macosx.cocoa.x86_64/Rel.app/Contents/MacOS/jre
xsltproc -o ~/git/Rel/_Deployment/tmp.plist ~/git/Rel/_Deployment/productMacOS_updatePlist.xslt ~/git/Rel/_Deployment/product/macosx.cocoa.x86_64/Rel.app/Contents/Info.plist
mv ~/git/Rel/_Deployment/tmp.plist ~/git/Rel/_Deployment/product/macosx.cocoa.x86_64/Rel.app/Contents/Info.plist
#cp -R ~/Documents/JavaJREs/jre-$version-windows-i586 ~/git/Rel/_Deployment/product/win32.win32.x86/Rel.app/jre
#cp -R ~/Documents/JavaJREs/jre-$version-windows-x64 ~/git/Rel/_Deployment/product/win32.win32.x86_64/Rel.app/jre
