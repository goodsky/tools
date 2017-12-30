/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * LispC Expression
 * A Scheme expression is composed of LISTS and ATOMS
 * LIST is vector
 * ATOM is a Number or a Symbol (string)
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
		virtual ~Expression();
		virtual bool is_atom() const = 0;
		virtual bool is_number() const = 0;
		virtual bool is_symbol() const = 0;

		virtual std::vector<Expression*> get_exps() const = 0;
		virtual Number get_number() const = 0;
		virtual Symbol get_symbol() const = 0;
	};

	std::ostream& operator<<(std::ostream& stream, const Expression& expression);

	class ListExpression : public Expression
	{
	public:
		ListExpression(std::vector<Expression*>& exps);
		virtual ~ListExpression() override;
		virtual bool is_atom() const override;
		virtual bool is_number() const override;
		virtual bool is_symbol() const override;

		virtual std::vector<Expression*> get_exps() const override;
		virtual Number get_number() const override;
		virtual Symbol get_symbol() const override;

	private:
		std::vector<Expression*> exps;
	};

	class NumberExpression : public Expression
	{
	public:
		NumberExpression(Number number);
		virtual ~NumberExpression() override;
		virtual bool is_atom() const override;
		virtual bool is_number() const override;
		virtual bool is_symbol() const override;

		virtual std::vector<Expression*> get_exps() const override;
		virtual Number get_number() const override;
		virtual Symbol get_symbol() const override;

	private:
		Number number;
	};

	class SymbolExpression : public Expression
	{
	public:
		SymbolExpression(Symbol symbol);
		virtual ~SymbolExpression() override;
		virtual bool is_atom() const override;
		virtual bool is_number() const override;
		virtual bool is_symbol() const override;

		virtual std::vector<Expression*> get_exps() const override;
		virtual Number get_number() const override;
		virtual Symbol get_symbol() const override;

	private:
		Symbol symbol;
	};
}