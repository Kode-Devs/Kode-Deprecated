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

        R visitThisExpr(This expr);

        R visitUnaryExpr(Unary expr);

        R visitVariableExpr(Variable expr);
        
        R visitArrayExpr(Array expr);
        
        R visitNativeExpr(Native expr);
    }

    // Nested Expr clases here...
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
    
    static class GetAtIndex extends Expr {

        GetAtIndex(Expr array,Expr index, Token paren) {
            this.array= array;
            this.index = index;
            this.paren = paren;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetATIndexExpr(this);
        }

        final Expr array;
        final Expr index;
        final Token paren;;
    }
    
    static class SetAtIndex extends Expr {

        SetAtIndex(Expr array,Expr index,Expr value, Token paren) {
            this.array= array;
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
        final Token paren;;
    }

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

    static class This extends Expr {

        This(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitThisExpr(this);
        }

        final Token keyword;
    }

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
    
    static class Array extends Expr {

        Array(Token paren,List<Expr> array) {
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
    
    static class Native extends Expr {

        Native(Token nav,List<Token> path, String pkg) {
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
