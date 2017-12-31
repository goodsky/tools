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
#include <fstream>
#include <iostream>
#include <string>
#include <vector>

#include "Eval.h"
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
	else if (argc == 2)
	{
		ifstream source;
		source.open(string(argv[1]));

		try 
		{
			Environment env;
			string line;

			int lineNumber = 1;

			while (getline(source, line))
			{
				cout << lineNumber++ << ") ";

				Expression* expression = parse(line);

				if (expression != nullptr)
				{
					Expression* result = eval(expression, &env);

					if (result == nullptr)
					{
						cout << "null";
					}
					else
					{
						cout << *result;
					}
				}

				cout << endl;
			}
		}
		catch (const std::exception& ex)
		{
			cout << "ERROR: ";
			cout << ex.what() << endl;
		}
	}
}

void repl()
{
	Environment env;

	cout << "LispC repl" << endl;
	while (true)
	{
		cout << ">>> ";

		std::string line;
		getline(cin, line);

		if (line.length() == 0 || line == "exit" || line == "quit")
			break;

		try 
		{
			Expression* expression = parse(line);
			Expression* result = eval(expression, &env);

			if (result == nullptr)
			{
				cout << "null" << endl;
			}
			else
			{
				cout << *result << endl;
			}
		}
		catch (const std::exception& ex)
		{
			cout << "ERROR: ";
			cout << ex.what() << endl;
		}
	}
}