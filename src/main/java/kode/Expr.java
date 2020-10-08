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
 * Abstract class representing all expression nodes.
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
abstract class Expr {

    @Override
    public int hashCode() {
        return super.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

    interface Visitor<R> {

        R visitAssignExpr(Assign expr);

        R visitBinaryExpr(Binary expr);

        R visitCallExpr(Call expr);

        R visitGetATIndexExpr(GetAtIndex expr);

        R visitSetATIndexExpr(SetAtIndex expr);

        R visitGetExpr(Get expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitLogicalExpr(Logical expr);

        R visitSetExpr(Set expr);

        R visitSuperExpr(Super expr);

        R visitUnaryExpr(Unary expr);

        R visitVariableExpr(Variable expr);

        R visitArrayExpr(Array expr);

        R visitNativeExpr(Native expr);
    }

    // Nested Expr clases here...
    /**
     * Expression node for assigning a new value to a predefined variable.
     */
    static class Assign extends Expr {

        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        final Token name;
        final Expr value;
    }

    /**
     * Expression node for performing binary operations.
     */
    static class Binary extends Expr {

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    /**
     * Expression node for performing call operation.
     */
    static class Call extends Expr {

        Call(Expr callee, Token paren, Expr[] arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

        final Expr callee;
        final Token paren;
        final Expr[] arguments;
    }

    /**
     * Expression node for retrieving an element present at a specific index in
     * any data-structure.
     */
    static class GetAtIndex extends Expr {

        GetAtIndex(Expr array, Expr index, Token paren) {
            this.array = array;
            this.index = index;
            this.paren = paren;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetATIndexExpr(this);
        }

        final Expr array;
        final Expr index;
        final Token paren;
    ;

    }
    
    /**
     * Expression node for assigning a new element to a specific index in any data-structure.
     */
    static class SetAtIndex extends Expr {

        SetAtIndex(Expr array, Expr index, Expr value, Token paren) {
            this.array = array;
            this.index = index;
            this.value = value;
            this.paren = paren;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetATIndexExpr(this);
        }

        final Expr array;
        final Expr index;
        final Expr value;
        final Token paren;
    ;

    }

    /**
     * Expression node for retrieving a field from any element.
     */
    static class Get extends Expr {

        Get(Expr object, Token name) {
            this.object = object;
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }

        final Expr object;
        final Token name;
    }

    /**
     * Expression node representing braces.
     */
    static class Grouping extends Expr {

        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final Expr expression;
    }

    /**
     * Expression node for generating a new literal.
     */
    static class Literal extends Expr {

        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }

    /**
     * Expression node for performing logical operations.
     */
    static class Logical extends Expr {

        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    /**
     * Expression node for assigning a value to a field in any element.
     */
    static class Set extends Expr {

        Set(Expr object, Token name, Expr value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }

        final Expr object;
        final Token name;
        final Expr value;
    }

    /**
     * Expression node for retrieving a specific field from the super class of
     * an class.
     */
    static class Super extends Expr {

        Super(Token keyword, Token method) {
            this.keyword = keyword;
            this.method = method;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSuperExpr(this);
        }

        final Token keyword;
        final Token method;
    }

    /**
     * Expression node for performing unary operations.
     */
    static class Unary extends Expr {

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expr right;
    }

    /**
     * Expression node for retrieving the value stored in a variable.
     */
    static class Variable extends Expr {

        Variable(Token name) {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        final Token name;
    }

    /**
     * Expression node for generating a new array.
     */
    static class Array extends Expr {

        Array(Token paren, List<Expr> array) {
            this.paren = paren;
            this.array = array;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitArrayExpr(this);
        }

        final Token paren;
        final List<Expr> array;
    }

    /**
     * Expression node for performing native call to Java.
     */
    static class Native extends Expr {

        Native(Token nav, List<Token> path, String pkg) {
            this.nav = nav;
            this.path = path;
            this.pkg = pkg;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitNativeExpr(this);
        }

        final Token nav;
        final List<Token> path;
        final String pkg;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
