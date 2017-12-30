/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC Symbol
* A string key to reference an entity in the symbol environment
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include <iostream>
#include <string>

namespace lispc
{
	class Symbol 
	{
	public:
		Symbol(const char* symbol);
		Symbol(std::string& symbol);
		Symbol(const Symbol& other);
		Symbol& operator=(const Symbol& other);

		std::string str() const;

		friend bool operator<(const Symbol& s1, const Symbol& s2);
		friend bool operator>(const Symbol& s1, const Symbol& s2);
		friend bool operator==(const Symbol& s1, const Symbol& s2);
		friend std::ostream& operator<<(std::ostream& stream, const Symbol& symbol);

	private:
		std::string symbol;
	};
}