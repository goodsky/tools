#include "Environment.h"

namespace lispc
{
	Environment::Environment() : 
		outer(nullptr)
	{
		env = standard_env();
	}

	Environment::Environment(std::vector<Symbol>& params, std::vector<Expression*>& args, Environment* outer) :
		outer(outer),
		env() 
	{
		if (params.size() != args.size())
			throw std::runtime_error("Argument count does not match scope parameters.");

		for (unsigned int i = 0; i < params.size(); ++i)
			env[params[i]] = args[i];
	}

	void Environment::set(Symbol key, Expression* expression)
	{
		env[key] = expression;
	}

	Expression* Environment::get(Symbol key) const
	{
		auto pos = env.find(key);
		
		if (pos == env.end())
		{
			if (outer == nullptr)
				throw std::runtime_error("Undefined symbol " + key.str());

			return outer->get(key);
		}
		else
		{
			return pos->second;
		}
	}

	Environment* Environment::find_scope(Symbol key)
	{
		auto pos = env.find(key);

		if (pos == env.end())
		{
			if (outer == nullptr)
				throw std::runtime_error("Undefined symbol " + key.str());

			return outer->find_scope(key);
		}
		else
		{
			return this;
		}
	}
}