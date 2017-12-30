#include "Symbol.h"

namespace lispc
{
	Symbol::Symbol(std::string& symbol) :
		symbol(symbol) {}

	Symbol::Symbol(const Symbol& other) :
		symbol(other.symbol) {}

	std::string Symbol::get_symbol() const
	{
		return symbol;
	}

	Symbol& Symbol::operator=(const Symbol& other)
	{
		return *this;
	}
}