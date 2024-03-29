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

import kni.KodeObject;

/**
 * This class is used to represent any callable object.
 *
 * <p>
 * To call the associated callable object use -<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;<code>Object result = callee.call(&lt;Array of parameters&gt;);</code><br>
 * where {@code callee} represents the reference to the callable object.</p>
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
abstract class KodeCallable implements ExtKodeObject {

    /**
     * For any non-static method, it returns {@code true} if an instance is
     * already associated with the method, else {@code false}.
     */
    abstract boolean isBind();

    /**
     * Returns the number of arguments for the callable object. A negative value
     * represents presence of variable argument input.
     *
     * <p>
     * For an example -<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;A callable with arity {@code n} represents, exact
     * {@code n} parameters, where as, another callable with arity {@code -n}
     * represents , {@code n-1} or more number of parameters.</p>
     */
    abstract int arity();

    /**
     * This method performs the actual work, and thus represents the actual
     * callable part of the object.
     *
     * @param arguments Array of parameters as per declaration.
     * @return The result after ending its execution or {@code null} if void.
     * For classes, it returns the generated object.
     */
    abstract KodeObject __call__(KodeObject... arguments);

    /**
     * {@inheritDoc}
     */
    @Override
    public final KodeObject call(KodeObject... arguments) {
        int len = this.isBind() ? arguments.length + 1 : arguments.length;
        int arity = this.arity();

        if (arity < 0 && len < -arity - 1) {
            throw new RuntimeError("Expected minimum " + (-arity - 1) + " arguments but got " + len + ".");
        } else if (arity >= 0 && len != arity) {
            throw new RuntimeError("Expected " + arity + " arguments but got " + len + ".");
        } else {
            return this.__call__(arguments);
        }
    }

    @Override
    public abstract String toString();
}
