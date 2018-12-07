#!/bin/bash

olddbrowserVersion=3.013
newdbrowserVersion=3.014

oldPRODUCT_VERSION=1
oldDATABASE_VERSION=0
oldREVISION=26
oldRELEASE=Beta

newPRODUCT_VERSION=1
newDATABASE_VERSION=0
newREVISION=27
newRELEASE=Beta

oldRelV=$oldPRODUCT_VERSION.$oldDATABASE_VERSION.$oldREVISION
newRelV=$newPRODUCT_VERSION.$newDATABASE_VERSION.$newREVISION

oldRelVersion=$oldRelV.$oldRELEASE
newRelVersion=$newRelV.$newRELEASE

sed -i '' -e "s/return $olddbrowserVersion;/return $newdbrowserVersion;/" ../DBrowser/src/org/reldb/dbrowser/ui/version/Version.java

sed -i '' -e "s/PRODUCT_VERSION = $oldPRODUCT_VERSION/PRODUCT_VERSION = $newPRODUCT_VERSION/" ../ServerV0000/src/org/reldb/rel/v0/version/Version.java
sed -i '' -e "s/DATABASE_VERSION = $oldDATABASE_VERSION/DATABASE_VERSION = $newDATABASE_VERSION/" ../ServerV0000/src/org/reldb/rel/v0/version/Version.java
sed -i '' -e "s/REVISION = $oldREVISION/REVISION = $newREVISION/" ../ServerV0000/src/org/reldb/rel/v0/version/Version.java
sed -i '' -e "s/RELEASE = $oldRELEASE/RELEASE = $newRELEASE/" ../ServerV0000/src/org/reldb/rel/v0/version/Version.java

sed -i '' -e "s/relversion=$olddbrowserVersion/relversion=$newdbrowserVersion/" ../_Deployment/buildProduct.sh
