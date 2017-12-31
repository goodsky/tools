#include "Eval.h"

namespace lispc
{
	Expression* eval(Expression* expression, Environment* env)
	{
		if (expression->is_number())
		{
			return expression;
		}
		else if (expression->is_symbol())
		{
			return env->get(expression->get_symbol());
		}
		else // ListExpression
		{
			auto inner = expression->get_exps();

			if (inner.size() == 0)
				throw std::runtime_error("SYNTAX: Empty expression is not allowed.");

			Expression* instruction = inner[0];

			if (instruction->is_number())
			{
				if (inner.size() != 1)
					throw std::runtime_error("SYNTAX: Numeric expression is not an instruction.");

				return instruction;
			}
			else if (instruction->is_symbol())
			{
				Symbol key = instruction->get_symbol();

				if (key == "quote") // quotation 
				{
					if (inner.size() != 2)
						throw std::runtime_error("SYNTAX: quote expression has invalid argument count. Expected 2.");

					return inner[1];
				}
				else if (key == "if") // conditional
				{
					if (inner.size() != 4)
						throw std::runtime_error("SYNTAX: if expression has invalid argument count. Expected 4.");

					Expression* condition = eval(inner[1], env);

					if (!condition->is_number())
						throw std::runtime_error("RUNTIME: if expression did not contain a boolean condition.");

					Number conditionNumber = condition->get_number();
					return (conditionNumber.get_int() == 1) ?
						eval(inner[2], env) :
						eval(inner[3], env);
				}
				else if (key == "define") // definition
				{
					if (inner.size() != 3)
						throw std::runtime_error("SYNTAX: define expression has invalid argument count. Expected 3.");

					Expression* symbol = inner[1];
					if (!symbol->is_symbol())
						throw new std::runtime_error("SYNTAX: define expression requires a symbol.");

					env->set(symbol->get_symbol(), eval(inner[2], env));
				}
				else if (key == "set") // assignment (I broke the language and named this 'set' instead of 'set!')
				{
					if (inner.size() != 3)
						throw std::runtime_error("SYNTAX: set expression has invalid argument count. Expected 3.");

					Expression* symbol = inner[1];
					if (!symbol->is_symbol())
						throw new std::runtime_error("SYNTAX: define expression requires a symbol.");

					Symbol key = symbol->get_symbol();
					env->find_scope(key)->set(key, eval(inner[2], env));
				}
				else if (key == "lambda") // procedure (lambda)
				{
					if (inner.size() != 3)
						throw std::runtime_error("SYNTAX: lambda expression has invalid argument count. Expected 3.");

					if (inner[1]->is_atom())
						throw std::runtime_error("SYNTAX: lambda expression requires list of parameters.");

					std::vector<Symbol> params;
					for (Expression* paramExp : inner[1]->get_exps())
					{
						if (!paramExp->is_symbol())
							throw std::runtime_error("SYNTAX: lambda expression has illegal parameter.");

						params.push_back(paramExp->get_symbol());
					}

					// TODO: leak warning
					return new LambdaExpression(inner[2], params, env);
				}
				else // Resolve Symbol
				{
					Expression* proc = eval(instruction, env);

					if (proc->is_number())
					{
						return proc;
					}
					else if (proc->is_func())
					{
						FuncExpression* func = static_cast<FuncExpression*>(proc);

						std::vector<Expression*> resolvedArgs;

						for (unsigned int i = 1; i < inner.size(); ++i)
							resolvedArgs.push_back(eval(inner[i], env));

						return func->invoke(resolvedArgs);
					}
					else if (proc->is_lambda())
					{
						LambdaExpression* lambda = static_cast<LambdaExpression*>(proc);

						std::vector<Expression*> resolvedArgs;
						for (unsigned int i = 1; i < inner.size(); ++i)
							resolvedArgs.push_back(eval(inner[i], env));

						return eval(lambda->get_body(), lambda->bind(resolvedArgs));
					}
					else
					{
						throw std::runtime_error("SYNTAX: Symbol did not resolve to a procedure.");
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