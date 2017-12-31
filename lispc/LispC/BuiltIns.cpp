#include "BuiltIns.h"

namespace lispc
{
	std::map<Symbol, Expression*> standard_env()
	{
		std::map<Symbol, Expression*> env;
		env["true"] = new NumberExpression(1);
		env["false"] = new NumberExpression(0);
		env["pi"] = new NumberExpression(3.141592653589793238462643383279502884);
		env["+"] = new FuncExpression(&add);
		env["-"] = new FuncExpression(&sub);
		env["*"] = new FuncExpression(&mul);
		env["/"] = new FuncExpression(&div);
		env["%"] = new FuncExpression(&mod);
		env[">"] = new FuncExpression(&gt);
		env["<"] = new FuncExpression(&lt);
		env[">="] = new FuncExpression(&gte);
		env["<="] = new FuncExpression(&lte);
		env["="] = new FuncExpression(&eq);

		return env;
	}

	void verify_args(std::string name, std::vector<Expression*>& args, int count)
	{
		if (args.size() != count)
			throw std::runtime_error("Wrong number of arguments for func " + name + ". Expected " + std::to_string(count) + ". Received " + std::to_string(args.size()) + ".");
	}

	Expression* add(std::vector<Expression*>& args)
	{
		verify_args("add", args, 2);
		return new NumberExpression(args[0]->get_number() + args[1]->get_number());
	}

	Expression* sub(std::vector<Expression*>& args)
	{
		verify_args("sub", args, 2);
		return new NumberExpression(args[0]->get_number() - args[1]->get_number());
	}

	Expression* mul(std::vector<Expression*>& args)
	{
		verify_args("mul", args, 2);
		return new NumberExpression(args[0]->get_number() * args[1]->get_number());
	}

	Expression* div(std::vector<Expression*>& args)
	{
		verify_args("div", args, 2);
		return new NumberExpression(args[0]->get_number() / args[1]->get_number());
	}

	Expression* mod(std::vector<Expression*>& args)
	{
		verify_args("mod", args, 2);
		return new NumberExpression(args[0]->get_number() % args[1]->get_number());
	}

	Expression* gt(std::vector<Expression*>& args)
	{
		verify_args("gt", args, 2);
		return new NumberExpression(args[0]->get_number() > args[1]->get_number());
	}

	Expression* lt(std::vector<Expression*>& args)
	{
		verify_args("lt", args, 2);
		return new NumberExpression(args[0]->get_number() < args[1]->get_number());
	}

	Expression* gte(std::vector<Expression*>& args)
	{
		verify_args("gte", args, 2);
		return new NumberExpression(args[0]->get_number() >= args[1]->get_number());
	}

	Expression* lte(std::vector<Expression*>& args)
	{
		verify_args("lte", args, 2);
		return new NumberExpression(args[0]->get_number() <= args[1]->get_number());
	}

	Expression* eq(std::vector<Expression*>& args)
	{
		verify_args("eq", args, 2);
		return new NumberExpression(args[0]->get_number() == args[1]->get_number());
	}
}