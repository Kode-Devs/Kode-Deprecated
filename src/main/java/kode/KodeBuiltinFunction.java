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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is used to represent any builtin function/method, and thus used to
 * define them.
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
final class KodeBuiltinFunction extends KodeFunction {

    final String fun_name;
    private final int arity;
    private final VarArgFunction<Object, Object> call;

    /**
     * Generates a new builtin function/method/constructor object.
     *
     * @param name Name of the function.
     * @param inter Associated interpreter reference.
     * @param doc Doc-string or help text associated with the function, or
     * {@code null} for missing documentation.
     * @param arity Arity of the function.
     * @param call A lambda method defining the actual work of the function or
     * the body of the function written in Java.
     */
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

    /**
     * Functional Interface for function with variable parameter input and one
     * output.
     *
     * @param <R> Return data type.
     * @param <T> Parameter data type.
     */
    @FunctionalInterface
    interface VarArgFunction<R, T> {

        R apply(T... args);
    }
}
