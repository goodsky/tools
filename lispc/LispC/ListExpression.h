/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC ListExpression
* Represents a list of expressions in the AST
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include "Expression.h"

namespace lispc
{
	class ListExpression : public Expression
	{
	public:
		ListExpression(std::vector<Expression*>& exps);
		virtual ~ListExpression() override;

		virtual std::vector<Expression*> get_exps() const override;

		virtual std::string get_type() const override;

	private:
		std::vector<Expression*> exps;
	};
}