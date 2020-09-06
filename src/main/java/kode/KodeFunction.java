/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.ArrayList;
import java.util.Arrays;
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
    KodeInstance instance;

    KodeFunction(Stmt.Function declaration, Environment closure, Interpreter inter, boolean isInitializer) {
        this.instance = null;
        this.isInitializer = isInitializer;
        this.closure = closure;
        this.declaration = declaration;
        this.interpreter = inter;
    }

    @Override
    public boolean isBind() {
        return this.instance != null;
    }

    KodeFunction bind(KodeInstance instance) {
        KodeFunction bind = new KodeFunction(declaration, new Environment(closure), interpreter, isInitializer);
        bind.instance = instance;
        return bind;
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
        KodeInstance This = this.instance;
        if (This != null) {
            ArrayList asList = new ArrayList(Arrays.asList(arguments));
            asList.add(0, This);
            arguments = asList.toArray();
        }

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
                return arguments[0];
            }

            return returnValue.value;
        }

        if (isInitializer) {
            return arguments[0];
        }
        return null;
    }
}
