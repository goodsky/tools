#include "Symbol.h"

namespace lispc
{
	Symbol::Symbol(const char* symbol) :
		symbol(std::string(symbol)) {}

	Symbol::Symbol(std::string& symbol) :
		symbol(symbol) {}

	Symbol::Symbol(const Symbol& other) :
		symbol(other.symbol) {}

	std::string Symbol::str() const
	{
		return symbol;
	}

	Symbol& Symbol::operator=(const Symbol& other)
	{
		return *this;
	}

	bool operator<(const Symbol& s1, const Symbol& s2)
	{
		return s1.symbol < s2.symbol;
	}

	bool operator>(const Symbol& s1, const Symbol& s2)
	{
		return s1.symbol > s2.symbol;
	}

	bool operator==(const Symbol& s1, const Symbol& s2)
	{
		return s1.symbol == s2.symbol;
	}

	std::ostream& operator<<(std::ostream& stream, const Symbol& symbol)
	{
		return stream << symbol.symbol;
	}
}