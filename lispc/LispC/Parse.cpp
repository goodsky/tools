#include <set>
#include <string>
#include <vector>

#include "Expression.h"
#include "Parse.h"

namespace lispc
{
	Expression* parse(std::string& program)
	{
		return read_from_tokens(tokenize(program));
	}

	std::vector<std::string> tokenize(std::string& program)
	{
		std::vector<std::string> tokens;

		int start = 0;
		bool isWhitespace = true;
		for (unsigned int i = 0; i < program.length(); ++i)
		{
			if (isWhitespace)
			{
				if (program[i] == ' ')
					continue;

				start = i;
				isWhitespace = false;
			}

			if (program[i] == '(' || program[i] == ')')
			{
				if (!isWhitespace && i - start > 0)
					tokens.push_back(program.substr(start, i - start));

				tokens.push_back(program.substr(i, 1));

				start = i + 1;
				isWhitespace = false;
			}
			else if (program[i] == ' ')
			{
				if (!isWhitespace && i - start > 0)
					tokens.push_back(program.substr(start, i - start));

				// start = i; whitespace doesn't need start updated
				isWhitespace = true;
			}
		}

		if (!isWhitespace && program.length() - start > 0)
			tokens.push_back(program.substr(start, program.length() - start));

		return tokens;
	}

	Expression* read_from_tokens(std::vector<std::string>& tokens, int& i)
	{
		std::string token = tokens[i++];
		if (token == "(")
		{
			std::vector<Expression*> expressions;
			
			while (tokens[i] != ")")
			{
				expressions.push_back(read_from_tokens(tokens, i));
			}

			++i;
			return new ListExpression(expressions);
		}
		else if (token == ")")
		{
			throw std::runtime_error("Unexpected ')'");
		}
		else
		{
			// atom
			// laughably inefficient - room to improve here
			try 
			{
				int intValue = std::stoi(token);
				return new NumberExpression(intValue);
			}
			catch (std::exception&)
			{
				try 
				{
					double doubleValue = std::stod(token);
					return new NumberExpression(doubleValue);
				}
				catch (std::exception&)
				{
					return new SymbolExpression(token);
				}
			}
		}
	}

	Expression* read_from_tokens(std::vector<std::string> tokens)
	{
		if (tokens.size() == 0)
			return nullptr;

		int i = 0;
		return read_from_tokens(tokens, i);
	}
}