#!/bin/sh
java -cp lib/rel/*:lib/rel/misc/*:lib/jdt/* -jar lib/rel/RelDBMS.jar -D $*
