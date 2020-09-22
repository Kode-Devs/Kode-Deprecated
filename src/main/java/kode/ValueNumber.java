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

import math.KodeNumber;

/**
 *
 * @author dell
 */
class ValueNumber extends Value {

    static Value val = new ValueNumber(new Interpreter());

    static KodeInstance create(KodeNumber x) {
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call(x);
        return instance;
    }

    private ValueNumber(Interpreter interpreter) {
        super("Number", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 2, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                ((KodeInstance) This).data = ValueNumber.toNumber(args[1]);
            }
            return This;
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="init subclass">
        this.methods.put(Kode.INIT_SUBCLASS, new KodeBuiltinFunction(Kode.INIT_SUBCLASS, interpreter, null, -3, args -> {
            Object This = args[1];
            if (This instanceof KodeInstance) {
                ((KodeInstance) This).data = KodeNumber.valueOf("0");
            }
            return null;
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) This)) {
                    return interpreter.toKodeValue(Kode.stringify(ValueNumber.toNumber(This)));
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="num">
        this.methods.put(Kode.NUMBER, new KodeBuiltinFunction(Kode.NUMBER, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) This)) {
                    return This;
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="bool">
        this.methods.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) This)) {
                    return interpreter.toKodeValue(Interpreter.isTruthy(ValueNumber.toNumber(This)));
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="isInteger">
        this.methods.put("isInt", new KodeBuiltinFunction("isInt", interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) This)) {
                    return interpreter.toKodeValue(ValueNumber.toNumber(This).isInteger());
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="asInt">
        this.methods.put("asInt", new KodeBuiltinFunction("asInt", interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) This)) {
                    try {
                        return interpreter.toKodeValue(ValueNumber.toNumber(This).getInteger());
                    } catch (ArithmeticException e) {
                        throw new RuntimeError("Has fractional part.");
                    }
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="asReal">
        this.methods.put("asReal", new KodeBuiltinFunction("asReal", interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) This)) {
                    return interpreter.toKodeValue(ValueNumber.toNumber(This).getFloat());
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="asIndex">
        this.methods.put("asIndex", new KodeBuiltinFunction("asIndex", interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) This)) {
                    try {
                        return interpreter.toKodeValue(ValueNumber.toNumber(This).getAsIndex());
                    } catch (ArithmeticException ex) {
                        throw new RuntimeError("Its value falls beyond range of Indexing.");
                    }
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="toNumber">
    static KodeNumber toNumber(Object x) {
        Object a = x;
        for (;;) {
            if (x instanceof KodeNumber) {
                return (KodeNumber) x;
            } else if (x instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) x)) {
                    return (KodeNumber) ((KodeInstance) x).data;
                } else {
                    try {
                        if (((KodeInstance) x).fields.containsKey(Kode.NUMBER)) {
                            Object get = ((KodeInstance) x).fields.get(Kode.NUMBER);
                            if (get instanceof KodeFunction) {
                                x = ((KodeFunction) get).bind((KodeInstance) x).call();
                                continue;
                            }
                        }
                        x = ((KodeInstance) x).klass.findMethod(Kode.NUMBER).bind((KodeInstance) x).call();
                        continue;
                    } catch (NotImplemented e) {
                        throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Numeric in Nature", null);
                    }
                }
            }
            throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Numeric in Nature", null);
        }
    }
//</editor-fold>

    final static boolean isNumber(KodeInstance i) {
        return instanceOf(i.klass, ValueNumber.class);
    }

}
