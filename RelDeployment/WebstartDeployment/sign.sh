#!/bin/sh
rm jars/*.jar
cp ~/git/Rel/RelDeployment/*.jar jars
rm jars/RelInstall*
echo Password:
read -s PASSWORD
for f in jars/*.jar
do
   pack200 -r $f
   jarsigner -tsa http://timestamp.digicert.com -keystore relkeys -storepass $PASSWORD -keypass $PASSWORD $f DaveVoorhis
done
