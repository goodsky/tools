# LispC

## How to write a (Lisp) interpreter using an inappropriate language (C++)

Thanks to Peter Norvig. http://www.norvig.com/lispy.html

## Capabiilties
* Parses Lisp (Scheme) code
* Creates simple AST of Expressions
* Evaluates AST using binding to built-in functions

## Areas of Improvement
* The Expression type is basically a union of all Expression Types - this doesn't feel right.
* Memory leaks are sure to exists. Memory management needs a once (or twice) over.
* LambdaExpression is not fully implemented.