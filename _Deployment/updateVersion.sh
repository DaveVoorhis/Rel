#!/bin/bash

newVersion=3.017

versionfile=../Shared/src/main/java/org/reldb/rel/shared/Version.java

oldVersion=`grep "PRODUCT_VERSION =" "$versionfile" | awk '{print $7}' | sed 's/.$//' | xargs`
newCopyrightYear=`date +'%Y'`
oldCopyrightYear=`grep "COPYRIGHT_LAST_YEAR =" "$versionfile" | awk '{print $7}' | sed 's/.$//' | xargs`

echo "newVersion = ${newVersion}"
echo "oldVersion = ${oldVersion}"
echo "newCopyrightYear = ${newCopyrightYear}"
echo "oldCopyrightYear = ${oldCopyrightYear}"

sed -i '' -e "s/PRODUCT_VERSION = $oldVersion;/PRODUCT_VERSION = $newVersion;/" $versionfile
sed -i '' -e "s/COPYRIGHT_LAST_YEAR = $oldCopyrightYear;/COPYRIGHT_LAST_YEAR = $newCopyrightYear;/" $versionfile

pushd ../
mvn versions:set -DnewVersion=$newVersion
mvn versions:commit
popd
