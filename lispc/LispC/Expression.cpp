#include "Expression.h"

namespace lispc
{
	Expression::~Expression() { }

	std::ostream& operator<<(std::ostream& stream, const Expression& expression)
	{
		if (expression.is_atom())
		{
			stream << "(atom ";

			if (expression.is_number())
			{
				stream << expression.get_number();
			}
			else
			{
				// I am disgusted.
				stream << expression.get_symbol().get_symbol();
			}

			return stream << ")";
		}
		else
		{
			stream << "(list ";
			for (Expression* nested : expression.get_exps())
			{
				stream << *nested << " ";
			}

			return stream << ")";
		}
	}

	// ListExpression ---------------------------------------------

	ListExpression::ListExpression(std::vector<Expression*>& exps) :
		exps()
	{
		for (unsigned int i = 0; i < exps.size(); ++i)
			this->exps.push_back(exps[i]);
	}

	ListExpression::~ListExpression() 
	{ 
		for (Expression* exp : this->exps)
		{
			delete exp;
		}
	}

	bool ListExpression::is_atom() const
	{
		return false;
	}

	bool ListExpression::is_number() const
	{
		return false;
	}

	bool ListExpression::is_symbol() const
	{
		return false;
	}

	std::vector<Expression*> ListExpression::get_exps() const
	{
		return this->exps;
	}

	Number ListExpression::get_number() const
	{
		throw std::runtime_error("ListExpression is not a Number.");
	}

	Symbol ListExpression::get_symbol() const
	{
		throw std::runtime_error("ListExpression is not a Symbol.");
	}

	// NumberExpression -----------------------------------------------

	NumberExpression::NumberExpression(Number number) :
		number(number) {}

	NumberExpression::~NumberExpression() { }

	bool NumberExpression::is_atom() const
	{
		return true;
	}

	bool NumberExpression::is_number() const
	{
		return true;
	}

	bool NumberExpression::is_symbol() const
	{
		return false;
	}

	std::vector<Expression*> NumberExpression::get_exps() const
	{
		throw std::runtime_error("NumberExpression is not a List.");
	}

	Number NumberExpression::get_number() const
	{
		return this->number;
	}

	Symbol NumberExpression::get_symbol() const
	{
		throw std::runtime_error("NumberExpression is not a Symbol.");
	}

	// SymbolExpression ----------------------------------------------

	SymbolExpression::SymbolExpression(Symbol symbol) :
		symbol(symbol) {}

	SymbolExpression::~SymbolExpression() { }

	bool SymbolExpression::is_atom() const
	{
		return true;
	}

	bool SymbolExpression::is_number() const
	{
		return false;
	}

	bool SymbolExpression::is_symbol() const
	{
		return true;
	}

	std::vector<Expression*> SymbolExpression::get_exps() const
	{
		throw std::runtime_error("SymbolExpression is not a List.");
	}

	Number SymbolExpression::get_number() const
	{
		throw std::runtime_error("SymbolExpression is not a Number.");
	}

	Symbol SymbolExpression::get_symbol() const
	{
		return this->symbol;
	}
}