/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 *
 * @author dell
 */
class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;
    private boolean loopon = false;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD
    }

    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS
    }

    private ClassType currentClass = ClassType.NONE;

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;

        declare(stmt.name);
        define(stmt.name);

        if (stmt.superclass != null && stmt.name.lexeme.equals(stmt.superclass.name.lexeme)) {
            error(stmt.superclass.name,
                    "A class cannot inherit from itself.");
        }

        if (stmt.superclass != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(stmt.superclass);
        }

        if (stmt.superclass != null) {
            beginScope();
            scopes.peek().put("super", true);
        }

        beginScope();
        scopes.peek().put("this", true);

        stmt.methods.forEach((method) -> {
            FunctionType declaration = FunctionType.METHOD;
            if (method.name.lexeme.equals(Kode.INIT)) {
                declaration = FunctionType.INITIALIZER;
            }

            resolveFunction(method, declaration);
        });

        endScope();

        if (stmt.superclass != null) {
            endScope();
        }

        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) {
            resolve(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitRequireStmt(Stmt.Require stmt) {
        if (stmt.methods != null) {
            stmt.methods.forEach((name) -> {
                declare(name);
                define(name);
            });
        } else if (stmt.alias != null) {
            declare(stmt.alias);
            define(stmt.alias);
        } else {
            declare(stmt.dir.get(stmt.dir.size() - 1));
            define(stmt.dir.get(stmt.dir.size() - 1));
        }
        return null;
    }

    @Override
    public Void visitRaiseStmt(Stmt.Raise stmt) {
        resolve(stmt.value);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            error(stmt.keyword, "Cannot return from top-level code.");
        }

        if (stmt.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                error(stmt.keyword,
                        "Cannot return a value from an initializer.");
            }

            resolve(stmt.value);
        }

        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        if (!loopon) {
            error(stmt.keyword, "Cannot break from top-level code.");
        }
        return null;
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        if (!loopon) {
            error(stmt.keyword, "Cannot continue from top-level code.");
        }
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        for (int i = 0; i < stmt.name.size(); i++) {
            declare(stmt.name.get(i));
            if (stmt.initial.get(i) != null) {
                resolve(stmt.initial.get(i));
            }
            define(stmt.name.get(i));
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        boolean previous = loopon;
        loopon = true;
        resolve(stmt.body);
        loopon = previous;
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        if (stmt.init != null) {
            resolve(stmt.init);
        }
        resolve(stmt.condition);
        if (stmt.increment != null) {
            resolve(stmt.increment);
        }
        boolean previous = loopon;
        loopon = true;
        resolve(stmt.body);
        loopon = previous;
        return null;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);
        for (Expr argument : expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitGetATIndexExpr(Expr.GetAtIndex expr) {
        resolve(expr.array);
        resolve(expr.index);
        return null;
    }

    @Override
    public Void visitSetATIndexExpr(Expr.SetAtIndex expr) {
        resolve(expr.value);
        resolve(expr.array);
        resolve(expr.index);
        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitSuperExpr(Expr.Super expr) {
        if (currentClass == ClassType.NONE) {
            error(expr.keyword,
                    "Cannot use 'super' outside of a class.");
        } else if (currentClass != ClassType.SUBCLASS) {
            error(expr.keyword,
                    "Cannot use 'super' in a class with no superclass.");
        }
        
        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitArrayExpr(Expr.Array expr) {
        expr.array.forEach((ex) -> {
            resolve(ex);
        });
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() && Objects.equals(scopes.peek().get(expr.name.lexeme), Boolean.FALSE)) {
            error(expr.name,
                    "Cannot read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitNativeExpr(Expr.Native expr) {
        return null;
    }

    @Override
    public Void visitTryStmt(Stmt.Try stmt) {
        beginScope();
        resolve(stmt.tryStmt);
        endScope();
        stmt.catchs.forEach(this::resolve);
        return null;
    }

    @Override
    public Void visitCatchStmt(Stmt.Catch stmt) {
        beginScope();
        if (stmt.alias != null) {
            declare(stmt.alias);
            define(stmt.alias);
        }
        resolve(stmt.catchStmt);
        endScope();
        return null;
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    void resolve(List<Stmt> statements) {
        statements.forEach((statement) -> {
            resolve(statement);
        });
    }

    private void resolveFunction(Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) {
            return;
        }

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            error(name,
                    "Variable with this name already declared in this scope.");
        }

        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) {
            return;
        }
        scopes.peek().put(name.lexeme, true);
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }

        // Not found. Assume it is global.                   
    }

    void error(Token token, String message) {
        throw new RuntimeError(message, token);
    }

}
