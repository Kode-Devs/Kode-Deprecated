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

import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */


abstract class KodeBuiltinFunction extends KodeFunction {

    final String fun_name;
    
    KodeBuiltinFunction(String name,Environment closure,Interpreter inter) {
        super(null, closure,inter, false);
        this.fun_name = name;
    }
    
    @Override
    KodeFunction bind(KodeInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        this.closure = environment;
        return this;
    }
    
    @Override
    public String toString() {
        return "<builtin function '" + fun_name +"'>";
    }
    
    @Override
    public abstract List<Pair<String,Object>> arity();
    
    @Override
    public abstract Object call(Map<String, Object> arguments);
    
}
