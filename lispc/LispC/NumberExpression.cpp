#include "NumberExpression.h"

namespace lispc
{
	NumberExpression::NumberExpression(Number number) :
		number(number) {}

	NumberExpression::~NumberExpression() { }

	bool NumberExpression::is_atom() const { return true; }

	bool NumberExpression::is_number() const { return true; }

	Number NumberExpression::get_number() const { return this->number; }

	std::string NumberExpression::get_type() const { return "NumberExpression"; }
}