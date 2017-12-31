#include "FuncExpression.h"

namespace lispc
{
	FuncExpression::FuncExpression(Func func) :
		func(func) {}

	FuncExpression::~FuncExpression() { }

	bool FuncExpression::is_atom() const { return true; }

	bool FuncExpression::is_func() const { return true; }

	std::string FuncExpression::get_type() const { return "FuncExpression"; }

	Expression* FuncExpression::invoke(std::vector<Expression*>& args)
	{
		return func(args);
	}
}