/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC ListExpression
* Represents a symbol in the AST
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include "Expression.h"

namespace lispc
{
	class SymbolExpression : public Expression
	{
	public:
		SymbolExpression(Symbol symbol);
		virtual ~SymbolExpression() override;

		virtual bool is_atom() const override;
		virtual bool is_symbol() const override;

		virtual Symbol get_symbol() const override;

	private:
		Symbol symbol;
	};
}