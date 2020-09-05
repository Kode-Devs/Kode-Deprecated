/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author dell
 */
final class KodeBuiltinFunction extends KodeFunction {

    final String fun_name;
    private final int arity;
    private final VarArgFunction<Object, Object> call;

    KodeBuiltinFunction(String name, Interpreter inter, String doc, int arity, VarArgFunction<Object, Object> call) {
        super(null, null, inter, false);
        this.fun_name = name;
        this.__doc__ = doc;
        this.arity = arity;
        this.call = call;
        this.instance = null;
    }

    @Override
    KodeFunction bind(KodeInstance instance) {
        KodeBuiltinFunction bind = new KodeBuiltinFunction(this.fun_name, this.interpreter, this.__doc__, this.arity, this.call);
        bind.instance = instance;
        return bind;
    }

    @Override
    public String toString() {
        return "<built-in function '" + fun_name + "'>";
    }

    @Override
    public int arity() {
        return this.arity;
    }

    @Override
    public Object call(Object... arguments) {
        KodeInstance This = this.instance;
        if (This != null) {
            ArrayList asList = new ArrayList(Arrays.asList(arguments));
            asList.add(0, This);
            arguments = asList.toArray();
        }
        return this.call.apply(arguments);
    }

    @FunctionalInterface
    interface VarArgFunction<R, T> {

        R apply(T... args);
    }
}
