#!/bin/sh
unsign_help() {
   echo "unsigner [<option> | <filename>]"
   echo " "
   echo "<filename>   Unsign specified jar file."
   echo " "
   echo " OPTIONS:"
   echo "   -a        Unsign all signed jar files in Marvin."
   echo "   -h        Print this file."
   echo " "
   echo "Example: unsigner -a   or   unsigner sjars/svgexport.jar"
}

unsign() {
    echo "unsign $1"
    dir=`dirname $1`
    echo "$dir" >dir.tmp
    sed -e 's/\//\\\//g' dir.tmp > pattern.tmp
    sed -e 's/^/s\//' pattern.tmp| sed -e 's/$/\\\/\/\//' > pattern1.tmp
    fname=`echo $1 |sed -f pattern1.tmp`
    rm -f pattern.tmp pattern1.tmp dir.tmp

    cd $dir
    rm -rf unzip_tmp
    mkdir unzip_tmp
    unzip -q $fname -d unzip_tmp
    rm -f unzip_tmp/META-INF/*.SF
    rm -f unzip_tmp/META-INF/*.RSA
    
    # remove signing info from MANIFEST.MF
    TILL=`grep -n -m 1 "^Name:" unzip_tmp/META-INF/MANIFEST.MF`
    if [ ! "$TILL" == "" ]; then
        ROW=`echo $TILL|sed -e 's/:.*//'`
        let "ROW -= 1"
        head -n $ROW unzip_tmp/META-INF/MANIFEST.MF > ${fname}_MANIFEST.MF.tmp
        mv ${fname}_MANIFEST.MF.tmp unzip_tmp/META-INF/MANIFEST.MF
    fi
    
    cd unzip_tmp
    zip -q -r $fname *
    mv $fname ../.
    cd ..
    rm -rf unzip_tmp
}

unsign_all() {
    basedir=`dirname $0`
    echo basedir=$basedir
    cd $basedir
    echo "unsign all signed jar in `pwd`"
    sh unsigner.sh jmarvin.jar
    echo "other"
    find sjars -name "*.jar" -exec ./unsigner.sh {} \;
}

if [[ -n "$1" ]]; then
    if [[ "$1" == "-a" ]]; then
	unsign_all;
    elif [[ "$1" == "-h" ]]; then
	unsign_help;
    else
	unsign $1;
    fi
else
    unsign_help;
fi
