#!/bin/bash

# This script constructs distributable Rel products.
#
# The generated product package files will be written to $proddir.
# It must be an absolute path.
#
# This is intended to run on MacOS, but will mostly work (it won't generate
# a Macos .dmg file) on any bash shell interpreter that provides the following:
#
# It assumes Maven is installed on the path, to drive the Java build stages.
#
# It assumes zip is installed on the path, to create .zip archives.
# It assumes tar is installed on the path, to create .tar and .tgz archives.
#
# It assumes jjtree and jjdoc (components of javacc) are referenced by
# $jjtree and $jjdoc, to generate the TutorialD.html grammar reference document.
#
# It assumes there is a JDK with javac and jlink binaries specified by
# $jlink and $javac, below. It should be the same version as the JDKs
# described in the next paragraph.
#
# It assumes copies of Java JDKs are available in the folder denoted by $jredir,
# with the version specified by $javaversion. It expects to find untarred
# JDKs in linux, osx, and windows folders, respectively. Each JDK should be
# untarred but in its folder, so the expected directory subtree for e.g.
# JDK version 11.0.1 (which must be in $javaversion, i.e., javaversion=11.0.1)
# would be:
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

proddir=~/git/Rel/_Deployment/product

jjtree=~/bin/jjtree
jjdoc=~/bin/jjdoc

jredir=~/Documents/OpenJDKs
javaversion=jdk-19

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

versionfile=../DBrowser/src/main/java/org/reldb/dbrowser/ui/version/Version.java
relversion=`awk 'c&&!--c;/getVersionNumber/{c=1}' "$versionfile" | awk '{print $2}' | sed 's/.$//' | xargs`

echo "Building version $relversion"

pushd () {
    command pushd "$@" > /dev/null
}

popd () {
    command popd "$@" > /dev/null
}

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

# Grammar
mkdir grammar
$jjtree -OUTPUT_DIRECTORY="./grammar" ../ServerV0000/src/main/java/org/reldb/rel/v0/languages/tutoriald/definition/TutorialD.jjt
$jjdoc ./grammar/TutorialD.jj
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

#
# DBrowser packages
#

echo "---------------------- DBrowser package (Linux) ----------------------"
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

echo "---------------------- DBrowser package (MacOS) ----------------------"
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
hdiutil=/usr/bin/hdiutil
if [ -f "$hdiutil" ]; then
  cp OSXPackager/Background.png OSXPackager/Package.command $targetBase
  pushd $targetBase
  ./Package.command $relversion
  mv *.dmg $proddir
  rm Background.png Package.command
  popd
else
  echo "Either this isn't MacOS or $hdiutil doesn't exist, so no MacOS DMG packaging."
  echo "Making a .tgz instead."
  pushd $targetBase
  tar cfz ../Rel$relversion.$mactarget.tar.gz Rel.app
  popd
fi

echo "---------------------- DBrowser package (Windows) ----------------------"
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
zip -q9r ../Rel$relversion.$wintarget.zip Rel
popd

echo "Prepare libraries for standalone DBMS packaging."
# Get lib
cp -R ../Server/target/lib .
cp ../Server/target/*.jar lib

#
# Standalone Rel DBMS packages
#

dbmsSubDir=StandAloneDBMS
DBMSDir=$proddir/$dbmsSubDir
mkdir -p $DBMSDir

echo "---------------------- Standalone DBMS package (Linux) ----------------------"
target=$DBMSDir/Rel$relversion.$linuxTargetDBMS.tar
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
pushd $DBMSDir
gzip -9 Rel$relversion.$linuxTargetDBMS.tar
popd

echo "---------------------- Standalone DBMS package (MacOS) ----------------------"
target=$DBMSDir/Rel$relversion.$macosTargetDBMS.tar
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
pushd $DBMSDir
gzip -9 Rel$relversion.$macosTargetDBMS.tar
popd

echo "---------------------- Standalone DBMS package (Windows) ----------------------"
target=$DBMSDir/Rel$relversion.$windowsTargetDBMS.zip
zip -q9r $target doc/* lib/*
pushd nativeLaunchers/RelDBMS/Windows
zip -q9r $target *
popd
pushd doc
zip -q9r $target LICENSE.txt
popd
pushd MakeJRE/Windows
zip -q9r $target *
popd

# Ancillary docs
echo "Copying ancillary documents..."
cp doc/LICENSE.txt doc/README.txt ReleaseNotes/RELEASE_$relversion.txt $proddir

# Cleanup
echo "Cleanup..."
rm -rf lib MakeJRE/Linux MakeJRE/MacOS MakeJRE/Windows $proddir/RelScripts $proddir/linux $proddir/macos $proddir/windows

echo "*** Done. ***"
