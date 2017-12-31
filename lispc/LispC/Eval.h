/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* LispC Evaluation
* 	 Where a late-binding language would be swell.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
#pragma once

#include "Environment.h"
#include "Expression.h"
#include "LambdaExpression.h"

namespace lispc
{
	Expression* eval(Expression* expression, Environment* env);
}