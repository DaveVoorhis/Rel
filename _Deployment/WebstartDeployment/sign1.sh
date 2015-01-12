#!/bin/sh
cp ../$1 jars
echo Password:
read -s PASSWORD
cd jars
../unsigner.sh $1
pack200 -r $1
jarsigner -tsa http://timestamp.digicert.com -keystore ../relkeys -storepass $PASSWORD -keypass $PASSWORD $1 DaveVoorhis
cd ..

