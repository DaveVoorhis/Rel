# ServerV0000

This is the core of the version 0000 database engine.

A future version 0001 database engine will provide the same
essential functionality, but the database format will be
incompatible. 

A full _Rel_ DBMS installation must provide all
prior database engine versions, so that if a prior version's
database is encountered, the prior version's database engine
can be used to emit the database as a database backup script for
loading into the latest version of the database engine.