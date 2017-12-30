#include "Eval.h"

namespace lispc
{
	Expression* eval(Expression* expression, Environment& env)
	{
		if (expression->is_number())
		{
			return expression;
		}
		else if (expression->is_symbol())
		{
			return env.get(expression->get_symbol());
		}
		else // ListExpression
		{
			auto nested = expression->get_exps();

			if (nested.size() == 0)
				throw std::runtime_error("SYNTAX: Empty expression is not allowed.");

			Expression* instruction = nested[0];

			if (instruction->is_number())
			{
				if (nested.size() != 1)
					throw std::runtime_error("SYNTAX: Numeric expression is not an instruction.");

				return instruction;
			}
			else if (instruction->is_symbol())
			{
				Symbol key = instruction->get_symbol();

				// Special-Case Expressions
				if (key == "if")
				{
					if (nested.size() != 4)
						throw std::runtime_error("SYNTAX: if expression has invalid argument count. Expected 4.");

					Expression* condition = eval(nested[1], env);

					if (!condition->is_number())
						throw std::runtime_error("RUNTIME: if expression did not contain a boolean condition.");

					Number conditionNumber = condition->get_number();
					return (conditionNumber.get_int() == 1) ?
						eval(nested[2], env) :
						eval(nested[3], env);
				}
				else if (key == "define")
				{
					if (nested.size() != 3)
						throw std::runtime_error("SYNTAX: define expression has invalid argument count. Expected 3.");

					Expression* symbol = nested[1];
					if (!symbol->is_symbol())
						throw new std::runtime_error("SYNTAX: define expression requires a symbol.");

					env.add(symbol->get_symbol(), nested[2]);
				}
				else // Resolve Symbol
				{
					Expression* proc = eval(instruction, env);

					if (proc->is_number())
					{
						return proc;
					}
					else if (proc->is_lambda())
					{
						LambdaExpression* lambda = static_cast<LambdaExpression*>(proc);

						std::vector<Expression*> resolvedArgs;

						resolvedArgs.push_back(proc);
						for (unsigned int i = 1; i < nested.size(); ++i)
							resolvedArgs.push_back(eval(nested[i], env));

						return lambda->invoke(resolvedArgs);
					}
					else
					{
						throw std::runtime_error("SYNTAX: Symbol did not resolve to a function.");
					}
				}
			}
			else
			{
				throw std::runtime_error("SYNTAX: Expected symbol or number inside list.");
			}
		}


		return nullptr;
	}
}