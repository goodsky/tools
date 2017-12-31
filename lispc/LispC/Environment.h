/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC Environment
* 	 Mapping of defined expressions.
*    Supports multiple scopes of environments in the case of lambdas.
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
		Environment(std::vector<Symbol>& params, std::vector<Expression*>& args, Environment* outer);

		void set(Symbol key, Expression* expression);
		Expression* get(Symbol key) const;
		Environment* find_scope(Symbol key);

	private:
		Environment(const Environment& other) = delete;

		std::map<Symbol, Expression*> env;
		Environment* outer;
	};
}