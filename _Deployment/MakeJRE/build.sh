#!/bin/sh

MODULES=makejre.reldb

JLINK=/Library/Java/JavaVirtualMachines/jdk-11.0.1.jdk/Contents/Home/bin/jlink

MODS_MACOS=~/Documents/OpenJDKs/osx/jdk-11.0.1.jdk/Contents/Home/jmods
MODS_LINUX=~/Documents/OpenJDKs/linux/jdk-11.0.1/jmods
MODS_WINDOWS=~/Documents/OpenJDKs/windows/jdk-11.0.1/jmods

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
$JLINK --module-path $MODS_LINUX:out --add-modules $MODULES --strip-debug --compress=2 --output Linux/jre

echo '  Building for MacOS...'
$JLINK --module-path $MODS_MACOS:out --add-modules $MODULES --strip-debug --compress=2 --output MacOS/jre

echo '  Building for Windows...'
$JLINK --module-path $MODS_WINDOWS:out --add-modules $MODULES --strip-debug --compress=2 --output Windows/jre

rm -rf out

echo 'JREs are ready.'
