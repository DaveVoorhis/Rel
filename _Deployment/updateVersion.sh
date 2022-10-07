#!/bin/bash

newVersion=3.017

versionfile=../DBrowser/src/main/java/org/reldb/dbrowser/ui/version/Version.java
dbmsversionfile=../ServerV0000/src/main/java/org/reldb/rel/v0/version/Version.java

oldVersion=`awk 'c&&!--c;/getVersionNumber/{c=1}' "$versionfile" | awk '{print $2}' | sed 's/.$//' | xargs`

sed -i '' -e "s/return $oldVersion;/return $newVersion;/" $versionfile
sed -i '' -e "s/PRODUCT_VERSION = $oldVersion/PRODUCT_VERSION = $newVersion/" $dbmsversionfile

pushd ../
mvn versions:set -DnewVersion=$newVersion
mvn versions:commit
popd
