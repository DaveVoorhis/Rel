#!/bin/sh

MODULES=makejre.reldb

JAVA_VERSION=jdk-13

JLINK=/Library/Java/JavaVirtualMachines/$JAVA_VERSION.jdk/Contents/Home/bin/jlink

MODS_MACOS=~/Documents/OpenJDKs/osx/$JAVA_VERSION.jdk/Contents/Home/jmods
MODS_LINUX=~/Documents/OpenJDKs/linux/$JAVA_VERSION/jmods
MODS_WINDOWS=~/Documents/OpenJDKs/windows/$JAVA_VERSION/jmods

OPTIONS="--strip-debug --compress=2 --no-header-files --no-man-pages"

echo 'Obtaining JREs...'

echo '  Removing previous build.'
rm -rf out Linux Windows MacOS

echo '  Compiling module-info.'
javac -d out src/module-info.java

echo '  Compiling project.'
javac -d out --module-path out src/org/reldb/makejre/*.java

mkdir Linux
mkdir MacOS
mkdir Windows

echo '  Building for Linux...'
$JLINK --module-path $MODS_LINUX:out --add-modules $MODULES $OPTIONS --output Linux/jre

echo '  Building for MacOS...'
$JLINK --module-path $MODS_MACOS:out --add-modules $MODULES $OPTIONS --output MacOS/jre

echo '  Building for Windows...'
$JLINK --module-path $MODS_WINDOWS:out --add-modules $MODULES $OPTIONS --output Windows/jre

rm -rf out

echo 'JREs are ready.'
