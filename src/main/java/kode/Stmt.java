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

import java.util.List;

/**
 * Abstract class representing all statement nodes.
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
abstract class Stmt {

    @Override
    public int hashCode() {
        return super.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

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

    /**
     * Statement node representing a block of statements surrounded by curly
     * braces.
     */
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

    /**
     * Statement node for declaring a new class.
     */
    static class Class extends Stmt {

        Class(Token name, Expr.Variable superclass, List<Function> methods, String doc) {
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
        final List<Function> methods;
        final String doc;
    }

    /**
     * Statement node representing a statement containing only one expression.
     */
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

    /**
     * Statement node for declaring a new function.
     */
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

    /**
     * Statement node representing if-else block.
     */
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

    /**
     * Statement node representing try block.
     */
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

    /**
     * Statement node to raise an error instance.
     */
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

    /**
     * Statement node representing a single-valued return statement, inside a function.
     */
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

    /**
     * Statement node representing a break statement, inside a loop.
     */
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

    /**
     * Statement node representing a continue statement, inside a loop.
     */
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

    /**
     * Statement node to declare a new variable with/without an initial value.
     */
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

    /**
     * Statement node representing while loop.
     */
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

    /**
     * Statement node representing for loop.
     */
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

    /**
     * Statement node representing try block.
     */
    static class Try extends Stmt {

        Try(List<Stmt> tryStmt, List<Catch> catchs) {
            this.tryStmt = tryStmt;
            this.catchs = catchs;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTryStmt(this);
        }

        final List<Stmt> tryStmt;
        final List<Catch> catchs;
    }

    /**
     * Statement node representing except block.
     */
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
