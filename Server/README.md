# Server

This provides a facade over the latest version of the
database engine, and if a database is encountered that
expects an earlier version of the database engine, uses
that database version to emit the database as a backup
script that can be loaded into the latest database
engine version.