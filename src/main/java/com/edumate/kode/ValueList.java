package com.edumate.kode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
class ValueList extends Value {

    static KodeInstance create(List x, Interpreter interpreter) {
        Value val = new ValueList(interpreter);
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call(Arrays.asList(x));
        return instance;
    }

    ValueList(Interpreter interpreter) {
        super("List", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("x", false));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    ((KodeInstance) This).list = ValueList.toList(arguments.get("x"));
                }
                return This;
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    Object i;
                    try {
                        if (!((KodeInstance) This).reccured) {
                            ((KodeInstance) This).reccured = true;
                            i = ValueString.create(Kode.stringify(((KodeInstance) This).list), interpreter);
                        } else {
                            i = ValueString.create(Kode.stringify("[...]"), interpreter);
                        }
                        ((KodeInstance) This).reccured = false;
                        return i;
                    } catch (Error | Exception e) {
                        ((KodeInstance) This).reccured = false;
                        throw e;
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="index">
        this.methods.put(Kode.GET_AT_INDEX, new KodeBuiltinFunction(Kode.GET_AT_INDEX, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("idx", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    Double toNumber;
                    try {
                        toNumber = ValueNumber.toNumber(arguments.get("idx"));
                    } catch (RuntimeError er) {
                        List ll;
                        try {
                            ll = ValueList.toList(arguments.get("idx"));
                        } catch (RuntimeError err) {
                            throw new RuntimeError("List Index must be Integer or List of Integer in Nature found " + Kode.stringify(arguments.get("idx")), null);
                        }
                        List li = new ArrayList();
                        for (Object l : ll) {
                            try {
                                toNumber = ValueNumber.toNumber(l);
                            } catch (RuntimeError err) {
                                throw new RuntimeError("List Index must be Integer or List of Integer in Nature found " + Kode.stringify(l), null);
                            }
                            if (toNumber.intValue() != toNumber) {
                                throw new RuntimeError("List Indices must be Integer in Nature found " + Kode.stringify(toNumber), null);
                            }
                            try {
                                Object temp = ((KodeInstance) This).list.get(toNumber.intValue());
                                li.add(interpreter.toKodeValue(temp));
                            } catch (IndexOutOfBoundsException e) {
                                throw new RuntimeError("List Index Out Of Bound : " + Kode.stringify(toNumber), null);
                            }
                        }
                        return interpreter.toKodeValue(li);
                    }
                    if (toNumber.intValue() != toNumber) {
                        throw new RuntimeError("List Indices must be Integer in Nature found " + Kode.stringify(toNumber), null);
                    }
                    Object temp;
                    try {
                        temp = ((KodeInstance) This).list.get(toNumber.intValue());
                    } catch (IndexOutOfBoundsException e) {
                        throw new RuntimeError("List Index Out Of Bound : " + Kode.stringify(toNumber), null);
                    }
                    return interpreter.toKodeValue(temp);
                }
                throw new NotImplemented();
            }
        });
        this.methods.put(Kode.SET_AT_INDEX, new KodeBuiltinFunction(Kode.SET_AT_INDEX, null, interpreter) {

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
                        Double toNumber = ValueNumber.toNumber(index);
                        if (toNumber.intValue() != toNumber) {
                            throw new RuntimeError("List Indices must be Integer in Nature found " + Kode.stringify(toNumber), null);
                        }
                        try {
                            return ((KodeInstance) This).list.get(toNumber.intValue());
                        } catch (IndexOutOfBoundsException e) {
                            throw new RuntimeError("List Index Out Of Bound : " + Kode.stringify(toNumber), null);
                        }
                    }else if(ValueList.isList((KodeInstance) index)){
                        
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Add">
        this.methods.put(Kode.ADD, new KodeBuiltinFunction(Kode.ADD, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                        List ll = new ArrayList();
                        ll.addAll(ValueList.toList(left));
                        ll.addAll(ValueList.toList(right));
                        return interpreter.toKodeValue(ll);
                    }
                }
                throw new NotImplemented();
            }
        });
        this.methods.put(Kode.RADD, new KodeBuiltinFunction(Kode.RADD, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object right = closure.getAt(0, "this");
                Object left = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                        List ll = new ArrayList();
                        ll.addAll(ValueList.toList(left));
                        ll.addAll(ValueList.toList(right));
                        return interpreter.toKodeValue(ll);
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Mul">
        this.methods.put(Kode.MUL, new KodeBuiltinFunction(Kode.MUL, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object left = closure.getAt(0, "this");
                Object right = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueList.isList((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        List ll = new ArrayList();
                        for (int i = 0; i < ValueNumber.toNumber(right); i++) {
                            ll.addAll(ValueList.toList(left));
                        }
                        return interpreter.toKodeValue(ll);
                    }
                    if (ValueList.isList((KodeInstance) right) && ValueNumber.isNumber((KodeInstance) left)) {
                        List ll = new ArrayList();
                        for (int i = 0; i < ValueNumber.toNumber(left); i++) {
                            ll.addAll(ValueList.toList(right));
                        }
                        return interpreter.toKodeValue(ll);
                    }
                }
                throw new NotImplemented();
            }
        });
        this.methods.put(Kode.RMUL, new KodeBuiltinFunction(Kode.RMUL, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object right = closure.getAt(0, "this");
                Object left = arguments.get("obj");
                if (left instanceof KodeInstance && right instanceof KodeInstance) {
                    if (ValueList.isList((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        List ll = new ArrayList();
                        for (int i = 0; i < ValueNumber.toNumber(right); i++) {
                            ll.addAll(ValueList.toList(left));
                        }
                        return interpreter.toKodeValue(ll);
                    }
                    if (ValueList.isList((KodeInstance) right) && ValueNumber.isNumber((KodeInstance) left)) {
                        List ll = new ArrayList();
                        for (int i = 0; i < ValueNumber.toNumber(left); i++) {
                            ll.addAll(ValueList.toList(right));
                        }
                        return interpreter.toKodeValue(ll);
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="append">
        this.methods.put("append", new KodeBuiltinFunction("append", null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                Object obj = arguments.get("obj");
                if (This instanceof KodeInstance) {
                    if (ValueList.isList((KodeInstance) This)) {
                        ValueList.toList(This).add(interpreter.toKodeValue(obj));
                        return null;
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
    }

    static List toList(Object x_) {
        return ValueList.toList(x_, x_);
    }

    //<editor-fold defaultstate="collapsed" desc="toList">
    private static List toList(Object x_, Object a) {
        if (x_ instanceof List) {
            return (List) x_;
        } else if (x_ instanceof KodeInstance) {
            if (((KodeInstance) x_).klass instanceof ValueList) {
                return ((KodeInstance) x_).list;
            } else {
                try {
                    if (((KodeInstance) x_).fields.containsKey(Kode.LIST)) {
                        Object get = ((KodeInstance) x_).fields.get(Kode.LIST);
                        if (get instanceof KodeFunction) {
                            return toList(((KodeFunction) get).bind((KodeInstance) x_).call(new HashMap()), a);
                        }
                    }
                    return toList(((KodeInstance) x_).klass.findMethod(Kode.LIST).bind((KodeInstance) x_).call(new HashMap()), a);
                } catch (NotImplemented e) {
                    throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Iterable in Nature", null);
                }
            }
        } else {
            throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Iterable in Nature", null);
        }
    }
//</editor-fold>

    final static boolean isList(KodeInstance i) {
        return instanceOf(i.klass, ValueList.class);
    }

}
