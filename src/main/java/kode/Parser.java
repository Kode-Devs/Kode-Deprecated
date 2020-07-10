/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.ArrayList;
import java.util.List;

import static kode.TokenType.*;
import math.KodeNumber;

/**
 *
 * @author dell
 */
class Parser {

    private static class ParseError extends RuntimeException {
    }

    String doc = null;

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        if (tokens.get(0).type == MLSTRING) {
            doc = tokens.get(0).literal.toString();
        }
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    private Expr expression() {
        return assignment();
    }

    private Stmt declaration() {
        try {
            if (match(CLASS)) {
                return classDeclaration();
            }
            if (match(FUN)) {
                return function("function");
            }
            if (match(VAR)) {
                return varDeclaration();
            }
            if (match(TRY)) {
                return tryCatch();
            }
            if (match(CATCH)) {
                throw error(previous(), "'except' without 'try' is not possible");
            }

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt tryCatch() {
        consume(LEFT_BRACE, "Expect '{' after 'try'.");
        List<Stmt> tryStmt = block();
        List<Stmt.Catch> catches = new ArrayList();
        consume(CATCH, "'try' without 'except' is not possible");
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
        } while (match(CATCH));
        return new Stmt.Try(tryStmt, catches);
    }

    private Stmt classDeclaration() {
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
            methods.add(function("method"));
        }

        consume(RIGHT_BRACE, "Expect '}' after class body.");

        return new Stmt.Class(name, superclass, methods, doc_temp);
    }

    private Stmt statement() {
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

    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");

        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        Stmt body = statement();

        if (condition == null) {
            condition = new Expr.Literal(true);
        }
        body = new Stmt.For(initializer, condition, new Stmt.Expression(increment), body);

        return body;
    }

    private Stmt ifStatement() {
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

    private Stmt requireStatement(Token imp) {
        List<Token> value = new ArrayList();
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

    private Stmt requireStatementFrom(Token imp) {
        List<Token> value = new ArrayList();
        do {
            value.add(consume(IDENTIFIER, "Module name Expected."));
        } while (match(DOT));
        consume(IMPORT, "Expect import keyword.");
        List<Token> field = new ArrayList();
        do {
            field.add(consume(IDENTIFIER, "Field name Expected."));
        } while (match(COMMA));
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Require(imp, value, null, field);
    }

    private Stmt raiseStatement() {
        Token keyword = previous();
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after raise value.");
        return new Stmt.Raise(keyword, value);
    }

    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }

        consume(SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    private Stmt breakStatement() {
        Token keyword = previous();
        consume(SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Break(keyword);
    }

    private Stmt continueStatement() {
        Token keyword = previous();
        consume(SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Continue(keyword);
    }

    private Stmt varDeclaration() {
        List<Token> name_list = new ArrayList();
        List<Expr> initializer_list = new ArrayList();
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

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Stmt.Function function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Pair<Token, Expr>> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                TokenType t = null;
                if (match(STAR, POWER)) {
                    t = previous().type;
                }
                Token token = consume(IDENTIFIER, "Expect parameter name.");
                Expr def = null;
                if (t != null) {
                    if (match(EQUAL)) {
                        def = expression();
                    }
                }
                parameters.add(new Pair(token, def).setType(t));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");

        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        String doc_temp = null;
        if (peek().type == MLSTRING) {
            doc_temp = peek().literal.toString();
        }
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body, doc_temp);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr assignment() {
        Expr expr = or();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            } else if (expr instanceof Expr.Get) {
                Expr.Get get = (Expr.Get) expr;
                return new Expr.Set(get.object, get.name, value);
            } else if (expr instanceof Expr.GetAtIndex) {
                Expr.GetAtIndex getAtIndex = (Expr.GetAtIndex) expr;
                return new Expr.SetAtIndex(getAtIndex.array, getAtIndex.index, value, getAtIndex.paren);
            }

            throw error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();

        while (match(SLASH, BACKSLASH, STAR, PERCENT)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS, PLUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return power();
    }

    private Expr power() {
        Expr expr = call();

        while (match(POWER)) {
            Token operator = previous();
            Expr right = call();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Pair<Token, Expr>> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (peek().type == IDENTIFIER && peekNext().type == EQUAL) {
                    Token token = consume(IDENTIFIER, "Identifier Expected");
                    advance();
                    arguments.add(new Pair(token, expression()));
                } else if (match(STAR, POWER)) {
                    arguments.add(new Pair(null, expression()).setType(previous().type));
                } else {
                    arguments.add(new Pair(null, expression()));
                }
            } while (match(COMMA));
        }

        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

        return new Expr.Call(callee, paren, arguments);
    }

    private Expr call() {
        Expr expr = primary();

        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(LEFT_SQUARE)) {
                Expr index = expression();
                Token paren = consume(RIGHT_SQUARE, "Expect ']' after arguments.");
                expr = new Expr.GetAtIndex(expr, index, paren);
            } else if (match(DOT)) {
                Token name = consume(IDENTIFIER,
                        "Expect property name after '.'.");
                expr = new Expr.Get(expr, name);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr primary() {
        if (match(FALSE)) {
            return new Expr.Literal(false);
        }
        if (match(TRUE)) {
            return new Expr.Literal(true);
        }
        if (match(NONE)) {
            return new Expr.Literal(null);
        }
        if (match(INFINITY)) {
            return new Expr.Literal(KodeNumber.valueOf(Double.POSITIVE_INFINITY));
        }
        if (match(NAN)) {
            return new Expr.Literal(KodeNumber.valueOf(Double.NaN));
        }

        if (match(NUMBER, STRING, MLSTRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(SUPER)) {
            Token keyword = previous();
            consume(DOT, "Expect '.' after 'super'.");
            Token method = consume(IDENTIFIER,
                    "Expect superclass method name.");
            return new Expr.Super(keyword, method);
        }

        if (match(THIS)) {
            return new Expr.This(previous());
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(NATIVE)) {
            Token nav = previous();
            List<Token> path = new ArrayList();
            do {
                path.add(consume(IDENTIFIER, "Expect identifier after native."));
            } while (match(DOT));
            String pkg = match(LESS) ? consume(IDENTIFIER, "Expect Package Name.").lexeme : null;
            consume(LEFT_PAREN, "Expected '('.");
            return finishCall(new Expr.Native(nav, path, pkg));
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if (match(LEFT_SQUARE)) {
            List<Expr> array = new ArrayList();

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

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }

        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token peekNext() {
        return tokens.get(current + 1);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        Kode.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) {
                return;
            }

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case RETURN:
                case IMPORT:
                case FROM:
                case BREAK:
                case CONTINUE:
                    return;
            }

            advance();
        }
    }
}
