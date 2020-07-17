/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import math.KodeNumber;

/**
 *
 * @author dell
 */
class ValueString extends Value {

    static Value val = new ValueString(new Interpreter());

    static KodeInstance create(String x) {
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call(new Object[]{x});
        return instance;
    }

    private ValueString(Interpreter interpreter) {
        super("String", interpreter);
//        //<editor-fold defaultstate="collapsed" desc="init">
//        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, null, interpreter) {
//
//            @Override
//            public List<Pair<String, Object>> arity() {
//                return Arrays.asList(new Pair("x", false));
//            }
//
//            @Override
//            public Object call(Map<String, Object> arguments) {
//                Object This = closure.getAt(0, "this");
//                if (This instanceof KodeInstance) {
//                    ((KodeInstance) This).data = ValueString.toStr(arguments.get("x"));
//                }
//                return This;
//            }
//        });
////</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="str">
//        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, null, interpreter) {
//
//            @Override
//            public List<Pair<String, Object>> arity() {
//                return new ArrayList();
//            }
//
//            @Override
//            public Object call(Map<String, Object> arguments) {
//                Object This = closure.getAt(0, "this");
//                if (This instanceof KodeInstance) {
//                    return This;
//                }
//                throw new NotImplemented();
//            }
//        });
////</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="num">
//        this.methods.put(Kode.NUMBER, new KodeBuiltinFunction(Kode.NUMBER, null, interpreter) {
//
//            @Override
//            public List<Pair<String, Object>> arity() {
//                return new ArrayList();
//            }
//
//            @Override
//            public Object call(Map<String, Object> arguments) {
//                Object This = closure.getAt(0, "this");
//                if (This instanceof KodeInstance) {
//                    try {
//                        return interpreter.toKodeValue(toNumber.toNumber(((KodeInstance) This).data));
//                    } catch (Exception ex) {
//                        throw new RuntimeError(ex.getMessage(), null);
//                    }
//                }
//                throw new NotImplemented();
//            }
//        });
////</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="bool">
//        this.methods.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, null, interpreter) {
//
//            @Override
//            public List<Pair<String, Object>> arity() {
//                return new ArrayList();
//            }
//
//            @Override
//            public Object call(Map<String, Object> arguments) {
//                Object This = closure.getAt(0, "this");
//                if (This instanceof KodeInstance) {
//                    return interpreter.toKodeValue(interpreter.isTruthy(((KodeInstance) This).data));
//                }
//                throw new NotImplemented();
//            }
//        });
////</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="list">
//        this.methods.put(Kode.LIST, new KodeBuiltinFunction(Kode.LIST, null, interpreter) {
//
//            @Override
//            public List<Pair<String, Object>> arity() {
//                return new ArrayList();
//            }
//
//            @Override
//            public Object call(Map<String, Object> arguments) {
//                Object This = closure.getAt(0, "this");
//                if (This instanceof KodeInstance) {
//                    try {
//                        List ll = new ArrayList();
//                        for (char ch : ((String) ((KodeInstance) This).data).toCharArray()) {
//                            ll.add(interpreter.toKodeValue("" + ch));
//                        }
//                        return interpreter.toKodeValue(ll);
//                    } catch (Exception ex) {
//                        throw new RuntimeError(ex.getMessage(), null);
//                    }
//                }
//                throw new NotImplemented();
//            }
//        });
////</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="concat">
//        this.methods.put("concat", new KodeBuiltinFunction("concat", null, interpreter) {
//
//            @Override
//            public List<Pair<String, Object>> arity() {
//                return Arrays.asList(new Pair("obj", null));
//            }
//
//            @Override
//            public Object call(Map<String, Object> arguments) {
//                Object This = closure.getAt(0, "this");
//                Object obj = arguments.get("obj");
//                if (This instanceof KodeInstance && obj instanceof KodeInstance) {
//                    if (ValueString.isString((KodeInstance) This) && ValueString.isString((KodeInstance) obj)) {
//                        return interpreter.toKodeValue(ValueString.toStr(This).concat(ValueString.toStr(obj)));
//                    }
//                }
//                throw new NotImplemented();
//            }
//        });
////</editor-fold>

    }

    static String toStr(Object x_) {
        return ValueString.toStr(x_, x_);
    }

    //<editor-fold defaultstate="collapsed" desc="toStr">
    private static String toStr(Object x_, Object a) {
        if (x_ instanceof String) {
            return (String) x_;
        } else if (x_ instanceof KodeInstance) {
            if (((KodeInstance) x_).klass instanceof ValueString) {
                return (String) ((KodeInstance) x_).data;
            } else {
                try {
                    if (((KodeInstance) x_).fields.containsKey(Kode.STRING)) {
                        Object get = ((KodeInstance) x_).fields.get(Kode.STRING);
                        if (get instanceof KodeFunction) {
                            return toStr(((KodeFunction) get).bind((KodeInstance) x_).call(new Object[0]), a);
                        }
                    }
                    return toStr(((KodeInstance) x_).klass.findMethod(Kode.STRING).bind((KodeInstance) x_).call(new Object[0]), a);
                } catch (NotImplemented e) {
                    throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Printable in Nature", null);
                }
            }
        } else {
            throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Printable in Nature", null);
        }
    }
//</editor-fold>

    final static boolean isString(KodeInstance i) {
        return instanceOf(i.klass, ValueString.class);
    }

    //<editor-fold defaultstate="collapsed" desc="toNumber">
    private static class toNumber {

        public static KodeNumber toNumber(Object num) throws Exception {
            try {
                switch (num.toString()) {
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
                if (checkNumberFormat(num.toString())) {
                    return KodeNumber.valueOf(num.toString());
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

            // Look for a exponential part.
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
