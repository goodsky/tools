/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * LispC - Lisp Scheme interpreter written in C++
 *   based on the better named Lispy written by Peter Norvig
 *   http://www.norvig.com/lispy.html
 *   https://en.wikipedia.org/wiki/Scheme_(programming_language)
 * 
 * What's it like to write a late binding interpreter in an early binding language?
 * 
 * written by Skyler Goodell
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#include <iostream>
#include <string>
#include <vector>

#include "Expression.h"
#include "Number.h"
#include "Parse.h"

using namespace std;
using namespace lispc;

void repl();

int main(int argc, char* argv[])
{
	if (argc == 1)
	{
		repl();
	}
}

void repl()
{
	cout << "LispC repl" << endl;
	while (true)
	{
		cout << ">>> ";

		std::string line;
		getline(cin, line);

		if (line.length() == 0 || line == "exit" || line == "quit")
			break;

		Expression* expression = parse(line);
		cout << *expression << endl;
	}
}