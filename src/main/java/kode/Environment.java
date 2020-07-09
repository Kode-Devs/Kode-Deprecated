/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        
        if (Kode.INTER.globals.values.containsKey(name.lexeme)){
            return Kode.INTER.globals.get(name);
        }

        throw new RuntimeError(
                "Undefined variable '" + name.lexeme + "'.",
                name);
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
        
        if (Kode.INTER.globals.values.containsKey(name.lexeme)){
            Kode.INTER.globals.define(name.lexeme, value);
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
