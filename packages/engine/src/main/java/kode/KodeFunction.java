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
import java.util.List;

import kni.KodeObject;

/**
 * This class is used to represent any non-builtin function/method.
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
class KodeFunction extends KodeCallable {

    /**
     * Doc-string or help text associated with the function, or {@code null} for
     * missing documentation.
     */
    String __doc__ = null;

    final Stmt.Function declaration;
    Environment closure;
    final boolean isInitializer;
    Interpreter interpreter;
    KodeInstance instance;

    /**
     * Generates a new non-builtin function/method/constructor object.
     *
     * @param declaration   Actual declaration of the function i.e., the function
     *                      node from the AST.
     * @param closure       Associated Symbol table.
     * @param inter         Associated interpreter.
     * @param isInitializer {@link boolean} value representing weather this
     *                      function is a constructor of a class or not.
     */
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

    /**
     * Associates an object with the method call for non-static methods.
     *
     * @param instance Reference to the object, needs to be associated.
     * @return Returns a new function object referencing the associated
     * instance.
     */
    KodeFunction bind(KodeInstance instance) {
        KodeFunction bind = new KodeFunction(declaration, new Environment(closure), interpreter, isInitializer);
        bind.instance = instance;
        bind.__doc__ = this.__doc__;
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
    public KodeObject __call__(KodeObject... arguments) {
        KodeInstance This = this.instance;
        if (This != null) {
            ArrayList<KodeObject> asList = new ArrayList<>(Arrays.asList(arguments));
            asList.add(0, This);
            arguments = asList.toArray(arguments);
        }

        Environment environment = new Environment(closure);
        if (declaration.params.length != 0) {
            for (int i = 0; i < declaration.params.length - 1; i++) {
                environment.define(declaration.params[i].lexeme, arguments[i]);
            }
            if (declaration.params[declaration.params.length - 1].lexeme.equals(Kode.VARARGIN)) {
                List<Object> varargin = new ArrayList<>(Arrays.asList(arguments).subList(declaration.params.length - 1, arguments.length));
                environment.define(Kode.VARARGIN, Interpreter.toKodeValue(varargin));
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

    @Override
    public KodeObject get(String name) {
        throw new RuntimeError("Not supported yet.");
    }

    @Override
    public void set(String name, KodeObject value) {
        throw new RuntimeError("Not supported yet.");
    }
}
