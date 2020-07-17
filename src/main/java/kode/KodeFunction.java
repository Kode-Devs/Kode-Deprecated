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
    public int arity() {
        return declaration.params.length;
    }

    @Override
    public Object call(Object[] arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.length; i++) {
            environment.define(declaration.params[i].lexeme, arguments[i]);
        }

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
}
