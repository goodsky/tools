/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC Parsing
* 	 1) tokenize into vector of string
* 	 2) parse into expressions
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

namespace lispc
{
	Expression* parse(std::string& program);

	std::vector<std::string> tokenize(std::string& program);

	Expression* read_from_tokens(std::vector<std::string> tokens);
}