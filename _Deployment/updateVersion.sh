#!/bin/bash

olddbrowserVersion=3.008
newdbrowserVersion=3.009

oldPRODUCT_VERSION=1
oldDATABASE_VERSION=0
oldREVISION=21
oldRELEASE=Beta

newPRODUCT_VERSION=1
newDATABASE_VERSION=0
newREVISION=22
newRELEASE=Beta

oldRelV=$oldPRODUCT_VERSION.$oldDATABASE_VERSION.$oldREVISION
newRelV=$newPRODUCT_VERSION.$newDATABASE_VERSION.$newREVISION

oldRelVersion=$oldRelV.$oldRELEASE
newRelVersion=$newRelV.$newRELEASE

sed -i '' -e "s/Bundle-Version: $oldRelVersion/Bundle-Version: $newRelVersion/" ../RelUI/META-INF/MANIFEST.MF

sed -i '' -e "s/$oldRelVersion/$newRelVersion/" ../RelUI/Rel.product

sed -i '' -e "s/return $olddbrowserVersion;/return $newdbrowserVersion;/" ../RelUI/src/org/reldb/dbrowser/ui/version/Version.java

sed -i '' -e "s/PRODUCT_VERSION = $oldPRODUCT_VERSION/PRODUCT_VERSION = $newPRODUCT_VERSION/" ../ServerV0000/src/org/reldb/rel/v0/version/Version.java
sed -i '' -e "s/DATABASE_VERSION = $oldDATABASE_VERSION/DATABASE_VERSION = $newDATABASE_VERSION/" ../ServerV0000/src/org/reldb/rel/v0/version/Version.java
sed -i '' -e "s/REVISION = $oldREVISION/REVISION = $newREVISION/" ../ServerV0000/src/org/reldb/rel/v0/version/Version.java
sed -i '' -e "s/RELEASE = $oldRELEASE/RELEASE = $newRELEASE/" ../ServerV0000/src/org/reldb/rel/v0/version/Version.java

sed -i '' -e "s/relversion=$olddbrowserVersion/relversion=$newdbrowserVersion/" ../_Deployment/productPostBuild.sh
