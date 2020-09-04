/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import math.KodeMath;

/**
 *
 * @author dell
 */
class KodeClass implements KodeCallable {

    String __doc__ = null;

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

    @Override
    public boolean isBind() {
        return true;
    }

    Map<String, KodeFunction> specialMethods() {
        Map<String, KodeFunction> sm = new HashMap<>();
        //<editor-fold defaultstate="collapsed" desc="init">
        sm.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 1, arg -> {
            return arg[0];
        }));
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="str">
        sm.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, arg -> {
            return "<object of '" + class_name + "'>"; //BUG
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="number">
        sm.put(Kode.NUMBER, new KodeBuiltinFunction(Kode.NUMBER, interpreter, null, 1, args -> {
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="list">
        sm.put(Kode.LIST, new KodeBuiltinFunction(Kode.LIST, interpreter, null, 1, args -> {
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="bool">
        sm.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, interpreter, null, 1, args -> {
            return interpreter.toKodeValue(Interpreter.isTruthy(true));
        }));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="neg">
        sm.put(Kode.NEG, new KodeBuiltinFunction(Kode.NEG, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) This)) {
                    This = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) This) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) This)) {
                    return interpreter.toKodeValue(KodeMath.neg(ValueNumber.toNumber(This)));
                }
            }
            throw new NotImplemented();
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="pos">
        sm.put(Kode.POS, new KodeBuiltinFunction(Kode.POS, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) This)) {
                    This = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) This) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) This)) {
                    return interpreter.toKodeValue(KodeMath.pos(ValueNumber.toNumber(This)));
                }
            }
            throw new NotImplemented();
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="getItem">
        sm.put(Kode.GET_ITEM, new KodeBuiltinFunction(Kode.GET_ITEM, interpreter, null, 2, args -> {
            Object This = args[0];
            Object index = args[1];
            if (This instanceof KodeInstance && index instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) index)) {
                    int toNumber;
                    try {
                        toNumber = ValueNumber.toNumber(index).getAsIndex();
                    } catch (RuntimeError ex) {
                        throw ex;
                    } catch (Exception ex) {
                        throw new RuntimeError("Index Out of Range or has fractional part.");
                    }
                    if (ValueList.isList((KodeInstance) This)) {
                        try {
                            return interpreter.toKodeValue(ValueList.toList(This).get(toNumber));
                        } catch (IndexOutOfBoundsException e) {
                            throw new RuntimeError("List Index Out Of Bound : " + Kode.stringify(toNumber), null);
                        }
                    }
                    if (ValueString.isString((KodeInstance) This)) {
                        try {
                            return interpreter.toKodeValue(ValueString.toStr(This).charAt(toNumber));
                        } catch (IndexOutOfBoundsException e) {
                            throw new RuntimeError("String Index Out Of Bound : " + Kode.stringify(toNumber), null);
                        }
                    }
                }
            }
            throw new NotImplemented();
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="setItem">
        sm.put(Kode.SET_ITEM, new KodeBuiltinFunction(Kode.SET_ITEM, interpreter, null, 3, args -> {
            Object This = args[0];
            Object index = args[1];
            if (This instanceof KodeInstance && index instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) index)) {
                    int toNumber;
                    try {
                        toNumber = ValueNumber.toNumber(index).getAsIndex();
                    } catch (RuntimeError ex) {
                        throw ex;
                    } catch (Exception ex) {
                        throw new RuntimeError("Index Out of Range or has fractional part.");
                    }
                    if (ValueList.isList((KodeInstance) This)) {
                        try {
                            ValueList.toList(This).set(toNumber, args[2]);
                            return null;
                        } catch (IndexOutOfBoundsException e) {
                            throw new RuntimeError("List Index Out Of Bound : " + Kode.stringify(toNumber));
                        }
                    }
                }
            }
            throw new NotImplemented();
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="len">
        sm.put(Kode.LEN, new KodeBuiltinFunction(Kode.LEN, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueString.isString((KodeInstance) This)) {
                    return interpreter.toKodeValue(ValueString.toStr(This).length());
                }
                if (ValueList.isList((KodeInstance) This)) {
                    return interpreter.toKodeValue(ValueList.toList(This).size());
                }
            }
            throw new NotImplemented();
        }));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="add">
        sm.put(Kode.ADD, new KodeBuiltinFunction(Kode.ADD, interpreter, null, 2, args -> {
            Object left = args[0];
            Object right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                    return interpreter.toKodeValue(ValueString.toStr(left).concat(ValueString.toStr(right)));
                } else if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                    List<Object> ll = new ArrayList<>();
                    ll.addAll(ValueList.toList(left));
                    ll.addAll(ValueList.toList(right));
                    return interpreter.toKodeValue(ll);
                } else {
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(KodeMath.add(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    }
                }
            }
            throw new NotImplemented();
        }));
        sm.put(Kode.RADD, new KodeBuiltinFunction(Kode.RADD, interpreter, null, 2, args -> {
            return sm.get(Kode.ADD).call(args[1], args[0]);
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="sub">
        sm.put(Kode.SUB, new KodeBuiltinFunction(Kode.SUB, interpreter, null, 2, args -> {
            Object left = args[0];
            Object right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    return interpreter.toKodeValue(KodeMath.substract(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                }
            }
            throw new NotImplemented();
        }));
        sm.put(Kode.RSUB, new KodeBuiltinFunction(Kode.RSUB, interpreter, null, 2, args -> {
            return sm.get(Kode.SUB).call(args[1], args[0]);
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="mul">
        sm.put(Kode.MUL, new KodeBuiltinFunction(Kode.MUL, interpreter, null, 2, args -> {
            Object left = args[0];
            Object right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    return interpreter.toKodeValue(KodeMath.multiply(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                }
                if (ValueString.isString((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    String str = "";
                    try {
                        for (int i = 0; i < ValueNumber.toNumber(right).getAsIndex(); i++) {
                            str = str.concat(ValueString.toStr(left));
                        }
                    } catch (Exception ex) {
                        throw new RuntimeError("TODO");
                    }
                    return interpreter.toKodeValue(str);
                }
                if (ValueString.isString((KodeInstance) right) && ValueNumber.isNumber((KodeInstance) left)) {
                    String str = "";
                    try {
                        for (int i = 0; i < ValueNumber.toNumber(left).getAsIndex(); i++) {
                            str = str.concat(ValueString.toStr(right));
                        }
                    } catch (Exception ex) {
                        throw new RuntimeError("TODO");
                    }
                    return interpreter.toKodeValue(str);
                }
                if (ValueList.isList((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    List<Object> ll = new ArrayList<>();
                    try {
                        for (int i = 0; i < ValueNumber.toNumber(right).getAsIndex(); i++) {
                            ll.addAll(ValueList.toList(left));
                        }
                    } catch (Exception ex) {
                        throw new RuntimeError("TODO");
                    }
                    return interpreter.toKodeValue(ll);
                }
                if (ValueList.isList((KodeInstance) right) && ValueNumber.isNumber((KodeInstance) left)) {
                    List<Object> ll = new ArrayList<>();
                    try {
                        for (int i = 0; i < ValueNumber.toNumber(left).getAsIndex(); i++) {
                            ll.addAll(ValueList.toList(right));
                        }
                    } catch (Exception ex) {
                        throw new RuntimeError("TODO");
                    }
                    return interpreter.toKodeValue(ll);
                }
            }
            throw new NotImplemented();
        }));
        sm.put(Kode.RMUL, new KodeBuiltinFunction(Kode.RMUL, interpreter, null, 2, args -> {
            return sm.get(Kode.MUL).call(args[1], args[0]);
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="true_div">
        sm.put(Kode.TRUE_DIV, new KodeBuiltinFunction(Kode.TRUE_DIV, interpreter, null, 2, args -> {
            Object left = args[0];
            Object right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    try {
                        return interpreter.toKodeValue(KodeMath.divide(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    } catch (ArithmeticException ex) {
                        throw new RuntimeError(ex.getMessage());
                    }
                }
            }
            throw new NotImplemented();
        }));
        sm.put(Kode.RTRUE_DIV, new KodeBuiltinFunction(Kode.RTRUE_DIV, interpreter, null, 2, args -> {
            return sm.get(Kode.TRUE_DIV).call(args[1], args[0]);
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="floor_div">
        sm.put(Kode.FLOOR_DIV, new KodeBuiltinFunction(Kode.FLOOR_DIV, interpreter, null, 2, args -> {
            Object left = args[0];
            Object right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    try {
                        return interpreter.toKodeValue(KodeMath.floor_div(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    } catch (ArithmeticException ex) {
                        throw new RuntimeError(ex.getMessage());
                    }
                }
            }
            throw new NotImplemented();
        }));
        sm.put(Kode.RFLOOR_DIV, new KodeBuiltinFunction(Kode.RFLOOR_DIV, interpreter, null, 2, args -> {
            return sm.get(Kode.FLOOR_DIV).call(args[1], args[0]);
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="mod">
        sm.put(Kode.MOD, new KodeBuiltinFunction(Kode.MOD, interpreter, null, 2, args -> {
            Object left = args[0];
            Object right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    try {
                        return interpreter.toKodeValue(KodeMath.modulo(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    } catch (ArithmeticException ex) {
                        throw new RuntimeError(ex.getMessage());
                    }
                }
            }
            throw new NotImplemented();
        }));
        sm.put(Kode.RMOD, new KodeBuiltinFunction(Kode.RMOD, interpreter, null, 2, args -> {
            return sm.get(Kode.MOD).call(args[1], args[0]);
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="pow">
        sm.put(Kode.POWER, new KodeBuiltinFunction(Kode.POWER, interpreter, null, 2, args -> {
            Object left = args[0];
            Object right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    try {
                        return interpreter.toKodeValue(KodeMath.exponent(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    } catch (ArithmeticException ex) {
                        throw new RuntimeError("TODO");
                    }
                }
            }
            throw new NotImplemented();
        }));
        sm.put(Kode.RPOWER, new KodeBuiltinFunction(Kode.RPOWER, interpreter, null, 2, args -> {
            return sm.get(Kode.POWER).call(args[1], args[0]);
        }));
        //</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="lshift">
//        sm.put(Kode.LSHIFT, new KodeBuiltinFunction(Kode.LSHIFT, interpreter) {
//
//            @Override
//            public int arity() {
//                return 1;
//            }
//
//            @Override
//            public Object call(Object... arguments) {
//                Object left = closure.getAt(0, "this");
//                Object right = arguments[0];
//                if (left instanceof KodeInstance && right instanceof KodeInstance) {
//                    if (ValueBool.isBool((KodeInstance) left)) {
//                        left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
//                    }
//                    if (ValueBool.isBool((KodeInstance) right)) {
//                        right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
//                    }
//                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
//                        try {
//                            return interpreter.toKodeValue(KodeMath.lshift(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
//                        } catch (ArithmeticException ex) {
//                            throw new RuntimeError("TODO");
//                        }
//                    }
//                }
//                throw new NotImplemented();
//            }
//        });
//        sm.put(Kode.RLSHIFT, new KodeBuiltinFunction(Kode.RLSHIFT, interpreter) {
//
//            @Override
//            public int arity() {
//                return 1;
//            }
//
//            @Override
//            public Object call(Object... arguments) {
//                Object right = closure.getAt(0, "this");
//                Object left = arguments[0];
//                if (left instanceof KodeInstance && right instanceof KodeInstance) {
//                    if (ValueBool.isBool((KodeInstance) left)) {
//                        left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
//                    }
//                    if (ValueBool.isBool((KodeInstance) right)) {
//                        right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
//                    }
//                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
//                        try {
//                            return interpreter.toKodeValue(KodeMath.lshift(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
//                        } catch (ArithmeticException ex) {
//                            throw new RuntimeError("TODO");
//                        }
//                    }
//                }
//                throw new NotImplemented();
//            }
//        });
//        //</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="rshift">
//        sm.put(Kode.RSHIFT, new KodeBuiltinFunction(Kode.RSHIFT, interpreter) {
//
//            @Override
//            public int arity() {
//                return 1;
//            }
//
//            @Override
//            public Object call(Object... arguments) {
//                Object left = closure.getAt(0, "this");
//                Object right = arguments[0];
//                if (left instanceof KodeInstance && right instanceof KodeInstance) {
//                    if (ValueBool.isBool((KodeInstance) left)) {
//                        left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
//                    }
//                    if (ValueBool.isBool((KodeInstance) right)) {
//                        right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
//                    }
//                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
//                        try {
//                            return interpreter.toKodeValue(KodeMath.rshift(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
//                        } catch (ArithmeticException ex) {
//                            throw new RuntimeError("TODO");
//                        }
//                    }
//                }
//                throw new NotImplemented();
//            }
//        });
//        sm.put(Kode.RRSHIFT, new KodeBuiltinFunction(Kode.RRSHIFT, interpreter) {
//
//            @Override
//            public int arity() {
//                return 1;
//            }
//
//            @Override
//            public Object call(Object... arguments) {
//                Object right = closure.getAt(0, "this");
//                Object left = arguments[0];
//                if (left instanceof KodeInstance && right instanceof KodeInstance) {
//                    if (ValueBool.isBool((KodeInstance) left)) {
//                        left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
//                    }
//                    if (ValueBool.isBool((KodeInstance) right)) {
//                        right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
//                    }
//                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
//                        try {
//                            return interpreter.toKodeValue(KodeMath.rshift(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
//                        } catch (ArithmeticException ex) {
//                            throw new RuntimeError("TODO");
//                        }
//                    }
//                }
//                throw new NotImplemented();
//            }
//        });
//        //</editor-fold>
//
//        //<editor-fold defaultstate="collapsed" desc="eq">
//        sm.put(Kode.EQ, new KodeBuiltinFunction(Kode.EQ, interpreter) {
//
//            @Override
//            public int arity() {
//                return 1;
//            }
//
//            @Override
//            public Object call(Object... arguments) {
//                return interpreter.toKodeValue(eq(closure.getAt(0, "this"), arguments[0]));
//            }
//        });
//        //</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="ne">
//        sm.put(Kode.NE, new KodeBuiltinFunction(Kode.NE, interpreter) {
//
//            @Override
//            public int arity() {
//                return 1;
//            }
//
//            @Override
//            public Object call(Object... arguments) {
//                return interpreter.toKodeValue(ne(closure.getAt(0, "this"), arguments[0]));
//            }
//        });
//        //</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="lt">
//        sm.put(Kode.LT, new KodeBuiltinFunction(Kode.LT, interpreter) {
//
//            @Override
//            public int arity() {
//                return 1;
//            }
//
//            @Override
//            public Object call(Object... arguments) {
//                return interpreter.toKodeValue(lt(closure.getAt(0, "this"), arguments[0]));
//            }
//        });
//        //</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="le">
//        sm.put(Kode.LE, new KodeBuiltinFunction(Kode.LE, interpreter) {
//
//            @Override
//            public int arity() {
//                return 1;
//            }
//
//            @Override
//            public Object call(Object... arguments) {
//                return interpreter.toKodeValue(le(closure.getAt(0, "this"), arguments[0]));
//            }
//        });
//        //</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="gt">
//        sm.put(Kode.GT, new KodeBuiltinFunction(Kode.GT, interpreter) {
//
//            @Override
//            public int arity() {
//                return 1;
//            }
//
//            @Override
//            public Object call(Object... arguments) {
//                return interpreter.toKodeValue(gt(closure.getAt(0, "this"), arguments[0]));
//            }
//        });
//        //</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="ge">
//        sm.put(Kode.GE, new KodeBuiltinFunction(Kode.GE, interpreter) {
//
//            @Override
//            public int arity() {
//                return 1;
//            }
//
//            @Override
//            public Object call(Object... arguments) {
//                return interpreter.toKodeValue(ge(closure.getAt(0, "this"), arguments[0]));
//            }
//        });
//        //</editor-fold>
        return sm;
    }

    KodeFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
        }

        Map<String, KodeFunction> specialMethods = specialMethods();
        if (specialMethods.containsKey(name)) {
            return specialMethods.get(name);
        }

        return null;
    }

    @Override
    public String toString() {
        return "<class '" + class_name + "'>";
    }

    @Override
    public Object call(Object... arguments) {
        KodeInstance instance = new KodeInstance(this);
        this.findMethod(Kode.INIT).bind(instance).call(arguments);
        return instance;
    }

    @Override
    public int arity() {
        KodeFunction initializer = findMethod(Kode.INIT);
        if (initializer == null) {
            return 0;
        }
        return initializer.arity();
    }

}
