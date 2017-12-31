#include "ListExpression.h"

namespace lispc
{
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

	std::vector<Expression*> ListExpression::get_exps() const { return this->exps; }

	std::string ListExpression::get_type() const { return "ListExpression"; }
}