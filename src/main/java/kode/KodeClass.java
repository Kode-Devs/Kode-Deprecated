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
import java.util.logging.Level;
import java.util.logging.Logger;
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

    Map<String, KodeFunction> specialMethods() {
        Map<String, KodeFunction> sm = new HashMap();
        //<editor-fold defaultstate="collapsed" desc="init">
        sm.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, null, interpreter) {
            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                return closure.getAt(0, "this");
            }
        });
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="str">
        sm.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, null, interpreter) {
            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                return "<object of '" + class_name + "'>";
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="number">
        sm.put(Kode.NUMBER, new KodeBuiltinFunction(Kode.NUMBER, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="list">
        sm.put(Kode.LIST, new KodeBuiltinFunction(Kode.LIST, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="bool">
        sm.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    Object o;
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        o = ValueNumber.toNumber(This);
                    } else if (ValueNone.isNone((KodeInstance) This)) {
                        o = null;
                    } else if (ValueBool.isBool((KodeInstance) This)) {
                        o = ValueBool.toBoolean(This);
                    } else if (ValueString.isString((KodeInstance) This)) {
                        o = ValueString.toStr(This);
                    } else if (ValueList.isList((KodeInstance) This)) {
                        o = ValueList.toList(This);
                    } else {
                        o = true;
                    }
                    return interpreter.toKodeValue(interpreter.isTruthy(o));
                }
                return interpreter.toKodeValue(interpreter.isTruthy(true));
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="neg">
        sm.put(Kode.NEG, new KodeBuiltinFunction(Kode.NEG, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueBool.isBool((KodeInstance) This)) {
                        This = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) This) ? 1 : 0);
                    }
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        return interpreter.toKodeValue(KodeMath.neg(ValueNumber.toNumber(This)));
                    }
                }
                throw new NotImplemented();
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="pos">
        sm.put(Kode.POS, new KodeBuiltinFunction(Kode.POS, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueBool.isBool((KodeInstance) This)) {
                        This = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) This) ? 1 : 0);
                    }
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        return interpreter.toKodeValue(KodeMath.pos(ValueNumber.toNumber(This)));
                    }
                }
                throw new NotImplemented();
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="getItem">
        sm.put(Kode.GET_ITEM, new KodeBuiltinFunction(Kode.GET_ITEM, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("idx", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                Object index = arguments.get("idx");
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
                    } else if (ValueList.isList((KodeInstance) index)) {

                    }
                }
                throw new NotImplemented();
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="setItem">
        sm.put(Kode.SET_ITEM, new KodeBuiltinFunction(Kode.SET_ITEM, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("idx", null), new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                Object index = arguments.get("idx");
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
                                ValueList.toList(This).set(toNumber, arguments.get("obj"));
                                return null;
                            } catch (IndexOutOfBoundsException e) {
                                throw new RuntimeError("List Index Out Of Bound : " + Kode.stringify(toNumber), null);
                            }
                        }
                    }
                }
                throw new NotImplemented();
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="len">
        sm.put(Kode.LEN, new KodeBuiltinFunction(Kode.LEN, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueString.isString((KodeInstance) This)) {
                        return interpreter.toKodeValue(ValueString.toStr(This).length());
                    }
                    if (ValueList.isList((KodeInstance) This)) {
                        return interpreter.toKodeValue(ValueList.toList(This).size());
                    }
                }
                throw new NotImplemented();
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="add">
        sm.put(Kode.ADD, new KodeBuiltinFunction(Kode.ADD, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueString.toStr(left).concat(ValueString.toStr(right)));
                    } else if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                        List ll = new ArrayList();
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
            }
        });
        sm.put(Kode.RADD, new KodeBuiltinFunction(Kode.RADD, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object right = closure.getAt(0, "this");
                Object left = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueString.toStr(left).concat(ValueString.toStr(right)));
                    } else if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                        List ll = new ArrayList();
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
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="sub">
        sm.put(Kode.SUB, new KodeBuiltinFunction(Kode.SUB, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
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
            }
        });
        sm.put(Kode.RSUB, new KodeBuiltinFunction(Kode.RSUB, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object right = closure.getAt(0, "this");
                Object left = arguments.get("obj");
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
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="mul">
        sm.put(Kode.MUL, new KodeBuiltinFunction(Kode.MUL, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
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
                        List ll = new ArrayList();
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
                        List ll = new ArrayList();
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
            }
        });
        sm.put(Kode.RMUL, new KodeBuiltinFunction(Kode.RMUL, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object right = closure.getAt(0, "this");
                Object left = arguments.get("obj");
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
                        List ll = new ArrayList();
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
                        List ll = new ArrayList();
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
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="true_div">
        sm.put(Kode.TRUE_DIV, new KodeBuiltinFunction(Kode.TRUE_DIV, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(KodeMath.divide(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    }
                }
                throw new NotImplemented();
            }
        });
        sm.put(Kode.RTRUE_DIV, new KodeBuiltinFunction(Kode.RTRUE_DIV, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object right = closure.getAt(0, "this");
                Object left = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(KodeMath.divide(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    }
                }
                throw new NotImplemented();
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="floor_div">
        sm.put(Kode.FLOOR_DIV, new KodeBuiltinFunction(Kode.FLOOR_DIV, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(KodeMath.floor_div(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    }
                }
                throw new NotImplemented();
            }
        });
        sm.put(Kode.RFLOOR_DIV, new KodeBuiltinFunction(Kode.RFLOOR_DIV, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object right = closure.getAt(0, "this");
                Object left = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(KodeMath.floor_div(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    }
                }
                throw new NotImplemented();
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="mod">
        sm.put(Kode.MOD, new KodeBuiltinFunction(Kode.MOD, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(KodeMath.modulo(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    }
                }
                throw new NotImplemented();
            }
        });
        sm.put(Kode.RMOD, new KodeBuiltinFunction(Kode.RMOD, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object right = closure.getAt(0, "this");
                Object left = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(KodeMath.modulo(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    }
                }
                throw new NotImplemented();
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="pow">
        sm.put(Kode.POWER, new KodeBuiltinFunction(Kode.POWER, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
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
                        } catch (Exception ex) {
                            throw new RuntimeError("TODO");
                        }
                    }
                }
                throw new NotImplemented();
            }
        });
        sm.put(Kode.RPOWER, new KodeBuiltinFunction(Kode.RPOWER, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object right = closure.getAt(0, "this");
                Object left = arguments.get("obj");
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
                        } catch (Exception ex) {
                            throw new RuntimeError("TODO");
                        }
                    }
                }
                throw new NotImplemented();
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="eq">
        sm.put(Kode.EQ, new KodeBuiltinFunction(Kode.EQ, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                return interpreter.toKodeValue(eq(left, right));
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="ne">
        sm.put(Kode.NE, new KodeBuiltinFunction(Kode.NE, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                return interpreter.toKodeValue(ne(left, right));
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="lt">
        sm.put(Kode.LT, new KodeBuiltinFunction(Kode.LT, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                return interpreter.toKodeValue(lt(left, right));
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="le">
        sm.put(Kode.LE, new KodeBuiltinFunction(Kode.LE, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                return interpreter.toKodeValue(le(left, right));
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="gt">
        sm.put(Kode.GT, new KodeBuiltinFunction(Kode.GT, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                return interpreter.toKodeValue(gt(left, right));
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="ge">
        sm.put(Kode.GE, new KodeBuiltinFunction(Kode.GE, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                return interpreter.toKodeValue(ge(left, right));
            }
        });
        //</editor-fold>
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
