/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
class KodeFunction implements KodeCallable {

    String __doc__ = null;

    final Stmt.Function declaration;
    Environment closure;
    final boolean isInitializer;
    Interpreter interpreter;

    KodeFunction(Stmt.Function declaration, Environment closure, Interpreter inter, boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.closure = closure;
        this.declaration = declaration;
        this.interpreter = inter;
    }

    KodeFunction bind(KodeInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new KodeFunction(declaration, environment, interpreter, isInitializer);
    }

    @Override
    public String toString() {
        return "<function '" + declaration.name.lexeme + "'>";
    }

    @Override
    public List<Pair<String, Object>> arity() {
        List<Pair<String, Object>> temp = new ArrayList();
        declaration.args.forEach((t) -> {
            temp.add(new Pair(t.key.lexeme, t.value).setType(t.type));
        });
        return temp;
    }

    @Override
    public Object call(Map<String, Object> arguments) {
        Environment environment = new Environment(closure);
        arguments.entrySet().forEach((arg) -> {
            environment.define(arg.getKey(), arg.getValue());
        });

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) {
                return closure.getAt(0, "this");
            }

            return returnValue.value;
        }

        if (isInitializer) {
            return closure.getAt(0, "this");
        }
        return null;
    }

    Object call(List<Object> args) {
        List<Pair<String, Object>> arity = this.arity();
        if (arity.size() != args.size()) {
            throw new RuntimeError("Number of argument crossed.");
        }
        Map<String, Object> arguments = new HashMap();
        for (int i = 0; i < arity.size(); i++) {
            arguments.put(arity.get(i).key, args.get(i) != null ? args.get(i) : arity.get(i).value);
        }
        return this.call(arguments);
    }
}
