#!/bin/sh
rm jars/*.jar
mkdir jars
cp ../*.jar jars
rm jars/RelInstall*
echo Password:
read -s PASSWORD
for f in jars/*.jar
do
   ./unsigner.sh $f
   pack200 -r $f
   jarsigner -tsa http://timestamp.digicert.com -keystore relkeys -storepass $PASSWORD -keypass $PASSWORD $f DaveVoorhis
done
