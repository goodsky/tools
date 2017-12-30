#include "BuiltIns.h"

namespace lispc
{
	std::map<Symbol, Expression*> standard_env()
	{
		std::map<Symbol, Expression*> env;
		env["true"] = new NumberExpression(1);
		env["false"] = new NumberExpression(0);
		env["+"] = new LambdaExpression(&add);
		env["-"] = new LambdaExpression(&sub);
		env["*"] = new LambdaExpression(&mul);
		env["/"] = new LambdaExpression(&div);
		env["%"] = new LambdaExpression(&mod);
		env[">"] = new LambdaExpression(&gt);
		env["<"] = new LambdaExpression(&lt);
		env[">="] = new LambdaExpression(&gte);
		env["<="] = new LambdaExpression(&lte);
		env["="] = new LambdaExpression(&eq);

		return env;
	}

	void verify_args(std::string name, std::vector<Expression*>& args, int count)
	{
		if (args.size() != count)
			throw std::runtime_error("Wrong number of arguments for func " + name + ". Expected " + std::to_string(count) + ". Received " + std::to_string(args.size()) + ".");
	}

	Expression* add(std::vector<Expression*>& args)
	{
		verify_args("add", args, 3);
		return new NumberExpression(args[1]->get_number() + args[2]->get_number());
	}

	Expression* sub(std::vector<Expression*>& args)
	{
		verify_args("sub", args, 3);
		return new NumberExpression(args[1]->get_number() - args[2]->get_number());
	}

	Expression* mul(std::vector<Expression*>& args)
	{
		verify_args("mul", args, 3);
		return new NumberExpression(args[1]->get_number() * args[2]->get_number());
	}

	Expression* div(std::vector<Expression*>& args)
	{
		verify_args("div", args, 3);
		return new NumberExpression(args[1]->get_number() / args[2]->get_number());
	}

	Expression* mod(std::vector<Expression*>& args)
	{
		verify_args("mod", args, 3);
		return new NumberExpression(args[1]->get_number() % args[2]->get_number());
	}

	Expression* gt(std::vector<Expression*>& args)
	{
		verify_args("gt", args, 3);
		return new NumberExpression(args[1]->get_number() > args[2]->get_number());
	}

	Expression* lt(std::vector<Expression*>& args)
	{
		verify_args("lt", args, 3);
		return new NumberExpression(args[1]->get_number() < args[2]->get_number());
	}

	Expression* gte(std::vector<Expression*>& args)
	{
		verify_args("gte", args, 3);
		return new NumberExpression(args[1]->get_number() >= args[2]->get_number());
	}

	Expression* lte(std::vector<Expression*>& args)
	{
		verify_args("lte", args, 3);
		return new NumberExpression(args[1]->get_number() <= args[2]->get_number());
	}

	Expression* eq(std::vector<Expression*>& args)
	{
		verify_args("eq", args, 3);
		return new NumberExpression(args[1]->get_number() == args[2]->get_number());
	}
}