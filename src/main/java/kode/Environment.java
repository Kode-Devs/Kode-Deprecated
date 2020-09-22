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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dell
 */
class Environment {

    final Environment enclosing;
    final Map<String, Object> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) {
            return enclosing.get(name);
        }

        if (Kode.BUILTIN_MODULE.inter.globals.values.containsKey(name.lexeme)) {
            return Kode.BUILTIN_MODULE.inter.globals.get(name);
        }

        throw new RuntimeError(
                "Undefined variable '" + name.lexeme + "'.",
                name);
    }

    Object get(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }

        if (enclosing != null) {
            return enclosing.get(name);
        }

        if (Kode.BUILTIN_MODULE.inter.globals.values.containsKey(name)) {
            return Kode.BUILTIN_MODULE.inter.globals.get(name);
        }

        throw new RuntimeError(
                "Undefined variable '" + name + "'.");
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        if (Kode.BUILTIN_MODULE.inter.globals.values.containsKey(name.lexeme)) {
            Kode.BUILTIN_MODULE.inter.globals.define(name.lexeme, value);
            return;
        }

        throw new RuntimeError(
                "Undefined variable '" + name.lexeme + "'.",
                name);
    }

    void define(String name, Object value) {
        values.put(name, value);
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }
}
