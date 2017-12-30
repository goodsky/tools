/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC Parsing
* 	 1) tokenize into vector of string
* 	 2) parse into expressions
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include <set>
#include <string>
#include <vector>

#include "Expression.h"
#include "ListExpression.h"
#include "SymbolExpression.h"
#include "NumberExpression.h"

namespace lispc
{
	Expression* parse(std::string& program);

	std::vector<std::string> tokenize(std::string& program);

	Expression* read_from_tokens(std::vector<std::string> tokens);
}