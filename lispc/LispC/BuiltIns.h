/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC Built-In Functions
* 	 Place to define built-in functions
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include <map>
#include <string>
#include <vector>

#include "Expression.h"
#include "LambdaExpression.h"
#include "NumberExpression.h"
#include "Symbol.h"

namespace lispc
{
	std::map<Symbol, Expression*> standard_env();

	Expression* add(std::vector<Expression*>& args);
	Expression* sub(std::vector<Expression*>& args);
	Expression* mul(std::vector<Expression*>& args);
	Expression* div(std::vector<Expression*>& args);
	Expression* mod(std::vector<Expression*>& args);
	Expression* gt(std::vector<Expression*>& args);
	Expression* lt(std::vector<Expression*>& args);
	Expression* gte(std::vector<Expression*>& args);
	Expression* lte(std::vector<Expression*>& args);
	Expression* eq(std::vector<Expression*>& args);
}