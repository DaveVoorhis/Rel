#!/bin/sh
rm relkeys
echo Password:
read -s PASSWORD
keytool -genkey -keystore relkeys -keypass $PASSWORD -storepass $PASSWORD -dname "CN=Dave Voorhis, OU=Rel, O=reldb.org, L=Derby, ST=Derbyshire, C=GB" -alias DaveVoorhis -validity 3600
