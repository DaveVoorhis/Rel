#!/bin/bash

#cp -R ~/Documents/JavaJREs/jre-8u40-linux-i586 ~/git/Rel/_Deployment/product/linux.gtk.x86/Rel/jre
#cp -R ~/Documents/JavaJREs/jre-8u40-linux-x64 ~/git/Rel/_Deployment/product/linux.gtk.x86_64/Rel/jre
#cp -R ~/Documents/JavaJREs/jre-8u40-macosx-x64/Contents/Home ~/git/Rel/_Deployment/product/macosx.cocoa.x86_64/Rel/jre
#cp -R ~/Documents/JavaJREs/jre-8u40-windows-i586 ~/git/Rel/_Deployment/product/win32.win32.x86/Rel/jre
#cp -R ~/Documents/JavaJREs/jre-8u40-windows-x64 ~/git/Rel/_Deployment/product/win32.win32.x86_64/Rel/jre

# Handle missing file.  See http://stackoverflow.com/questions/29542908/eclipse-rcp-os-x-preferences-about-menu-integration-works-inside-eclipse-but-not
cp /Applications/eclipse/plugins/org.eclipse.e4.ui.workbench.renderers.swt.cocoa_0.11.200.v20140417-0906.jar ~/git/Rel/_Deployment/product/macosx.cocoa.x86_64/Rel/plugins/
