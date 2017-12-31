/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC FuncExpression
* Represents a function call in the AST.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include <vector>

#include "Expression.h"

namespace lispc
{
	class FuncExpression : public Expression
	{
	public:
		FuncExpression(Func func);
		virtual ~FuncExpression() override;

		virtual bool is_atom() const override;
		virtual bool is_func() const override;

		virtual std::string get_type() const override;

		Expression* invoke(std::vector<Expression*>& args);

	private:
		Func func;
	};
}