/*
 * Copyright (C) 2020 Kode Devs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kode;

import java.util.ArrayList;
import java.util.List;

import static kode.TokenType.*;

import math.KodeNumber;

/**
 * <B><center>--- Syntax Analyzer for KODE interpreter ---</center></B>
 * <p>
 * Syntax Analyzer or in-short Parser is an algorithm/process which generates an
 * Abstract Syntax Tree or AST from the List of Tokens as input according to the
 * syntax rules defined for KODE.</p>
 *
 * <p>
 * The default syntax to perform Syntax Analysis is<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;<code>new Parser(&lt;tokens&gt;).parse();
 * </code><br>where, {@code tokens} is the list of Tokens generated by the
 * Lexical Analyzer.</p>
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 * @see Parser#parse()
 * @see Parser( List)
 */
class Parser {

    /*
     *                      --- PROJECT NOTE ---
     *
     * AIM -> To build a Syntax Analyzer for the interpreter, which generates an AST
     * (i.e, Abstract Syntax Tree) from the list of Tokens, with respect to the
     * syntax definitions of the interpreter.
     *
     * Note - The structure of this parser has been derived from jLox
     * interpreter.
     */
    String doc = null;
    private final List<Token> tokens;

    /**
     * Pointer to the index of current
     */
    private int current = 0;

    /**
     * Creates an instance of the Syntax Analyzer for a specific list of Tokens
     * generated by the Lexical Analyzer.
     *
     * @param tokens List of Tokens.
     */
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Generates the AST from the associated list of Tokens following the syntax
     * rules defined. It also checks for the multi-lined string attached at the
     * beginning of the source file as documentation/help string.
     *
     * @return Returns the AST i.e., the root element/node of the AST. Note that
     * each element in the returned list is the root for each statement defined
     * at the global scope.
     * @see Parser
     * @see Parser#declaration
     */
    List<Stmt> parse() {
        // Check for help/doc text.
        if (tokens.get(0).type == MLSTRING) {
            doc = tokens.get(0).literal.toString();
        }

        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    /**
     * Scans for Expressions.
     *
     * @see Expr
     * @see Parser#assignment
     */
    private Expr expression() {
        return assignment();
    }

    /**
     * Scans for Declaration statements for Classes, Functions and Variables.
     *
     * @see Parser#classDeclaration
     * @see Parser#function
     * @see Parser#varDeclaration
     * @see Parser#statement
     * @see Stmt
     */
    private Stmt declaration() {
        if (match(CLASS)) {
            return classDeclaration();
        }
        if (match(FUN)) {
            return function("function"); // Function Declaration
        }
        if (match(VAR)) {
            return varDeclaration();
        }
        return statement();
    }

    /**
     * Scans for declaration of individual statements.
     */
    private Stmt statement() {
        if (match(TRY)) {
            return tryExceptStatement();
        }
        if (match(EXCEPT)) {
            // EXCEPT statement must be consumed during consuming try statement.
            // And can not be consumed independently.
            throw error(previous(), "'except' without 'try' is not possible");
        }
        if (match(FOR)) {
            return forStatement();
        }
        if (match(IF)) {
            return ifStatement();
        }
        if (match(WHILE)) {
            return whileStatement();
        }
        if (match(RETURN)) {
            return returnStatement();
        }
        if (match(BREAK)) {
            return breakStatement();
        }
        if (match(CONTINUE)) {
            return continueStatement();
        }
        if (match(IMPORT)) {
            return requireStatement(previous());
        }
        if (match(FROM)) {
            return requireStatementFrom(previous());
        }
        if (match(RAISE)) {
            return raiseStatement();
        }
        if (match(LEFT_BRACE)) {
            return new Stmt.Block(block());
        }

        return expressionStatement();
    }

    /**
     * Scans for {@code for} loops.
     */
    private Stmt forStatement() {
        /*
            Syntax
            ----------------------------------------------
            for(init; condition; incr/decr){
                // code to be executed
            }
         */
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
        Stmt initializer = match(SEMICOLON) ? null : (match(VAR) ? varDeclaration() : expressionStatement());

        Expr condition = check(SEMICOLON) ? new Expr.Literal(true) : expression();
        consume(SEMICOLON, "Expect ';' after loop condition.");

        Stmt increment = check(RIGHT_PAREN) ? null : new Stmt.Expression(expression());
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        return new Stmt.For(initializer, condition, increment, statement());
    }

    /**
     * Scans for {@code while} loops.
     */
    private Stmt whileStatement() {
        /*
            Syntax
            ----------------------------------------------
            while(condition){
                // code to be executed
            }
         */
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    /**
     * Scans for {@code if-else} statements/blocks.
     */
    private Stmt ifStatement() {
        /*
            Syntax
            ----------------------------------------------
            if(condition){
                // code to be executed if condition is true
            } else {
                // code to be executed if condition is false
            }
        
            You can also use it as if-else-if block.
         */
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    /**
     * Scans for {@code import} statements.
     */
    private Stmt requireStatement(Token imp) {
        /*
            Syntax
            ----------------------------------------------
            import pkg.name as alias;
         */
        List<Token> value = new ArrayList<>();
        do {
            value.add(consume(IDENTIFIER, "Module name Expected."));
        } while (match(DOT));
        Token alias = null;
        if (match(AS)) {
            alias = consume(IDENTIFIER, "Expect alias name.");
        }
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Require(imp, value, alias, null);
    }

    /**
     * Scans for {@code from-import} statements.
     */
    private Stmt requireStatementFrom(Token imp) {
        /*
            Syntax
            ----------------------------------------------
            from pkg.name import func1 as alias1, func2 as alias2, ...;
         */
        List<Token> value = new ArrayList<>();
        do {
            value.add(consume(IDENTIFIER, "Module name Expected."));
        } while (match(DOT));
        consume(IMPORT, "Expect import keyword.");
        List<Token> field = new ArrayList<>();
        do {
            field.add(consume(IDENTIFIER, "Field name Expected."));
        } while (match(COMMA));
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Require(imp, value, null, field);
    }

    /**
     * Scans for {@code raise} statements.
     */
    private Stmt raiseStatement() {
        /*
            Syntax
            ----------------------------------------------
            raise error_instance;
         */
        Token keyword = previous();
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after raise value.");
        return new Stmt.Raise(keyword, value);
    }

    /**
     * Scans for {@code return} statements.
     */
    private Stmt returnStatement() {
        /*
            Syntax
            ----------------------------------------------
            return value;
         */
        Token keyword = previous();
        Expr value = check(SEMICOLON) ? null : expression();
        consume(SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    /**
     * Scans for {@code break} statements.
     */
    private Stmt breakStatement() {
        /*
            Syntax
            ----------------------------------------------
            break;
         */
        Token keyword = previous();
        consume(SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Break(keyword);
    }

    /**
     * Scans for {@code continue} statements.
     */
    private Stmt continueStatement() {
        /*
            Syntax
            ----------------------------------------------
            continue;
         */
        Token keyword = previous();
        consume(SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Continue(keyword);
    }

    /**
     * Scans for declarations of variables.
     */
    private Stmt varDeclaration() {
        /*
            Syntax
            ----------------------------------------------
            var var_name1 = init_value1, var_name2 = init_value2, ...;
         */
        List<Token> name_list = new ArrayList<>();
        List<Expr> initializer_list = new ArrayList<>();
        do {
            Token name = consume(IDENTIFIER, "Expect variable name.");

            Expr initializer = null;
            if (match(EQUAL)) {
                initializer = expression();
            }
            name_list.add(name);
            initializer_list.add(initializer);
        } while (match(COMMA));

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name_list, initializer_list);
    }

    /**
     * Scans for try-expect statements.
     *
     * @see Stmt
     */
    private Stmt tryExceptStatement() {
        /*
            Syntax
            ----------------------------------------------
            try {
                // code to be executed
            } except(error_type as inst_alias) {
                // code to be executed in case of failure
            }
         */
        consume(LEFT_BRACE, "Expect '{' after 'try'.");
        List<Stmt> tryStmt = block();
        List<Stmt.Catch> catches = new ArrayList<>();
        consume(EXCEPT, "'try' without 'except' is not possible");
        do {
            Expr.Variable ErrorType = null;
            Token alias = null;

            if (match(LEFT_PAREN)) {
                consume(IDENTIFIER, "Expect superclass name.");
                ErrorType = new Expr.Variable(previous());

                if (match(AS)) {
                    alias = consume(IDENTIFIER, "Expect alias name.");
                }
                consume(RIGHT_PAREN, "Expect ')' after for clauses.");
            }
            consume(LEFT_BRACE, "Expect '{' after 'expect'.");
            List<Stmt> catchStmt = block();
            catches.add(new Stmt.Catch(ErrorType, alias, catchStmt));
        } while (match(EXCEPT));
        return new Stmt.Try(tryStmt, catches);
    }

    /**
     * Scans for declaration of Classes.
     */
    private Stmt classDeclaration() {
        /*
            Syntax
            ----------------------------------------------
            class name < super_class {
                `doc-string goes here`
                
                method1 (params) {
                    // code to be executed
                }
                
                method2 (params) {
                    // code to be executed
                }
            }
         */
        Token name = consume(IDENTIFIER, "Expect class name.");

        Expr.Variable superclass = null;
        if (match(LESS)) {
            consume(IDENTIFIER, "Expect superclass name.");
            superclass = new Expr.Variable(previous());
        }

        consume(LEFT_BRACE, "Expect '{' before class body.");
        String doc_temp = null;
        if (peek().type == MLSTRING) {
            doc_temp = advance().literal.toString();
        }

        List<Stmt.Function> methods = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method")); // Method Declaration
        }

        consume(RIGHT_BRACE, "Expect '}' after class body.");

        return new Stmt.Class(name, superclass, methods, doc_temp);
    }

    /**
     * Scans for statement only containing a single expression.
     */
    private Stmt expressionStatement() {
        /*
            Syntax
            ----------------------------------------------
            expr;
         */
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    /**
     * Scans for function/method declaration.
     *
     * @param kind Type of function.
     */
    private Stmt.Function function(String kind) {
        /*
            Syntax
            ----------------------------------------------
            fun func_name (params) {
                `doc-string goes here`;
                // code to be executed
            }
         */
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= Integer.MAX_VALUE) {
                    throw error(peek(), "Cannot have more than " + Integer.MAX_VALUE + " parameters.");
                }
                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
                if (parameters.get(parameters.size() - 1).lexeme.equals(Kode.VARARGIN) && match(COMMA)) {
                    throw error(peek(), "'" + Kode.VARARGIN + "' is a special input parameter and must only be included as the last input parameter.");
                }
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");

        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        String doc_temp = null;
        if (peek().type == MLSTRING) {
            doc_temp = peek().literal.toString();
        }
        return new Stmt.Function(name, parameters.toArray(new Token[]{}), block(), doc_temp);
    }

    /**
     * Scans for independent blocks declaration.
     */
    private List<Stmt> block() {
        /*
            Syntax
            ----------------------------------------------
            {
                // code to be executed
            }
         */
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    /**
     * Scans for assignment expressions.
     */
    private Expr assignment() {
        /*
            Syntax
            ----------------------------------------------
            identifier = new_value
            list_name [idx_val] = new_value
            obj.field = new_value
         */
        Expr expr = or();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                /*
                    Syntax
                    ----------------------------------------------
                    identifier = new_value
                 */
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            } else if (expr instanceof Expr.Get) {
                /*
                    Syntax
                    ----------------------------------------------
                    obj.field = new_value
                 */
                Expr.Get get = (Expr.Get) expr;
                return new Expr.Set(get.object, get.name, value);
            } else if (expr instanceof Expr.GetAtIndex) {
                /*
                    Syntax
                    ----------------------------------------------
                    list_name [idx_val] = new_value
                 */
                Expr.GetAtIndex getAtIndex = (Expr.GetAtIndex) expr;
                return new Expr.SetAtIndex(getAtIndex.array, getAtIndex.index, value, getAtIndex.paren);
            }

            throw error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    /**
     * Scans for binary logical 'OR' operation expression.
     */
    private Expr or() {
        /*
            Syntax
            ----------------------------------------------
            a or b
         */
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    /**
     * Scans for binary logical 'AND' operation expression.
     */
    private Expr and() {
        /*
            Syntax
            ----------------------------------------------
            a and b
         */
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    /**
     * Scans for binary equality or binary not-equality operation expression.
     */
    private Expr equality() {
        /*
            Syntax
            ----------------------------------------------
            a == b      a != b
         */
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Scans for other binary comparison operation expression apart from
     * equality and not-equality.
     */
    private Expr comparison() {
        /*
            Syntax
            ----------------------------------------------
            a < b       a <= b
            a > b       a >= b
         */
        Expr expr = shift();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = shift();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Scans for binary shift operation expression.
     */
    private Expr shift() {
        /*
            Syntax
            ----------------------------------------------
            a << b       a >> b
         */
        Expr expr = addition();

        while (match(LSHIFT, RSHIFT)) {
            Token operator = previous();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Scans for binary addition operation expression.
     */
    private Expr addition() {
        /*
            Syntax
            ----------------------------------------------
            a + b       a - b
         */
        Expr expr = multiplication();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Scans for binary multiplication operation expression.
     */
    private Expr multiplication() {
        /*
            Syntax
            ----------------------------------------------
            a * b       a / b       a \ b       a % b
         */
        Expr expr = unary();

        while (match(SLASH, BACKSLASH, STAR, PERCENT)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Scans for unary operation expression.
     */
    private Expr unary() {
        /*
            Syntax
            ----------------------------------------------
            !a       +a       -a
         */
        if (match(BANG, MINUS, PLUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return power();
    }

    /**
     * Scans for exponential/power operation expression.
     */
    private Expr power() {
        /*
            Syntax
            ----------------------------------------------
            a ** b
         */
        Expr expr = call();

        while (match(POWER)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    /**
     * Scans for call operation expression which includes calls like function
     * call, object creation call, etc.
     */
    private Expr finishCall(Expr callee) {
        /*
            Syntax
            ----------------------------------------------
            func_name (args)
         */
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= Integer.MAX_VALUE) {
                    throw error(peek(), "Cannot have more than " + Integer.MAX_VALUE + " arguments.");
                }
                arguments.add(expression());
            } while (match(COMMA));
        }
        return new Expr.Call(callee, consume(RIGHT_PAREN, "Expect ')' after arguments."), arguments.toArray(new Expr[]{}));
    }

    /**
     * Scans for certain call like operation expression which includes call
     * operation, indexing operation, attribute/field access operation, etc.
     */
    private Expr call() {
        Expr expr = primary();

        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(LEFT_SQUARE)) {
                /*
                    Syntax
                    ----------------------------------------------
                    list_name [idx_val]
                 */
                Expr index = expression();
                Token paren = consume(RIGHT_SQUARE, "Expect ']' after arguments.");
                expr = new Expr.GetAtIndex(expr, index, paren);
            } else if (match(DOT)) {
                /*
                    Syntax
                    ----------------------------------------------
                    obj.field
                 */
                Token name = consume(IDENTIFIER,
                        "Expect property name after '.'.");
                expr = new Expr.Get(expr, name);
            } else {
                break;
            }
        }

        return expr;
    }

    /**
     * Scans for expression which includes literals expressions, super
     * expression, identifiers/value-access operation, native-call operation,
     * grouping operations, array/list declaration operations, etc.
     */
    private Expr primary() {
        if (match(FALSE)) { // Binary False literal
            return new Expr.Literal(false);
        }
        if (match(TRUE)) { // Binary True literal
            return new Expr.Literal(true);
        }
        if (match(NONE)) { // Binary None literal
            return new Expr.Literal(null);
        }
        if (match(INFINITY)) { // Numeric Infinity literal
            return new Expr.Literal(KodeNumber.valueOf(Double.POSITIVE_INFINITY));
        }
        if (match(NAN)) { // Numeric NaN literal
            return new Expr.Literal(KodeNumber.valueOf(Double.NaN));
        }

        if (match(NUMBER, STRING, MLSTRING)) { // Numeric/String literals
            return new Expr.Literal(previous().literal);
        }

        if (match(SUPER)) {
            /*
                Syntax
                ----------------------------------------------
                super.field
             */
            Token keyword = previous();
            consume(DOT, "Expect '.' after 'super'.");
            Token method = consume(IDENTIFIER,
                    "Expect superclass method name.");
            return new Expr.Super(keyword, method);
        }

        if (match(IDENTIFIER)) { // Variable Accessing using name
            return new Expr.Variable(previous());
        }

        if (match(NATIVE)) {
            /*
                Syntax
                ----------------------------------------------
                native path.to.java.class < pkg_name (params)
             */
            Token nav = previous();
            List<Token> path = new ArrayList<>();
            do {
                path.add(consume(IDENTIFIER, "Expect identifier after native."));
            } while (match(DOT));
            String pkg = match(LESS) ? consume(IDENTIFIER, "Expect Package Name.").lexeme : null;
            consume(LEFT_PAREN, "Expected '('.");
            return finishCall(new Expr.Native(nav, path, pkg));
        }

        if (match(LEFT_PAREN)) {
            /*
                Syntax
                ----------------------------------------------
                (expr)
             */
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if (match(LEFT_SQUARE)) {
            /*
                Syntax
                ----------------------------------------------
                [value1, value2, value3, ...]
             */
            List<Expr> array = new ArrayList<>();

            if (!check(RIGHT_SQUARE)) {
                do {
                    array.add(expression());
                } while (match(COMMA));
            }

            Token paren = consume(RIGHT_SQUARE, "Expect ']' after expression.");

            return new Expr.Array(paren, array);
        }

        throw error(peek(), "Expect expression.");
    }

    /**
     * Checks weather the current token belongs to one of the enlisted
     * token-types or not.
     *
     * @param types List of Token types.
     * @return Returns {@code true} if the current token belongs to one of the
     * following enlisted token types, else {@code false}.
     * @see TokenType
     * @see Parser#advance()
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    /**
     * Consumes the current token if it matches the specified token type, else
     * generate an error with the error message provided.
     *
     * @param type    The specified token type.
     * @param message Error message if it fails to consume the current token.
     * @return Returns the consumed token.
     * @see Token
     * @see TokenType
     * @see Parser#advance
     * @see Parser#error
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }

        throw error(peek(), message);
    }

    /**
     * Checks whether the current token belongs to the specified token type, or
     * not, without consuming it. Returns {@code false} if at end.
     *
     * @see Parser#peek
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    /**
     * Increments the current token pointer by 1, thus consuming the current
     * token.
     *
     * @return Returns the Token addressed by the current token pointer before
     * update.
     * @see Token
     * @see Parser#current
     * @see Parser#previous
     */
    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    /**
     * Checks weather the list of tokens has come to an end, or not.
     */
    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    /**
     * Peeks the current token without consuming it.
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Peeks the next token without consuming it.
     */
    @SuppressWarnings("unused")
    private Token peekNext() {
        return tokens.get(current + 1);
    }

    /**
     * Peeks the previous token which has been already consumed. In other words,
     * it gives the last token which was consumed.
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * Generates an instance of error having a specific message, including a
     * token as reference.
     *
     * @param token   A token instance as reference.
     * @param message Error message describing the error instance.
     * @return The error instance generated.
     * @see RuntimeError
     */
    private Error error(Token token, String message) {
        return new RuntimeError(message, token);
    }
}
