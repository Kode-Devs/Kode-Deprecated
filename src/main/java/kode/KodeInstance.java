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
import kni.KodeObject;

/**
 * This class is used to represent an instance of an class.
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
class KodeInstance implements ExtKodeObject{

    /**
     * Doc-string or help text associated with the instance, or {@code null} for
     * missing documentation.
     */
    String __doc__ = null;

    /**
     * Associated class reference.
     */
    KodeClass klass;

    /**
     * This fields stores the actual data object, or in other words it defines
     * the value of the instance.
     */
    Object data = null;

    /**
     * This field helps to detect circular recursion, when necessary. A
     * {@literal true} value represents that the instance has already been
     * encountered, else not.
     */
    boolean reccured = false;

    /**
     * Map data structure to store the associated fields.
     */
    Map<String, KodeObject> fields = new HashMap<>();

    /**
     * Generates a new instance object associated with a class.
     *
     * @param klass The associated class.
     */
    KodeInstance(KodeClass klass) {
        this.klass = klass;
        if (klass != null) {
            fields.put(Kode.CLASS, klass);
        }
    }

    /**
     * Retrieves a field/method by using its name from the object of the class.
     *
     * @implNote It first scans for a field with the given name an then goes for
     * method, if no such field exits.
     *
     * @param name Name of the attribute as Token object.
     * @return Returns the associated field/method.
     */
    @Override
    public KodeObject get(Token name) {
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

    /**
     * Retrieves a field/method by using its name from the object of the class.
     *
     * @implNote It first scans for a field with the given name an then goes for
     * method, if no such field exits.
     *
     * @param name Name of the attribute as String object.
     * @return Returns the associated field/method.
     */
    @Override
    public KodeObject get(String name) {
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

    /**
     * Defines or assigns a field with a new value by using its name w.r.t., the
     * object of the class.
     *
     * @param name Name of the attribute as Token object.
     * @param value New Value for the field.
     */
    @Override
    public void set(Token name, KodeObject value) {
        set(name.lexeme, value);
    }

    /**
     * Defines or assigns a field with a new value by using its name w.r.t., the
     * object of the class.
     *
     * @param name Name of the attribute as String object.
     * @param value New Value for the field.
     */
    @Override
    public void set(String name, KodeObject value) {
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

    @Override
    public KodeObject call(KodeObject... args) {
        throw new RuntimeError("Not supported yet.");
    }

}
