/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC Environment
* 	 Mapping of defined expressions.
*    Keeps track of built-in functions as well as defined symbols.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include <map>
#include <string>
#include <vector>

#include "BuiltIns.h"
#include "Expression.h"

namespace lispc
{
	class Environment
	{
	public:
		Environment();

		void add(Symbol key, Expression* expression);
		Expression* get(Symbol key) const;

	private:
		Environment(const Environment& other) = delete;

		std::map<Symbol, Expression*> env;
	};
}