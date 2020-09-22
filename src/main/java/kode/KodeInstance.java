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
class KodeInstance {

    String __doc__ = null;

    KodeClass klass;
    Object data = null;
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
