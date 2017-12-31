#include "LambdaExpression.h"

namespace lispc
{
	LambdaExpression::LambdaExpression(Expression* body, std::vector<Symbol>& parameters, Environment* env) :
		body(body),
		params(parameters),
		env(env) {}

	LambdaExpression::~LambdaExpression() { }

	bool LambdaExpression::is_atom() const { return true; }

	bool LambdaExpression::is_lambda() const { return true; }

	std::string LambdaExpression::get_type() const { return "LambdaExpression"; }

	Environment* LambdaExpression::bind(std::vector<Expression*>& args)
	{
		// TODO: leak warning
		return new Environment(params, args, env);
	}

	Expression* LambdaExpression::get_body() const
	{
		return body;
	}
}