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

import kni.KodeObject;
import math.KodeNumber;

/**
 * String DataType
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
class ValueString extends Value {

    static Value val = new ValueString(new Interpreter());

    static KodeInstance create(String x) {
        KodeInstance instance = new KodeInstance(val);
        instance.data = x;
        val.findMethod(Kode.INIT).bind(instance).call(instance);
        return instance;
    }

    private ValueString(Interpreter interpreter) {
        super("String", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 2, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                ((KodeInstance) This).data = ValueString.toStr(args[1]);
            }
            return This;
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="init subclass">
        this.methods.put(Kode.INIT_SUBCLASS, new KodeBuiltinFunction(Kode.INIT_SUBCLASS, interpreter, null, -3, args -> {
            KodeObject This = args[1];
            if (This instanceof KodeInstance) {
                ((KodeInstance) This).data = "";
            }
            return null;
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueString.isString((KodeInstance) This)) {
                    return This;
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="num">
        this.methods.put(Kode.NUMBER, new KodeBuiltinFunction(Kode.NUMBER, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueString.isString((KodeInstance) This)) {
                    try {
                        return Interpreter.toKodeValue(String2Num.toNumber(ValueString.toStr(This)));
                    } catch (Exception ex) {
                        throw new RuntimeError(ex.getMessage(), null);
                    }
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="bool">
        this.methods.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueString.isString((KodeInstance) This)) {
                    return Interpreter.toKodeValue(Interpreter.isTruthy(ValueString.toStr(This)));
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="list">
        this.methods.put(Kode.LIST, new KodeBuiltinFunction(Kode.LIST, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueString.isString((KodeInstance) This)) {
                    return Interpreter.toKodeValue(ValueString.toStr(This).toCharArray());
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="toStr">
    static String toStr(KodeObject x) {
        KodeObject a = x;
        for (; ; ) {
            if (x instanceof KodeInstance) {
                if (ValueString.isString((KodeInstance) x)) {
                    return (String) ((KodeInstance) x).data;
                } else {
                    try {
                        if (((KodeInstance) x).fields.containsKey(Kode.STRING)) {
                            Object get = ((KodeInstance) x).fields.get(Kode.STRING);
                            if (get instanceof KodeFunction) {
                                x = ((KodeFunction) get).bind((KodeInstance) x).call();
                                continue;
                            }
                        }
                        x = ((KodeInstance) x).klass.findMethod(Kode.STRING).bind((KodeInstance) x).call();
                        continue;
                    } catch (NotImplemented e) {
                        throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Printable in Nature", null);
                    }
                }
            }
            throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Printable in Nature", null);
        }
    }
//</editor-fold>

    static boolean isString(KodeInstance i) {
        return instanceOf(i.klass, ValueString.class);
    }

    //<editor-fold defaultstate="collapsed" desc="toNumber">
    private static class String2Num {

        public static KodeNumber toNumber(String num) throws Exception {
            try {
                switch (num) {
                    case Kode.INFINITY:
                    case "+" + Kode.INFINITY:
                        return KodeNumber.valueOf(Double.POSITIVE_INFINITY);
                    case "-" + Kode.INFINITY:
                        return KodeNumber.valueOf(-Double.POSITIVE_INFINITY);
                    case Kode.NAN:
                    case "+" + Kode.NAN:
                        return KodeNumber.valueOf(Double.NaN);
                    case "-" + Kode.NAN:
                        return KodeNumber.valueOf(-Double.NaN);
                }
                if (checkNumberFormat(num)) {
                    return KodeNumber.valueOf(num);
                } else {
                    throw new Exception("Number Format Error : " + num);
                }
            } catch (Exception e) {
                throw new Exception("Number Format Error : " + num);
            }
        }

        private static boolean checkNumberFormat(String num) {
            if (num.isEmpty()) {
                return false;
            }
            int i = 0;
            if (charAt(num, 0) == '+' || charAt(num, 0) == '-') {
                i++;
            }
            while (isDigit(charAt(num, i))) {
                i++;
            }

            // Look for a fractional part.
            if (charAt(num, i) == '.' && isDigit(charAt(num, i + 1))) {
                // Consume the "."
                i++;

                while (isDigit(charAt(num, i))) {
                    i++;
                }
            }

            // Look for an exponential part.
            if ((charAt(num, i) == 'e' || charAt(num, i) == 'E') && (isDigit(charAt(num, i + 1)) || charAt(num, i + 1) == '+' || charAt(num, i + 1) == '-')) {
                // Consume the "e"
                i++;
                if (charAt(num, i) == '+' || charAt(num, i) == '-') {
                    i++;
                }

                while (isDigit(charAt(num, i))) {
                    i++;
                }
            }

            return i >= num.length();
        }

        private static boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }

        private static char charAt(String s, int i) {
            if (i < s.length()) {
                return s.charAt(i);
            } else {
                return ' ';
            }
        }
    }
//</editor-fold>

}
