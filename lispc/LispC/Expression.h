/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC Expression base class
* A Scheme expression is composed of LISTS and ATOMS
* LIST is vector
* ATOM is a Number or a Symbol (string)
* LAMDA is a function bound to an environment
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include <iostream>
#include <vector>

#include "Number.h"
#include "Symbol.h"

namespace lispc
{
	// NB: Expression is a union of list and atom functionality.
	//     There is likely a more elegant type to achieve this.
	class Expression
	{
	public:
		Expression(std::string type);
		virtual ~Expression();

		virtual bool is_atom() const;
		virtual bool is_number() const;
		virtual bool is_symbol() const;
		virtual bool is_lambda() const;

		virtual std::vector<Expression*> get_exps() const;
		virtual Number get_number() const;
		virtual Symbol get_symbol() const;

	private:
		std::string type;
	};

	typedef Expression* (*Func)(std::vector<Expression*>&);

	std::ostream& operator<<(std::ostream& stream, const Expression& expression);
}