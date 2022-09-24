#!/bin/bash

# This script constructs distributable Rel products. It is intended to run on MacOS.
#
# It assumes Maven is installed, to drive the Java build stages.
#
# It assumes jjtree and jjdoc (components of javacc) are installed in ~/bin, to
# generate the TutorialD.html grammar reference.
#
# It assumes there is a JDK ($hostjdk) with javac and jlink binaries specified by
# $jlink and $javac, below. It should be the same version as the JDKs
# described in the next paragraph.
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
#         Contents
#         ...etc...
#   windows
#      jdk-11.0.1
#         bin 
#         ...etc...
#

javaversion=jdk-19

jredir=~/Documents/OpenJDKs
proddir=~/git/Rel/_Deployment/product

hostjdk=/Library/Java/JavaVirtualMachines/$javaversion.jdk
hostjdkbin=$hostjdk/Contents/Home/bin
jlink=$hostjdkbin/jlink
javac=$hostjdkbin/javac

linuxtarget=linux
mactarget=macos
wintarget=windows

linuxTargetDBMS=linuxDBMS
macosTargetDBMS=macosDBMS
windowsTargetDBMS=windowsDBMS

versionfile=../DBrowser/src/org/reldb/dbrowser/ui/version/Version.java
relversion=`awk 'c&&!--c;/getVersionNumber/{c=1}' "$versionfile" | awk '{print $2}' | sed 's/.$//' | xargs`

echo "Building version $relversion"

# Clear
mkdir $proddir &>/dev/null
./clearProduct.sh
rm `find ./ -name .DS_Store -print` &>/dev/null

# Java build
pushd ../
mvn clean install
if [ ! "$?" -eq 0 ]; then
  popd
  echo "*** Build failed. ***"
  exit 1
fi
popd

# Verify build
pushd ../Tests/target
echo "----- Running Tests -----"
java -cp "lib/*:tests-$relversion.jar" AllTests
if [ ! "$?" -eq 0 ]; then
  rm -rf ./Reldb ./Relplugins ./Extensions ClickToOpen.rdb
  popd
  echo "*** Test(s) failed. ***"
  exit 1
fi
rm -rf ./Reldb ./Relplugins ./Extensions ClickToOpen.rdb
popd

# Grammar
mkdir grammar
~/bin/jjtree -OUTPUT_DIRECTORY="./grammar" ../ServerV0000/src/org/reldb/rel/v0/languages/tutoriald/definition/TutorialD.jjt
~/bin/jjdoc ./grammar/TutorialD.jj
mv TutorialD.html $proddir
rm -rf grammar

# Scripts
cp -R Scripts $proddir/RelScripts
pushd $proddir/
zip -9r Rel_ExamplesAndUtilities_$relversion.zip RelScripts
popd

# Build JREs
pushd MakeJRE
MODS_MACOS=$jredir/osx/$javaversion.jdk/Contents/Home/jmods
MODS_LINUX=$jredir/linux/$javaversion/jmods
MODS_WINDOWS=$jredir/windows/$javaversion/jmods
MODULES=makejre.reldb
OPTIONS="--strip-debug --compress=2 --no-header-files --no-man-pages"
echo 'Obtaining JREs...'
echo '  Removing previous build.'
rm -rf out Linux Windows MacOS
echo '  Compiling module-info.'
$javac -d out src/module-info.java
echo '  Compiling project.'
$javac -d out --module-path out src/org/reldb/makejre/*.java
mkdir Linux MacOS Windows
echo '  Building for Linux...'
$jlink --module-path $MODS_LINUX:out --add-modules $MODULES $OPTIONS --output Linux/jre
echo '  Building for MacOS...'
$jlink --module-path $MODS_MACOS:out --add-modules $MODULES $OPTIONS --output MacOS/jre
echo '  Building for Windows...'
$jlink --module-path $MODS_WINDOWS:out --add-modules $MODULES $OPTIONS --output Windows/jre
rm -rf out
echo 'JREs are ready.'
popd

# Linux GTK 64bit
echo "---------------------- DBrowser full Linux Build ----------------------"
targetBase=$proddir/$linuxtarget
target=$targetBase/Rel
mkdir -p $target
cp -R MakeJRE/Linux/jre $target/jre
cp nativeLaunchers/binaries/Linux/Rel $target
mkdir $target/doc
cp doc/* $target/doc
cp doc/LICENSE.txt $target
cp -R ../DBrowser/target/lib $target
rm $target/lib/org.eclipse.swt.* $target/lib/org.reldb.rel.swt_*
cp ../DBrowser/target/*.jar ../swtNative/swt_linux/target/lib/* ../swtNative/swt_linux/target/*.jar nativeLaunchers/Rel/Linux/Rel.ini splash.png $target/lib
chmod +x $target/jre/bin/*
pushd $targetBase
tar cfz ../Rel$relversion.$linuxtarget.tar.gz Rel
popd

# MacOS (64bit)
echo "---------------------- DBrowser full MacOS Build ----------------------"
targetBase=$proddir/$mactarget
target=$targetBase/Rel.app/Contents/MacOS
mkdir $targetBase
cp -R nativeLaunchers/binaries/MacOS/Rel.app $targetBase
cp nativeLaunchers/binaries/MacOS/launchBinSrc/Rel $target
mkdir $target/doc
cp doc/* $target/doc
rm $target/README.txt
cp doc/LICENSE.txt $target
cp -R MakeJRE/MacOS/jre $target/jre
cp -R ../DBrowser/target/lib $target/
rm $target/lib/org.eclipse.swt.* $target/lib/org.reldb.rel.swt_*
cp ../DBrowser/target/*.jar ../swtNative/swt_macos/target/lib/* ../swtNative/swt_macos/target/*.jar nativeLaunchers/Rel/MacOS/Rel.ini splash.png $target/lib
cp OSXPackager/Background.png OSXPackager/Package.command $targetBase
pushd $targetBase
./Package.command $relversion
mv *.dmg $proddir
rm Background.png Package.command
popd

# Windows 64bit
echo "---------------------- DBrowser full Windows Build ----------------------"
targetBase=$proddir/$wintarget
target=$targetBase/Rel
mkdir -p $target
cp -R MakeJRE/Windows/jre $target/jre
cp nativeLaunchers/binaries/Windows/x64/Release/Rel.exe $target
mkdir $target/doc
cp doc/* $target/doc
cp doc/LICENSE.txt $target
cp -R ../DBrowser/target/lib $target
rm $target/lib/org.eclipse.swt.* $target/lib/org.reldb.rel.swt_*
cp ../DBrowser/target/*.jar ../swtNative/swt_win/target/lib/* ../swtNative/swt_win/target/*.jar nativeLaunchers/Rel/Windows/Rel.ini splash.png $target/lib
pushd $targetBase
zip -9r ../Rel$relversion.$wintarget.zip Rel
popd

# Get lib
cp -R ../Server/target/lib .
cp ../Server/target/*.jar ../Tests/target/*.jar lib

# Standalone Rel DBMS (Linux)
echo "---------------------- Standalone DBMS Build (Linux) ----------------------"
target=$proddir/Rel$relversion.$linuxTargetDBMS.tar
tar cf $target doc/* lib/*
pushd nativeLaunchers/RelDBMS/Linux
tar rf $target *
popd
pushd doc
tar rf $target LICENSE.txt
popd
pushd MakeJRE/Linux
tar rf $target *
popd
pushd $proddir
gzip -9 Rel$relversion.$linuxTargetDBMS.tar
popd

# Standalone Rel DBMS (MacOS)
echo "---------------------- Standalone DBMS Build (MacOS) ----------------------"
target=$proddir/Rel$relversion.$macosTargetDBMS.tar
tar cf $target doc/* lib/*
pushd nativeLaunchers/RelDBMS/MacOS
tar rf $target *
popd
pushd doc
tar rf $target LICENSE.txt
popd
pushd MakeJRE/MacOS
tar rf $target *
popd
pushd $proddir
gzip -9 Rel$relversion.$macosTargetDBMS.tar
popd

# Standalone Rel DBMS (Windows)
echo "---------------------- Standalone Windows DBMS Build (Windows) ----------------------"
target=$proddir/Rel$relversion.$windowsTargetDBMS.zip
zip -9r $target doc/* lib/*
pushd nativeLaunchers/RelDBMS/Windows
zip -9r $target *
popd
pushd doc
zip -9r $target LICENSE.txt
popd
pushd MakeJRE/Windows
zip -9r $target *
popd

# Cleanup
echo "Cleanup..."
rm -rf lib MakeJRE/Linux MakeJRE/MacOS MakeJRE/Windows

echo "*** Done. ***"
