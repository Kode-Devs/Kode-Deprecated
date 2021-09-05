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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 * <B><center>--- Semantic Analyzer for KODE interpreter ---</center></B>
 * <p>
 * Semantic Analyzer or in-short Resolver traverses through the AST and checks
 * for errors based on semantic information gathered from the source code. It
 * includes scoping rules, variable access before declaration errors, etc. which
 * isn't possible to be discovered before or during parsing of the source
 * file.</p>
 *
 * <p>
 * The default syntax to perform Semantic Analysis is
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;  <code>
 * new Resolver(&lt;interpreter&gt;).resolve(&lt;list of statements&gt;);
 * </code><br>where, {@code list of statements} is the root of AST generated by
 * the Syntax Analyzer.</p>
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 * @see Resolver#resolve(List)
 * @see Resolver(Interpreter)
 */
class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

    private final Interpreter interpreter;

    /**
     * Stack representing the scope stack, where each item in the stack
     * represents a single scope, and is in the format of a map between variable
     * name and a Boolean flag. Here, {@code true} value of the flag represents
     * variable declared and initialized, and {@code false} value of the flag
     * represents variable declared but not initialized.
     */
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private final Map<String, Boolean> global_scope = new HashMap<>();

    // Tracks the position of node, whether it is inside a function, constructor, method, class, subclass, or None.
    private FunctionType currentFunction = FunctionType.NONE;
    private ClassType currentClass = ClassType.NONE;
    private boolean loop_on = false;

    /**
     * Creates an instance of the Semantic Analyzer to scan the AST generated by
     * the Syntax Analyzer.
     *
     * @param interpreter Associated interpreter instance which will be used for
     *                    executing the source code.
     */
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

        // Sub-class and super-class can not have same names.
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
            scopes.peek().put("super", true); // super needs to be pre-declared inside the scope of the class.
        }

        beginScope();

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
        if (!loop_on) {
            error(stmt.keyword, "Cannot break from top-level code.");
        }
        return null;
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        if (!loop_on) {
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
        boolean previous = loop_on;
        loop_on = true;
        resolve(stmt.body);
        loop_on = previous;
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
        boolean previous = loop_on;
        loop_on = true;
        resolve(stmt.body);
        loop_on = previous;
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
        // Super can be called inside sub-class only.
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
        expr.array.forEach(this::resolve);
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

    /**
     * Scans a specific Statement Node representing a single statement.
     */
    private void resolve(Stmt stmt) {
        // Calling accept on node executes the necessary function defined in the object passed as parameter.
        stmt.accept(this);
    }

    /**
     * Scans a specific Expression Node.
     */
    private void resolve(Expr expr) {
        // Calling accept on node executes the necessary function defined in the object passed as parameter.
        expr.accept(this);
    }

    /**
     * Scans through the list of Statement Nodes where each Node represents each
     * single statement. The resolver starts its execution from this point, when
     * the root of the AST is passed as the list of statements.
     *
     * @param statements List of Statements.
     * @see Stmt
     */
    void resolve(List<Stmt> statements) {
        statements.forEach(this::resolve);
    }

    /**
     * Resolves any type of function.
     *
     * @param function Node representing the function, method, or constructor.
     * @param type     Type of the function.
     */
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

    /**
     * Begins a new scope.
     */
    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    /**
     * Terminates the previous scope.
     */
    private void endScope() {
        scopes.pop();
    }

    /**
     * Defines a new variable without initializing it i.e., it represents
     * variable declaration.
     *
     * @param name Variable identifier
     */
    private void declare(Token name) {
        if (scopes.isEmpty()) {
            // Added to check already defined variable in global scope
            try {
                interpreter.globals.get(name);
            } catch (RuntimeError e) {
                if (!global_scope.containsKey(name.lexeme)) {
                    global_scope.put(name.lexeme, false);
                    return;
                }
            }
            error(name,
                    "Variable '" + name.lexeme + "' is already declared in the this scope.");
        }

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            error(name,
                    "Variable '" + name.lexeme + "' is already declared in this scope.");
        }

        scope.put(name.lexeme, false);
    }

    /**
     * Initializes a already defined variable i.e., it represents variable
     * initialization.
     *
     * @param name Variable identifier
     */
    private void define(Token name) {
        if (scopes.isEmpty()) {
            global_scope.put(name.lexeme, true);
            return;
        }
        scopes.peek().put(name.lexeme, true);
    }

    /**
     * A very important utility function used to define the scope depth for any
     * local variable.
     *
     * @param expr Associated expression node required for mapping
     * @param name Variable identifier
     */
    private boolean resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return true;
            }
        }

        // Not found. Assume it is global.             
        try {
            interpreter.globals.get(name);
            return true;
        } catch (RuntimeError e) {
            return global_scope.containsKey(name.lexeme);
        }
    }

    /**
     * Generates an instance of error having a specific message, including a
     * token as reference.
     *
     * @param token   A token instance as reference.
     * @param message Error message describing the error instance.
     * @see RuntimeError
     */
    private void error(Token token, String message) {
        throw new RuntimeError(message, token);
    }

}
