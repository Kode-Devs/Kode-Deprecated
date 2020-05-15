/*
 * MIT License
 *
 * Copyright (c) 2020 Edumate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.edumate.kode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Object> {

    Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    Object interpret(List<Stmt> statements) {
        try {
            Object ret = null;
            for (Stmt statement : statements) {
                ret = execute(statement);
            }
            return ret;
        } catch (RuntimeError error) {
            Kode.runtimeError(error);
        } catch (StackOverflowError error) {
            Kode.runtimeError(new RuntimeError("Max Depth of Recursion Exceeded."));
        }
        return null;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return this.toKodeValue(expr.value);
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) {
                return left;
            }
        } else {
            if (!isTruthy(left)) {
                return left;
            }
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);

        if (object instanceof KodeInstance) {
            Object value = evaluate(expr.value);
            ((KodeInstance) object).set(expr.name, value);
            return value;
        }

        throw new RuntimeError("Only instances have fields.", expr.name);
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        KodeClass superclass = (KodeClass) environment.getAt(distance, "super");

        // "this" is always one level nearer than "super"'s environment.
        KodeInstance object = (KodeInstance) environment.getAt(
                distance - 1, "this");

        KodeFunction method = superclass.findMethod(expr.method.lexeme);

        if (method == null) {
            throw new RuntimeError(
                    "Undefined property '" + expr.method.lexeme + "'.",
                    expr.method);
        }

        return method.bind(object);
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private Object execute(Stmt stmt) {
        return stmt.accept(this);
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            statements.forEach((statement) -> {
                execute(statement);
            });
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if (!(superclass instanceof KodeClass)) {
                throw new RuntimeError(
                        "Superclass must be a class.",
                        stmt.superclass.name);
            }
        }

        environment.define(stmt.name.lexeme, null);

        if (stmt.superclass != null) {
            environment = new Environment(environment);
            environment.define("super", superclass);
        }

        Map<String, KodeFunction> methods = new HashMap<>();
        stmt.methods.forEach((method) -> {
            KodeFunction function = new KodeFunction(method, environment, this, method.name.lexeme.equals(Kode.INIT));
            methods.put(method.name.lexeme, function);

            method.params.forEach((par) -> {
                method.args.add(new Pair(par.key, par.value != null ? evaluate(par.value) : null));
            });
        });

        KodeClass klass = new KodeClass(stmt.name.lexeme, (KodeClass) superclass, methods, this);

        if (superclass != null) {
            environment = environment.enclosing;
        }

        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Object visitExpressionStmt(Stmt.Expression stmt) {
//        evaluate(stmt.expression);
//        return null;
        return evaluate(stmt.expression);
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        KodeFunction function = new KodeFunction(stmt, environment, this, false);
        environment.define(stmt.name.lexeme, function);
        stmt.params.forEach((par) -> {
            stmt.args.add(new Pair(par.key, par.value != null ? evaluate(par.value) : null, par.star));
        });
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitRequireStmt(Stmt.Require stmt) {
        List<String> dir = new ArrayList<>();
        stmt.dir.forEach((item) -> {
            dir.add(item.lexeme);
        });

        String join = String.join(File.separator, dir);
        try {
            KodeModule module;
            if (Kode.ModuleRegistry.containsKey(join)) {
                module = Kode.ModuleRegistry.get(join);
            } else {
                module = new KodeModule(String.join(".", dir), join);
                Kode.ModuleRegistry.put(join, module);
                module.run();
            }
            if (module.hadError || module.hadRuntimeError) {
                throw new Exception();
            }
            if (stmt.methods != null) {
                stmt.methods.forEach((item) -> {
                    this.globals.define(item.lexeme, module.get(item));
                });
            } else {
                this.globals.define(stmt.alias != null ? stmt.alias.lexeme : dir.get(dir.size() - 1), module);
            }
        } catch (RuntimeError e) {
            e.token.add(stmt.imp);
            throw e;
        } catch (Error | Exception e) {
            throw new RuntimeError("Failed to Import Module '" + String.join(".", dir) + "'", stmt.imp);
        }
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) {
            value = evaluate(stmt.value);
        }

        throw new Return(value);
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        throw new Break();
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        throw new Continue();
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        for (int i = 0; i < stmt.name.size(); i++) {
            Object value = ValueNone.create(this);
            if (stmt.initial.get(i) != null) {
                value = evaluate(stmt.initial.get(i));
                if (value == null) {
                    throw new RuntimeError("The expression associated with variable '" + stmt.name.get(i).lexeme + "' does not return any value.", stmt.name.get(i));
                }
            }
            environment.define(stmt.name.get(i).lexeme, value);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
            } catch (Break b) {
                break;
            } catch (Continue c) {
            }
        }
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        execute(stmt.init);
        while (isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
            } catch (Break b) {
                break;
            } catch (Continue c) {
            }
            execute(stmt.increment);
        }
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        if (value == null) {
            throw new RuntimeError("The expression associated with variable '" + expr.name.lexeme + "' does not return any value.", expr.name);
        }
        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        return value;
    }

    private Object BinOP(Object left, Object right, String lop, String rop, Token op) {
        if (left instanceof KodeInstance && right instanceof KodeInstance) {
            try {
                Object fun = ((KodeInstance) left).get(lop);
                if (fun instanceof KodeFunction) {
                    return ((KodeFunction) fun).call(Arrays.asList(right));
                }
            } catch (NotImplemented e1) {
                try {
                    Object fun = ((KodeInstance) right).get(rop);
                    if (fun instanceof KodeFunction) {
                        return ((KodeFunction) fun).call(Arrays.asList(left));
                    }
                } catch (NotImplemented e2) {
                }
            }
        }
        throw new RuntimeError("Binary Operation '" + op.lexeme + "' can not be performed between operands of type '"
                + Kode.type(left) + "' and '" + Kode.type(right) + "'.", op);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case MINUS:
                return BinOP(left, right, Kode.SUB, Kode.RSUB, expr.operator);
            case PLUS:
                return BinOP(left, right, Kode.ADD, Kode.RADD, expr.operator);
            case SLASH:
                return BinOP(left, right, Kode.DIV, Kode.RDIV, expr.operator);
            case STAR:
                return BinOP(left, right, Kode.MUL, Kode.RMUL, expr.operator);
            case PERCENT:
                return BinOP(left, right, Kode.MOD, Kode.RMOD, expr.operator);
            case BACKSLASH:
                return BinOP(left, right, Kode.FLOOR_DIV, Kode.RFLOOR_DIV, expr.operator);
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }

        // Unreachable.                                
        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Pair<Token, Object>> arguments = new ArrayList<>();
        expr.arguments.forEach((argument) -> {
            arguments.add(new Pair(argument.key, evaluate(argument.value)));
        });

        if (!(callee instanceof KodeCallable)) {
            throw new RuntimeError(
                    "Can only call functions and classes.",
                    expr.paren);
        }

        KodeCallable function = (KodeCallable) callee;
        List<Pair<String, Object>> arity = function.arity();
        Map<String, Object> map = new HashMap();
        arity.forEach((item) -> {
            if (item.value != null) {
                map.put(item.key, item.value);
            }
        });
        int i = 0;
        int j = 0;
        try {
            while (i < arguments.size()) {
                Pair<Token, Object> arg = arguments.get(i);
                if (arg.key == null && arity.get(j).star) {
                    List temp = new ArrayList();
                    for (; i < arguments.size(); i++) {
                        if (arguments.get(i).key != null) {
                            i--;
                            break;
                        }
                        temp.add(arguments.get(i).value);
                    }
                    map.put(arity.get(j).key, toKodeValue(temp));
                } else {
                    map.put(arg.key == null ? arity.get(j).key : arg.key.lexeme, arg.value);
                }
                i++;
                j++;
            }
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeError("Number of argument crossed.", expr.paren);
        }
        arity.forEach((name) -> {
            if (!map.keySet().contains(name.key)) {
                throw new RuntimeError("Argument '" + name.key + "' requirment not met.", expr.paren);
            }
        });
        try {
            return function.call(map);
        } catch (RuntimeError e) {
            e.token.add(expr.paren);
            throw e;
        }
    }

    @Override
    public Object visitGetATIndexExpr(Expr.GetAtIndex expr) {
        Object array = evaluate(expr.array);
        Object index = evaluate(expr.index);
        if (array instanceof KodeInstance) {
            KodeFunction method = ((KodeInstance) array).klass.findMethod(Kode.GET_AT_INDEX);
            try {
                return method.bind((KodeInstance) array).call(Arrays.asList(index));
            } catch (NotImplemented e) {
                throw new RuntimeError("Non Indexable object as no attribute index.", expr.paren);
            }
        } else {
            throw new RuntimeError("Non Indexable object as no attribute index.", expr.paren);
        }
    }

    @Override
    public Object visitSetATIndexExpr(Expr.SetAtIndex expr) {
        Object value = evaluate(expr.value);
        Object array = evaluate(expr.array);
        Object index = evaluate(expr.index);
        if (array instanceof KodeInstance) {
            KodeFunction method = ((KodeInstance) array).klass.findMethod(Kode.SET_AT_INDEX);
            try {
                return method.bind((KodeInstance) array).call(Arrays.asList(index, value));
            } catch (NotImplemented e) {
                throw new RuntimeError(Kode.type(array)+" object is non-indexable.", expr.paren);
            }
        } else {
            throw new RuntimeError(Kode.type(array)+" object is non-indexable.", expr.paren);
        }
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object == null) {
            throw new RuntimeError(
                    "No such object found",
                    expr.name);
        }
        if (object instanceof KodeInstance) {
            return ((KodeInstance) object).get(expr.name);
        }

        throw new RuntimeError(
                "Only instances have properties.",
                expr.name);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return this.toKodeValue(!isTruthy(right));
            case MINUS:
                if (right instanceof KodeInstance) {
                    try {
                        Object fun = ((KodeInstance) right).get(Kode.NEG);
                        if (fun instanceof KodeFunction) {
                            return ((KodeFunction) fun).call(Arrays.asList());
                        }
                    } catch (NotImplemented e2) {
                    }
                }
                throw new RuntimeError("Unary Operation '" + expr.operator.lexeme
                        + "' can not be performed on operand of type '" + Kode.type(right) + "'.", expr.operator);
            case PLUS:
                return right;
        }

        // Unreachable.                              
        return right;
    }

    @Override
    public Object visitArrayExpr(Expr.Array expr) {
        List arr = new ArrayList();
        expr.array.forEach((ex) -> {
            arr.add(evaluate(ex));
        });
        return toKodeValue(arr);
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    @Override
    public Object visitNativeExpr(Expr.Native expr) {
        List<String> temp = new ArrayList();
        for (int i = 0; i < expr.path.size() - 1; i++) {
            temp.add(expr.path.get(i).lexeme);
        }
        String className = String.join(".", temp);
        String methodName = expr.path.get(expr.path.size() - 1).lexeme;
        return new JavaNative(className, methodName, null, this);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }
        throw new RuntimeError("Operand must be a number.", operator);
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }

        throw new RuntimeError("Operands must be numbers.", operator);
    }

    boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (boolean) object;
        }
        if (object instanceof Double) {
            return ((Double) object) != 0;
        }
        if (object instanceof String) {
            return ((String) object).length() != 0;
        }
        if (object instanceof List) {
            return !((List) object).isEmpty();
        }
        if (object instanceof KodeInstance) {
            if (ValueBool.isBool((KodeInstance) object)) {
                return ValueBool.toBoolean(object);
            }
            Object method = ((KodeInstance) object).get(Kode.BOOLEAN);
            if (method instanceof KodeFunction) {
                return isTruthy(((KodeFunction) method).bind((KodeInstance) object).call(new HashMap()));
            }
            return true;
        }
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        // nil is only equal to nil.               
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }

        return a.equals(b);
    }

    Object toKodeValue(Object value) {
        if (value == null) {
            return ValueNone.create(this);
        } else if (value instanceof Double) {
            return ValueNumber.create((Double) value, this);
        } else if (value instanceof String) {
            return ValueString.create((String) value, this);
        } else if (value instanceof Boolean) {
            return ValueBool.create((Boolean) value, this);
        } else if (value instanceof List) {
            return ValueList.create((List) value, this);
        }
        return value;
    }

    Object toJava(Object value) {
        if (value instanceof KodeInstance) {
            if (ValueNone.isNone((KodeInstance) value)) {
                return null;
            }
            if (ValueString.isString((KodeInstance) value)) {
                return ValueString.toStr(value);
            }
            if (ValueBool.isBool((KodeInstance) value)) {
                return ValueBool.toBoolean(value);
            }
            if (ValueList.isList((KodeInstance) value)) {
                return ValueList.toList(value);
            }
        }
        return value;
    }

}
