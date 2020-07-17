/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import math.KodeMath;
import math.KodeNumber;

/**
 *
 * @author dell
 */
class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Object> {

    Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    Object interpret(List<Stmt> statements) throws Exception {
        try {
            Object ret = null;
            for (Stmt statement : statements) {
                ret = execute(statement);
            }
            return ret;
        } catch (RuntimeError error) {
            throw error;
        } catch (StackOverflowError error) {
            throw new RuntimeError("Max Depth of Recursion Exceeded.");
        }
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

        if (expr.name.lexeme.equals(Kode.CLASS)) {
            throw new RuntimeError("Can not change '" + expr.name.lexeme + "' attribute of any instance.", expr.name);
        }

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

    Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    Object execute(Stmt stmt) {
        return stmt.accept(this);
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    void executeBlock(List<Stmt> statements, Environment env) {
        Environment previous = this.environment;
        try {
            this.environment = env;
            for (Stmt statement : statements) {
                execute(statement);
            };
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
        });

        KodeClass klass = new KodeClass(stmt.name.lexeme, (KodeClass) superclass, methods, this);

        if (superclass != null) {
            environment = environment.enclosing;
        }

        klass.__doc__ = stmt.doc;
        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Object visitExpressionStmt(Stmt.Expression stmt) {
        return evaluate(stmt.expression);
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        KodeFunction function = new KodeFunction(stmt, environment, this, false);
        function.__doc__ = stmt.doc;
        environment.define(stmt.name.lexeme, function);
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
                module.inter.globals.define(Kode.__NAME__,
                        module.inter.toKodeValue(stmt.imp.fn));
                try {
                    module.run();
                } catch (Exception e) {
                    Kode.ModuleRegistry.remove(join);
                    throw e;
                }
            }
            if (module.hadError || module.hadRuntimeError) {
                return null;
            }
            if (stmt.methods != null) {
                stmt.methods.forEach((item) -> {
                    environment.define(item.lexeme, module.get(item));
                });
            } else {
                environment.define(stmt.alias != null ? stmt.alias.lexeme : dir.get(dir.size() - 1), module);
            }
        } catch (RuntimeError e) {
            e.token.add(stmt.imp);
            throw e;
        } catch (Error | Exception e) {
            throw new RuntimeError("Failed to Import Module '" + String.join(".", dir) + "'.", stmt.imp);
        }
        return null;
    }

    @Override
    public Void visitRaiseStmt(Stmt.Raise stmt) {
        Object value = evaluate(stmt.value);
        if (value instanceof KodeInstance) {
            if (ValueNotImplemented.isNotImplemented((KodeInstance) value)) {
                RuntimeError e = new NotImplemented((KodeInstance) value);
                e.token.add(stmt.keyword);
                throw e;
            }
            if (Kode.instanceOf((KodeInstance) value, ValueError.val)) {
                RuntimeError e = new RuntimeError((KodeInstance) value);
                e.token.add(stmt.keyword);
                throw e;
            }
        }
        throw new RuntimeError("Can only raise intances of Error class or its sub-classes but found element of type '"
                + Kode.type(value) + "'.", stmt.keyword);
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        throw new Return(stmt.value != null ? evaluate(stmt.value) : null);
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
            Object value = ValueNone.create();
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
        int cnt = 0;
        while (isTruthy(evaluate(stmt.condition))) {
            try {
                if (cnt == 100000) {
                    IO.printf_err("[INFO]: The While Loop has already iterated for a lot of time...\nDo you want to Continue iterating ?");
                    if (!IO.scanf().equalsIgnoreCase("y")) {
                        throw new RuntimeError("User Cancelled.");
                    }
                    cnt = 0;
                }
                cnt++;
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
        int cnt = 0;
        if (stmt.init != null) {
            execute(stmt.init);
        }
        while (stmt.condition != null ? isTruthy(evaluate(stmt.condition)) : true) {
            try {
                if (cnt == 100000) {
                    IO.printf_err("[INFO]: The For Loop has already iterated for a lot of time...\nDo you want to Continue iterating ?");
                    if (!IO.scanf().equalsIgnoreCase("y")) {
                        throw new RuntimeError("User Cancelled.");
                    }
                    cnt = 0;
                }
                cnt++;
                execute(stmt.body);
            } catch (Break b) {
                break;
            } catch (Continue c) {
            }
            if (stmt.increment != null) {
                execute(stmt.increment);
            }
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
                    return ((KodeFunction) fun).call(right);
                }
            } catch (NotImplemented e1) {
                try {
                    Object fun = ((KodeInstance) right).get(rop);
                    if (fun instanceof KodeFunction) {
                        return ((KodeFunction) fun).call(left);
                    }
                } catch (NotImplemented e2) {
                }
            }
        }
        if (lop.equals(rop) && lop.equals(Kode.EQ)) {
            return this.toKodeValue(Objects.equals(left, right));
        }
        if (lop.equals(rop) && lop.equals(Kode.NE)) {
            return this.toKodeValue(!Objects.equals(left, right));
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
                return BinOP(left, right, Kode.GT, Kode.GT, expr.operator);
            case GREATER_EQUAL:
                return BinOP(left, right, Kode.GE, Kode.GE, expr.operator);
            case LESS:
                return BinOP(left, right, Kode.LT, Kode.LT, expr.operator);
            case LESS_EQUAL:
                return BinOP(left, right, Kode.LE, Kode.LE, expr.operator);
            case MINUS:
                return BinOP(left, right, Kode.SUB, Kode.RSUB, expr.operator);
            case PLUS:
                return BinOP(left, right, Kode.ADD, Kode.RADD, expr.operator);
            case SLASH:
                return BinOP(left, right, Kode.TRUE_DIV, Kode.RTRUE_DIV, expr.operator);
            case STAR:
                return BinOP(left, right, Kode.MUL, Kode.RMUL, expr.operator);
            case PERCENT:
                return BinOP(left, right, Kode.MOD, Kode.RMOD, expr.operator);
            case BACKSLASH:
                return BinOP(left, right, Kode.FLOOR_DIV, Kode.RFLOOR_DIV, expr.operator);
            case POWER:
                return BinOP(left, right, Kode.POWER, Kode.RPOWER, expr.operator);
            case BANG_EQUAL:
                return BinOP(left, right, Kode.NE, Kode.NE, expr.operator);
            case EQUAL_EQUAL:
                return BinOP(left, right, Kode.EQ, Kode.EQ, expr.operator);
        }

        // Unreachable.                                
        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        if (!(callee instanceof KodeCallable)) {
            throw new RuntimeError("Can only call functions and classes.", expr.paren);
        }
        
        // TODO Make Some Adjustments here.
        Object[] arguments = expr.arguments;
//        Object[] arguments = new Object[expr.arguments.length];
        for (int i = 0; i < expr.arguments.length; i++) {
            arguments[i] = evaluate(expr.arguments[i]);
        }

        KodeCallable function = (KodeCallable) callee;
        if (arguments.length != function.arity()) {
            throw new RuntimeError("Expected "
                    + function.arity() + " arguments but got "
                    + arguments.length + ".", expr.paren);
        }
        return function.call(arguments);
    }

    @Override
    public Object visitGetATIndexExpr(Expr.GetAtIndex expr) {
        Object array = evaluate(expr.array);
        Object index = evaluate(expr.index);
        if (array instanceof KodeInstance) {
            KodeFunction method = ((KodeInstance) array).klass.findMethod(Kode.GET_ITEM);
            try {
                return method.bind((KodeInstance) array).call(index);
            } catch (NotImplemented e) {
                throw new RuntimeError(Kode.type(array) + " object is non-indexable.", expr.paren);
            }
        } else {
            throw new RuntimeError(Kode.type(array) + " object is non-indexable.", expr.paren);
        }
    }

    @Override
    public Object visitSetATIndexExpr(Expr.SetAtIndex expr) {
        Object value = evaluate(expr.value);
        Object array = evaluate(expr.array);
        Object index = evaluate(expr.index);
        if (array instanceof KodeInstance) {
            KodeFunction method = ((KodeInstance) array).klass.findMethod(Kode.SET_ITEM);
            try {
                return method.bind((KodeInstance) array).call(index, value);
            } catch (NotImplemented e) {
                throw new RuntimeError(Kode.type(array) + " object does not support item assignment.", expr.paren);
            }
        } else {
            throw new RuntimeError(Kode.type(array) + " object is non-indexable.", expr.paren);
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
                            return ((KodeFunction) fun).call();
                        }
                    } catch (NotImplemented e2) {
                    }
                }
                throw new RuntimeError("Unary Operation '" + expr.operator.lexeme
                        + "' can not be performed on operand of type '" + Kode.type(right) + "'.", expr.operator);
            case PLUS:
                if (right instanceof KodeInstance) {
                    try {
                        Object fun = ((KodeInstance) right).get(Kode.POS);
                        if (fun instanceof KodeFunction) {
                            return ((KodeFunction) fun).call();
                        }
                    } catch (NotImplemented e2) {
                    }
                }
                throw new RuntimeError("Unary Operation '" + expr.operator.lexeme
                        + "' can not be performed on operand of type '" + Kode.type(right) + "'.", expr.operator);
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
        expr.path.forEach(i -> temp.add(i.lexeme));
        String className = String.join(".", temp);
        return new KodeNative(className, expr.pkg, this);
    }

    @Override
    public Object visitTryStmt(Stmt.Try stmt) {
        try {
            executeBlock(stmt.tryStmt, new Environment(environment));
        } catch (RuntimeError e) {
            KodeInstance instance = e.instance;
            for (Stmt.Catch c : stmt.catchs) {
                Object err_type = c.ErrorType == null ? ValueError.val : evaluate(c.ErrorType);
                KodeClass cls;
                if (err_type instanceof ValueError) {
                    cls = (ValueError) err_type;
                } else {
                    throw new RuntimeError(c.ErrorType.name.lexeme + " is not a Error class name", c.ErrorType.name);
                }
                if (Kode.instanceOf(instance, cls)) {
                    c.instance = instance;
                    this.execute(c);
                    return null;
                }
            }
            throw e;
        }
        return null;
    }

    @Override
    public Object visitCatchStmt(Stmt.Catch stmt) {
        Environment env = new Environment(this.environment);
        if (stmt.alias != null) {
            env.define(stmt.alias.lexeme, stmt.instance);
        }
        executeBlock(stmt.catchStmt, env);
        return null;
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (boolean) object;
        }
        if (object instanceof KodeNumber) {
            return KodeMath.equal((KodeNumber) object, KodeNumber.valueOf("0"));
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
                return isTruthy(((KodeFunction) method).bind((KodeInstance) object).call());
            }
            return true;
        }
        return true;
    }

    Object toKodeValue(Object value) {
        if (value == null) {
            return ValueNone.create();
        } else if (value instanceof KodeNumber) {
            return ValueNumber.create((KodeNumber) value);
        } else if (value instanceof Number) {
            return ValueNumber.create(KodeNumber.valueOf((Number) value));
        } else if (value instanceof String) {
            return ValueString.create((String) value);
        } else if (value instanceof Character) {
            return ValueString.create("" + value);
        } else if (value instanceof Boolean) {
            return ValueBool.create((Boolean) value);
        } else if (value instanceof List) {
            List ll = new ArrayList();
            for (Object item : (List) value) {
                ll.add(this.toKodeValue(item));
            }
            return ValueList.create(ll);
        } else if (value.getClass().isArray()) {
            return this.toKodeValue(convertToObjectArray(value));
        }
        return value;
    }

    private List convertToObjectArray(Object array) {
        Class ofArray = array.getClass().getComponentType();
        if (ofArray.isPrimitive()) {
            List ar = new ArrayList();
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                ar.add(Array.get(array, i));
            }
            return ar;
        } else {
            return Arrays.asList((Object[]) array);
        }
    }

    static Object toJava(Object value) {
        if (value instanceof KodeInstance) {
            if (ValueNone.isNone((KodeInstance) value)) {
                return null;
            }
            if (ValueNumber.isNumber((KodeInstance) value)) {
                return ValueNumber.toNumber(value);
            }
            if (ValueString.isString((KodeInstance) value)) {
                return ValueString.toStr(value);
            }
            if (ValueBool.isBool((KodeInstance) value)) {
                return ValueBool.toBoolean(value);
            }
            if (ValueList.isList((KodeInstance) value)) {
                return ValueList.toList(value).stream().map(Interpreter::toJava).collect(Collectors.toList());
            }
        }
        return value;
    }

}
