/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC Symbol
* A string key to reference an entity in the symbol environment
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include <string>

namespace lispc
{
	class Symbol 
	{
	public:
		Symbol(std::string& symbol);
		Symbol(const Symbol& other);
		Symbol& operator=(const Symbol& other);

		std::string get_symbol() const;

	private:
		std::string symbol;
	};
}