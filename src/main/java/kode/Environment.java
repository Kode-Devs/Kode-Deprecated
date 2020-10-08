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
 * Acts as the Symbol Table to store the values of the declared variables.
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
class Environment {

    /**
     * Reference to the parent symbol table, representing outer scope.
     */
    final Environment enclosing;

    /**
     * Data-structure which acts as the actual storage for the values of the
     * variables, mapping the variable name and its value.
     */
    final Map<String, Object> values = new HashMap<>();

    /**
     * Generates a new Symbol Table, as root.
     */
    Environment() {
        this(null);
    }

    /**
     * Generates a new Symbol Table, also referencing another Symbol Table as
     * its parent.
     *
     * @param enclosing Child Symbol Table.
     */
    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    /**
     * Retrieve the value of an variable from the Symbol Table and its parent,
     * by using its name.
     *
     * @param name Variable Name as Token Object.
     * @return Returns the value stored.
     * @throws RuntimeError If the variable with the specified name not found.
     */
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

    /**
     * Recursively Retrieve the value of an variable from the Symbol Table and
     * its parents, by using its name.
     *
     * @param name Variable Name as String.
     * @return Returns the value stored.
     * @throws RuntimeError If the variable with the specified name not found.
     */
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

    /**
     * Recursively Assigns a new value to a variable if and only if the variable
     * is already present in the symbol table or its parents.
     *
     * @param name Variable Name as Token Object.
     * @param value New value of the variable.
     * @throws RuntimeError If the variable with the specified name not found in
     * the symbol table.
     */
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

    /**
     * Stores a new variable in the symbol table, with an initial value.
     *
     * @param name Variable Name as String.
     * @param value Initial Value of the Variable.
     */
    void define(String name, Object value) {
        values.put(name, value);
    }

    /**
     * Returns reference to the ancestor table having n<sup>th</sup> distance
     * from the current symbol table.
     *
     * @param distance The value of n.
     * @return Returns reference to the ancestor, or {@code null}, if the
     * ancestor is not found.
     */
    private Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance && environment != null; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

    /**
     * Retrieve the value of an variable from the ancestor table having a
     * specific distance from the current Symbol Table , by using its name.
     *
     * @param distance The distance between the current table and its ancestor.
     * @param name Name of the Variable as String.
     * @return Returns the value stored in the variable, if found, or
     * {@code null} if not found.
     */
    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    /**
     * Defines a new variable or assigns a new value to an old variable in the
     * ancestor table having a specific distance from the current Symbol Table ,
     * by using its name.
     *
     * @param distance The distance between the current table and its ancestor.
     * @param name Name of the Variable as Token Object.
     * @param value New value to be assigned.
     */
    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }
}
