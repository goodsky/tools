#include "LambdaExpression.h"

namespace lispc
{
	LambdaExpression::LambdaExpression(Func func, Environment* env) :
		Expression("LambdaExpression"),
		func(func),
		env(env) {}

	LambdaExpression::~LambdaExpression() { }

	bool LambdaExpression::is_atom() const { return true; }

	bool LambdaExpression::is_lambda() const { return true; }

	Expression* LambdaExpression::invoke(std::vector<Expression*>& args)
	{
		return func(args);
	}
}