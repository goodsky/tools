/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC NumberExpression
* Represents a number in the AST
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include "Expression.h"

namespace lispc
{
	class NumberExpression : public Expression
	{
	public:
		NumberExpression(Number number);
		virtual ~NumberExpression() override;

		virtual bool is_atom() const override;
		virtual bool is_number() const override;

		virtual Number get_number() const override;

	private:
		Number number;
	};
}