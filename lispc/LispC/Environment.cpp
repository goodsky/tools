#include "Environment.h"

namespace lispc
{
	Environment::Environment()
	{
		env = standard_env();
	}

	void Environment::add(Symbol key, Expression* expression)
	{
		env[key] = expression;
	}

	Expression* Environment::get(Symbol key) const
	{
		auto pos = env.find(key);
		
		if (pos == env.end())
		{
			throw std::runtime_error("Undefined symbol " + key.str());
		}
		else
		{
			return pos->second;
		}
	}
}