/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC Number
* Represents an integer or a floating point number
*
* I also potentially deviate from the Scheme standard by treating integers 0 & 1 as
* boolean data types for logic.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include <iostream>

namespace lispc
{
	class Number 
	{
	public:
		Number(int val);
		Number(double val);
		Number(const Number& other);
		Number& operator=(const Number& other);

		bool is_int() const;
		int get_int() const;
		double get_double() const;

		friend Number operator+(const Number& n1, const Number& n2);
		friend Number operator-(const Number& n1, const Number& n2);
		friend Number operator*(const Number& n1, const Number& n2);
		friend Number operator/(const Number& n1, const Number& n2);
		friend Number operator%(const Number& n1, const Number& n2);
		friend Number operator>(const Number& n1, const Number& n2);
		friend Number operator<(const Number& n1, const Number& n2);
		friend Number operator>=(const Number& n1, const Number& n2);
		friend Number operator<=(const Number& n1, const Number& n2);
		friend Number operator==(const Number& n1, const Number& n2);
		friend std::ostream& operator<<(std::ostream& stream, const Number& number);

	private:
		bool isInteger;
		int intValue;
		double doubleValue;
	};
}