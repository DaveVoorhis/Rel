#!/bin/bash

# This script constructs distributable Rel products. It is intended to run on MacOS.
#
# It assumes Maven is installed to drive the Java build stages.
#
# It assumes copies of Java JDKs are available in the folder denoted by $jredir,
# below, which expects to find untarred JDKs in linux, osx, and windows folders,
# respectively. Each JDK should be untarred but in its folder, so the expected
# directory subtree for JDK version 11.0.1 would be:
# 
# OpenJDKs
#   linux
#      jdk-11.0.1
#         bin 
#         ...etc...
#   osx
#      jdk-11.0.1.jdk
#         bin 
#         ...etc...
#   windows
#      jdk-11.0.1
#         bin 
#         ...etc...
#

relversion=3.015
javaversion=jdk-19
jredir=~/Documents/OpenJDKs
proddir=~/git/Rel/_Deployment/product

linuxtarget=linux
mactarget=macos
wintarget=windows

linuxTargetDBMS=linuxDBMS
macosTargetDBMS=macosDBMS
windowsTargetDBMS=windowsDBMS

# Clear
mkdir $proddir &>/dev/null
./clearProduct.sh
rm `find ./ -name .DS_Store -print` &>/dev/null

# Java build
pushd ../
mvn clean install
popd

# Grammar
~/bin/jjdoc ../ServerV0000/src/org/reldb/rel/v0/languages/tutoriald/definition/TutorialD.jjt
mv TutorialD.html $proddir

# Scripts
cp -R Scripts $proddir/RelScripts
pushd $proddir/
zip -9r Rel_ExamplesAndUtilities_$relversion.zip RelScripts
popd

# Build JREs
pushd MakeJRE
./build.sh
popd

# Linux GTK 64bit
echo "---------------------- DBrowser full Linux Build ----------------------"
linuxtargetRel=$linuxtarget/Rel
mkdir -p $proddir/$linuxtargetRel
cp -R MakeJRE/Linux/jre $proddir/$linuxtargetRel/jre
cp nativeLaunchers/binaries/Linux/Rel $proddir/$linuxtargetRel
mkdir $proddir/$linuxtargetRel/doc
cp doc/* $proddir/$linuxtargetRel/doc
cp doc/LICENSE.txt $proddir/$linuxtargetRel
cp -R ../DBrowser/target/lib $proddir/$linuxtargetRel
rm $proddir/$linuxtargetRel/lib/org.eclipse.swt*
rm $proddir/$linuxtargetRel/lib/swt_*
cp ../DBrowser/target/*.jar $proddir/$linuxtargetRel/lib
cp ../swtNative/swt_linux/target/lib/* $proddir/$linuxtargetRel/lib
cp ../swtNative/swt_linux/target/*.jar $proddir/$linuxtargetRel/lib
cp nativeLaunchers/Rel/Linux/Rel.ini $proddir/$linuxtargetRel/lib
cp splash.png $proddir/$linuxtargetRel/lib
chmod +x $proddir/$linuxtargetRel/jre/bin/*
pushd $proddir/$linuxtarget
tar cfz ../Rel$relversion.$linuxtarget.tar.gz Rel
popd

# MacOS (64bit)
echo "---------------------- DBrowser full MacOS Build ----------------------"
mkdir $proddir/$mactarget
cp -R nativeLaunchers/binaries/MacOS/Rel.app $proddir/$mactarget
cp nativeLaunchers/binaries/MacOS/launchBinSrc/Rel $proddir/$mactarget/Rel.app/Contents/MacOS
mkdir $proddir/$mactarget/Rel.app/Contents/MacOS/doc
cp doc/* $proddir/$mactarget/Rel.app/Contents/MacOS/doc
rm $proddir/$mactarget/Rel.app/Contents/MacOS/README.txt
cp doc/LICENSE.txt $proddir/$mactarget/Rel.app/Contents/MacOS
cp -R MakeJRE/MacOS/jre $proddir/$mactarget/Rel.app/Contents/MacOS/jre
cp -R ../DBrowser/target/lib $proddir/$mactarget/Rel.app/Contents/MacOS/
rm $proddir/$mactarget/Rel.app/Contents/MacOS/lib/org.eclipse.swt*
rm $proddir/$mactarget/Rel.app/Contents/MacOS/lib/swt_*
cp ../DBrowser/target/*.jar $proddir/$mactarget/Rel.app/Contents/MacOS/lib
cp ../swtNative/swt_macos/target/lib/* $proddir/$mactarget/Rel.app/Contents/MacOS/lib
cp ../swtNative/swt_macos/target/*.jar $proddir/$mactarget/Rel.app/Contents/MacOS/lib
cp nativeLaunchers/Rel/MacOS/Rel.ini $proddir/$mactarget/Rel.app/Contents/MacOS/lib
cp splash.png $proddir/$mactarget/Rel.app/Contents/MacOS/lib
cp OSXPackager/Background.png $proddir/$mactarget
cp OSXPackager/Package.command $proddir/$mactarget
pushd $proddir/$mactarget
./Package.command $relversion
mv *.dmg $proddir
rm Background.png
rm Package.command
popd

# Windows 64bit
echo "---------------------- DBrowser full Windows Build ----------------------"
wintargetRel=$wintarget/Rel
mkdir -p $proddir/$wintargetRel
cp -R MakeJRE/Windows/jre $proddir/$wintargetRel/jre
cp nativeLaunchers/binaries/Windows/x64/Release/Rel.exe $proddir/$wintargetRel
mkdir $proddir/$wintargetRel/doc
cp doc/* $proddir/$wintargetRel/doc
cp doc/LICENSE.txt $proddir/$wintargetRel
cp -R ../DBrowser/target/lib $proddir/$wintargetRel
rm $proddir/$wintargetRel/lib/org.eclipse.swt*
rm $proddir/$wintargetRel/lib/swt_*
cp ../DBrowser/target/*.jar $proddir/$wintargetRel
cp ../swtNative/swt_win/target/lib/* $proddir/$wintargetRel/lib
cp ../swtNative/swt_win/target/*.jar $proddir/$wintargetRel/lib
cp nativeLaunchers/Rel/Windows/Rel.ini $proddir/$wintargetRel/lib
cp splash.png $proddir/$wintargetRel/lib
pushd $proddir/$wintarget
zip -9r ../Rel$relversion.$wintarget.zip Rel
popd

# Get lib
cp -R ../Server/target/lib .
cp ../Server/target/*.jar lib
cp ../Tests/target/*.jar lib

# Standalone Rel DBMS (Linux)
echo "---------------------- Standalone DBMS Build (Linux) ----------------------"
tar cf $proddir/Rel$relversion.$linuxTargetDBMS.tar doc/* lib/*
pushd nativeLaunchers/RelDBMS/Linux
tar rf $proddir/Rel$relversion.$linuxTargetDBMS.tar *
popd
pushd doc
tar rf $proddir/Rel$relversion.$linuxTargetDBMS.tar LICENSE.txt
popd
pushd MakeJRE/Linux
tar rf $proddir/Rel$relversion.$linuxTargetDBMS.tar *
popd
pushd $proddir
gzip -9 Rel$relversion.$linuxTargetDBMS.tar
popd

# Standalone Rel DBMS (MacOS)
echo "---------------------- Standalone DBMS Build (MacOS) ----------------------"
tar cf $proddir/Rel$relversion.$macosTargetDBMS.tar doc/* lib/*
pushd nativeLaunchers/RelDBMS/MacOS
tar rf $proddir/Rel$relversion.$macosTargetDBMS.tar *
popd
pushd doc
tar rf $proddir/Rel$relversion.$macosTargetDBMS.tar LICENSE.txt
popd
pushd MakeJRE/MacOS
tar rf $proddir/Rel$relversion.$macosTargetDBMS.tar *
popd
pushd $proddir
gzip -9 Rel$relversion.$macosTargetDBMS.tar
popd

# Standalone Rel DBMS (Windows)
echo "---------------------- Standalone Windows DBMS Build (Windows) ----------------------"
zip -9r $proddir/Rel$relversion.$windowsTargetDBMS.zip doc/* lib/*
pushd nativeLaunchers/RelDBMS/Windows
zip -9r $proddir/Rel$relversion.$windowsTargetDBMS.zip *
popd
pushd doc
zip -9r $proddir/Rel$relversion.$windowsTargetDBMS.zip LICENSE.txt
popd
pushd MakeJRE/Windows
zip -9r $proddir/Rel$relversion.$windowsTargetDBMS.zip *
popd

# Cleanup
echo "Cleanup..."
rm -rf lib
rm -rf MakeJRE/Linux
rm -rf MakeJRE/MacOS
rm -rf MakeJRE/Windows

echo "Done."
