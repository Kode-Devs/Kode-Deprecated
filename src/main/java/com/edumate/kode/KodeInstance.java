/*
 * MIT License
 *
 * Copyright (c) 2020 Edumate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.edumate.kode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
class KodeInstance {

    KodeClass klass;
    Double num;
    Boolean bool;
    String str;
    List list;
    boolean reccured = false; 
    Map<String, Object> fields = new HashMap<>();

    KodeInstance(KodeClass klass) {
        this.klass = klass;
        if (klass != null) {
            fields.put(Kode.CLASS, klass);
        }
        fields.put(Kode.HASH, new Hash(this));
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
                "Undefined property '" + name + "'.",
                null);
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
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
