/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.ArrayList;
import java.util.List;

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
        return declaration.params.length == 0 ? declaration.params.length
                : declaration.params.length * (declaration.params[declaration.params.length - 1].lexeme.equals(Kode.VARARGIN) ? -1 : 1);
    }

    @Override
    public Object call(Object... arguments) {
        Environment environment = new Environment(closure);
        if (declaration.params.length != 0) {
            for (int i = 0; i < declaration.params.length - 1; i++) {
                environment.define(declaration.params[i].lexeme, arguments[i]);
            }
            if (declaration.params[declaration.params.length - 1].lexeme.equals(Kode.VARARGIN)) {
                List<Object> varargin = new ArrayList<>();
                for (int j = declaration.params.length - 1; j < arguments.length; j++) {
                    varargin.add(arguments[j]);
                }
                environment.define(Kode.VARARGIN, this.interpreter.toKodeValue(varargin));
            } else {
                environment.define(declaration.params[declaration.params.length - 1].lexeme, arguments[declaration.params.length - 1]);
            }
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
