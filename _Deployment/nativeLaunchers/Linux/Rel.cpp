// Rel native launcher for Linux.

#include <iostream>
#include <cstdlib>
#include <unistd.h>

int main(int argc, char **argv)
{
	chdir(argv[0]);
	system("jre/bin/java -splash:lib/Splash.png -cp \"lib/*:lib/nattable/*:lib/swt/*:lib/swt/win_64/*\" DBrowser ");
}
