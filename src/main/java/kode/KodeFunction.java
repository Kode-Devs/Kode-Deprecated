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

import kni.KodeCallable;
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
