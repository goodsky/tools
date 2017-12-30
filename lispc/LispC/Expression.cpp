#include "Expression.h"

namespace lispc
{
	Expression::Expression(std::string type) :
		type(type) {}

	Expression::~Expression() { }

	bool Expression::is_atom() const { return false; }

	bool Expression::is_number() const { return false; }

	bool Expression::is_symbol() const { return false; }

	bool Expression::is_lambda() const { return false; }

	std::vector<Expression*> Expression::get_exps() const { throw std::runtime_error(type + " is not a List."); }

	Number Expression::get_number() const { throw std::runtime_error(type + " is not a Number."); }

	Symbol Expression::get_symbol() const { throw std::runtime_error(type + " is not a Symbol."); }

	std::ostream& operator<<(std::ostream& stream, const Expression& expression)
	{
		if (expression.is_number())
		{
			stream << expression.get_number();
		}
		else if (expression.is_symbol())
		{
			stream << "[SYMBOL: " << expression.get_symbol() << "]";
		}
		else if (expression.is_lambda())
		{
			stream << "[LAMBDA]";
		}
		else
		{
			stream << "(";
			for (Expression* nested : expression.get_exps())
			{
				stream << *nested << " ";
			}

			return stream << ")";
		}
	}
}