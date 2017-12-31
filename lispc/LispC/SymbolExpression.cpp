#include "SymbolExpression.h"

namespace lispc
{
	SymbolExpression::SymbolExpression(Symbol symbol) :
		symbol(symbol) {}

	SymbolExpression::~SymbolExpression() { }

	bool SymbolExpression::is_atom() const { return true; }

	bool SymbolExpression::is_symbol() const { return true; }

	Symbol SymbolExpression::get_symbol() const { return this->symbol; }

	std::string SymbolExpression::get_type() const { return "SymbolExpression"; }
}