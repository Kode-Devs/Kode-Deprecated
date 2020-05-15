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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
class KodeClass implements KodeCallable {

    final String class_name;
    final KodeClass superclass;
    Map<String, KodeFunction> methods;
    final Interpreter interpreter;

    KodeClass(String name, KodeClass superclass, Map<String, KodeFunction> methods, Interpreter interpreter) {
        this.superclass = superclass;
        this.class_name = name;
        this.methods = methods;
        this.interpreter = interpreter;
    }

    KodeFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
        }

        switch (name) {
            case Kode.INIT:
                return new KodeBuiltinFunction(name, null, interpreter) {
                    @Override
                    public List<Pair<String, Object>> arity() {
                        return new ArrayList();
                    }

                    @Override
                    public Object call(Map<String, Object> arguments) {
                        return closure.getAt(0, "this");
                    }
                };
            case Kode.STRING:
                return new KodeBuiltinFunction(name, null, interpreter) {
                    @Override
                    public List<Pair<String, Object>> arity() {
                        return new ArrayList();
                    }

                    @Override
                    public Object call(Map<String, Object> arguments) {
                        return "<object of '" + class_name + "'>";
                    }
                };

            case Kode.NUMBER:
            case Kode.LIST:
            case Kode.NEG:
                return new KodeBuiltinFunction(name, null, interpreter) {
                    @Override
                    public List<Pair<String, Object>> arity() {
                        return new ArrayList();
                    }

                    @Override
                    public Object call(Map<String, Object> arguments) {
                        throw new NotImplemented();
                    }
                };

            case Kode.BOOLEAN:
                return new KodeBuiltinFunction(name, null, interpreter) {

                    @Override
                    public List<Pair<String, Object>> arity() {
                        return new ArrayList();
                    }

                    @Override
                    public Object call(Map<String, Object> arguments) {
                        Object This = closure.getAt(0, "this");
                        if (This instanceof KodeInstance) {
                            Object o = true;
                            if (ValueNumber.isNumber((KodeInstance) This)) {
                                o = ValueNumber.toNumber(This);
                            }
                            if (ValueNone.isNone((KodeInstance) This)) {
                                o = null;
                            }
                            if (ValueBool.isBool((KodeInstance) This)) {
                                o = ValueBool.toBoolean(This);
                            }
                            if (ValueString.isString((KodeInstance) This)) {
                                o = ValueString.toStr(This);
                            }
                            if (ValueList.isList((KodeInstance) This)) {
                                o = ValueList.toList(This);
                            }
                            return interpreter.toKodeValue(interpreter.isTruthy(o));
                        }
                        return interpreter.toKodeValue(interpreter.isTruthy(true));
                    }
                };

            case Kode.GET_AT_INDEX:
                return new KodeBuiltinFunction(name, null, interpreter) {
                    @Override
                    public List<Pair<String, Object>> arity() {
                        return Arrays.asList(new Pair("idx", null));
                    }

                    @Override
                    public Object call(Map<String, Object> arguments) {
                        throw new NotImplemented();
                    }
                };

            case Kode.ADD:
            case Kode.RADD:
            case Kode.SUB:
            case Kode.RSUB:
            case Kode.MUL:
            case Kode.RMUL:
            case Kode.DIV:
            case Kode.RDIV:
            case Kode.MOD:
            case Kode.RMOD:
            case Kode.FLOOR_DIV:
            case Kode.RFLOOR_DIV:
                return new KodeBuiltinFunction(name, null, interpreter) {

                    @Override
                    public List<Pair<String, Object>> arity() {
                        return Arrays.asList(new Pair("obj", null));
                    }

                    @Override
                    public Object call(Map<String, Object> arguments) {
                        throw new NotImplemented();
                    }
                };

        }

        return null;
    }

    @Override
    public String toString() {
        return "<class '" + class_name + "'>";
    }

    @Override
    public Object call(Map<String, Object> arguments) {
        KodeInstance instance = new KodeInstance(this);
        KodeFunction initializer = findMethod(Kode.INIT);
        initializer.bind(instance).call(arguments);
        return instance;
    }

    @Override
    public List<Pair<String, Object>> arity() {
        KodeFunction initializer = findMethod(Kode.INIT);
        if (initializer == null) {
            return new ArrayList();
        }
        return initializer.arity();
    }

}
