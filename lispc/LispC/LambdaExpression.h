/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC ListExpression
* Represents a function and environment in the AST
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include "Expression.h"

namespace lispc
{
	class Environment;
	class LambdaExpression : public Expression
	{
	public:
		LambdaExpression(Func func, Environment* env = nullptr);
		virtual ~LambdaExpression() override;
		virtual bool is_atom() const override;
		virtual bool is_lambda() const override;
		Expression* invoke(std::vector<Expression*>& args);

	private:
		Func func;
		Environment* env;
	};
}