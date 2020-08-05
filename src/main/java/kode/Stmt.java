/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.List;

/**
 *
 * @author dell
 */
abstract class Stmt {

    interface Visitor<R> {

        R visitBlockStmt(Block stmt);

        R visitClassStmt(Class stmt);

        R visitExpressionStmt(Expression stmt);

        R visitFunctionStmt(Function stmt);

        R visitIfStmt(If stmt);

        R visitRequireStmt(Require stmt);

        R visitRaiseStmt(Raise stmt);

        R visitReturnStmt(Return stmt);

        R visitBreakStmt(Break stmt);

        R visitContinueStmt(Continue stmt);

        R visitVarStmt(Var stmt);

        R visitWhileStmt(While stmt);

        R visitForStmt(For stmt);

        R visitTryStmt(Try stmt);

        R visitCatchStmt(Catch stmt);
    }

    // Nested Stmt classes here...
    static class Block extends Stmt {

        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        final List<Stmt> statements;
    }

    static class Class extends Stmt {

        Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods, String doc) {
            this.name = name;
            this.superclass = superclass;
            this.methods = methods;
            this.doc = doc;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStmt(this);
        }

        final Token name;
        final Expr.Variable superclass;
        final List<Stmt.Function> methods;
        final String doc;
    }

    static class Expression extends Stmt {

        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        final Expr expression;
    }

    static class Function extends Stmt {

        Function(Token name, Token[] params, List<Stmt> body, String doc) {
            this.name = name;
            this.params = params;
            this.body = body;
            this.doc = doc;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        final Token name;
        final Token[] params;
        final List<Stmt> body;
        final String doc;
    }

    static class If extends Stmt {

        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;
    }

    static class Require extends Stmt {

        Require(Token imp, List<Token> dir, Token alias, List<Token> methods) {
            this.dir = dir;
            this.imp = imp;
            this.alias = alias;
            this.methods = methods;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRequireStmt(this);
        }

        final Token imp;
        final List<Token> dir;
        final Token alias;
        final List<Token> methods;
    }

    static class Raise extends Stmt {

        Raise(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRaiseStmt(this);
        }

        final Token keyword;
        final Expr value;
    }

    static class Return extends Stmt {

        Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }

        final Token keyword;
        final Expr value;
    }

    static class Break extends Stmt {

        Break(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStmt(this);
        }

        final Token keyword;
    }

    static class Continue extends Stmt {

        Continue(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitContinueStmt(this);
        }

        final Token keyword;
    }

    static class Var extends Stmt {

        Var(List<Token> name, List<Expr> initial) {
            this.name = name;
            this.initial = initial;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        final List<Token> name;
        final List<Expr> initial;
    }

    static class While extends Stmt {

        While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        final Expr condition;
        final Stmt body;
    }

    static class For extends Stmt {

        For(Stmt init, Expr condition, Stmt increment, Stmt body) {
            this.init = init;
            this.condition = condition;
            this.increment = increment;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStmt(this);
        }

        final Stmt init;
        final Expr condition;
        final Stmt increment;
        final Stmt body;
    }

    static class Try extends Stmt {

        Try(List<Stmt> tryStmt, List<Stmt.Catch> catchs) {
            this.tryStmt = tryStmt;
            this.catchs = catchs;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTryStmt(this);
        }

        final List<Stmt> tryStmt;
        final List<Stmt.Catch> catchs;
    }

    static class Catch extends Stmt {

        Catch(Expr.Variable ErrorType, Token alias, List<Stmt> catchStmt) {
            this.ErrorType = ErrorType;
            this.alias = alias;
            this.catchStmt = catchStmt;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCatchStmt(this);
        }

        final Expr.Variable ErrorType;
        final Token alias;
        final List<Stmt> catchStmt;
        KodeInstance instance;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
