/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC LambdaExpression
*    Represents a captured lambda in the AST.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include "Environment.h"
#include "Expression.h"

namespace lispc
{
	class LambdaExpression : public Expression
	{
	public:
		LambdaExpression(Expression* body, std::vector<Symbol>& parameters, Environment* env = nullptr);
		virtual ~LambdaExpression() override;

		virtual bool is_atom() const override;
		virtual bool is_lambda() const override;

		virtual std::string get_type() const override;

		Environment* bind(std::vector<Expression*>& args);
		Expression* get_body() const;

	private:
		Expression* body;
		std::vector<Symbol> params;
		Environment* env;
	};
}