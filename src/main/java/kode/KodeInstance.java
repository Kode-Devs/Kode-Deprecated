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
class KodeInstance {

    String __doc__ = null;

    KodeClass klass;
    Object data;
    boolean reccured = false; 
    Map<String, Object> fields = new HashMap<>();

    KodeInstance(KodeClass klass) {
        this.klass = klass;
        if (klass != null) {
            fields.put(Kode.CLASS, klass);
        }
    }

    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        KodeFunction method = klass.findMethod(name.lexeme);
        if (method != null) {
            return method.bind(this);
        }

        throw new RuntimeError(
                "Undefined property '" + name.lexeme + "'.",
                name);
    }

    Object get(String name) {
        if (fields.containsKey(name)) {
            return fields.get(name);
        }

        KodeFunction method = klass.findMethod(name);
        if (method != null) {
            return method.bind(this);
        }

        throw new RuntimeError(
                "Undefined property '" + name + "'.");
    }

    void set(Token name, Object value) {
        set(name.lexeme, value);
    }
    
    void set(String name, Object value) {
        fields.put(name, value);
    }

    @Override
    public String toString() {
        try {
            return ValueString.toStr(this);
        } catch (StackOverflowError e) {
            throw new RuntimeError("Max Depth of Recursion Exceeded.");
        }
    }

}
