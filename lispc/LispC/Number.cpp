#include "Number.h"

namespace lispc
{
	Number::Number(int val) :
		isInteger(true),
		intValue(val),
		doubleValue((double)val) {}

	Number::Number(double val) :
		isInteger(false),
		intValue((int)val),
		doubleValue(val) {}

	Number::Number(const Number& other) :
		isInteger(other.isInteger),
		intValue(other.intValue),
		doubleValue(other.doubleValue) {}

	Number& Number::operator=(const Number& other)
	{
		isInteger = other.isInteger;
		intValue = other.intValue;
		doubleValue = other.doubleValue;
		return *this;
	}

	bool Number::is_int() const
	{
		return isInteger;
	}

	int Number::get_int() const
	{
		return intValue;
	}

	double Number::get_double() const
	{
		return doubleValue;
	}

	Number operator+(const Number& n1, const Number& n2)
	{
		if (n1.isInteger && n2.isInteger)
		{
			return Number(n1.intValue + n2.intValue);
		}
		else
		{
			return Number(n1.doubleValue + n2.doubleValue);
		}
	}

	Number operator-(const Number& n1, const Number& n2)
	{
		if (n1.isInteger && n2.isInteger)
		{
			return Number(n1.intValue - n2.intValue);
		}
		else
		{
			return Number(n1.doubleValue - n2.doubleValue);
		}
	}

	Number operator*(const Number& n1, const Number& n2)
	{
		if (n1.isInteger && n2.isInteger)
		{
			return Number(n1.intValue * n2.intValue);
		}
		else
		{
			return Number(n1.doubleValue * n2.doubleValue);
		}
	}

	Number operator/(const Number& n1, const Number& n2)
	{
		if (n1.isInteger && n2.isInteger)
		{
			return Number(n1.intValue / n2.intValue);
		}
		else
		{
			return Number(n1.doubleValue / n2.doubleValue);
		}
	}

	Number operator%(const Number& n1, const Number& n2)
	{
		if (n1.isInteger && n2.isInteger)
		{
			return Number(n1.intValue % n2.intValue);
		}
		else
		{
			return Number(fmod(n1.doubleValue, n2.doubleValue));
		}
	}

	std::ostream& operator<<(std::ostream& stream, const Number& number)
	{
		if (number.isInteger)
		{
			return stream << number.intValue;
		}
		else
		{
			return stream << number.doubleValue;
		}
	}
}