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
class ValueString extends Value {

    static KodeInstance create(String x, Interpreter interpreter) {
        Value val = new ValueString(interpreter);
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT_NAME);
        initializer.bind(instance).call(Arrays.asList(x));
        return instance;
    }

    ValueString(Interpreter interpreter) {
        super("String", interpreter);
        this.methods.put(Kode.INIT_NAME, new KodeBuiltinFunction(Kode.INIT_NAME, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("x", false));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    ((KodeInstance) This).str = ValueString.toStr(arguments.get("x"));
                }
                return This;
            }
        });
        this.methods.put(Kode.STR_NAME, new KodeBuiltinFunction(Kode.STR_NAME, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    return This;
                }
                throw new NotImplemented();
            }
        });
        this.methods.put(Kode.NUMBER_NAME, new KodeBuiltinFunction(Kode.NUMBER_NAME, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    try {
                        return interpreter.toKodeValue(KodeTools.toNumber(((KodeInstance) This).str));
                    } catch (Exception ex) {
                        throw new RuntimeError(ex.getMessage(), null);
                    }
                }
                throw new NotImplemented();
            }
        });
        this.methods.put(Kode.BOOL_NAME, new KodeBuiltinFunction(Kode.BOOL_NAME, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    return interpreter.toKodeValue(interpreter.isTruthy(((KodeInstance) This).str));
                }
                throw new NotImplemented();
            }
        });
        this.methods.put(Kode.LIST_NAME, new KodeBuiltinFunction(Kode.LIST_NAME, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    try {
                        List ll = new ArrayList();
                        for (char ch : ((KodeInstance) This).str.toCharArray()) {
                            ll.add(interpreter.toKodeValue("" + ch));
                        }
                        return interpreter.toKodeValue(ll);
                    } catch (Exception ex) {
                        throw new RuntimeError(ex.getMessage(), null);
                    }
                }
                throw new NotImplemented();
            }
        });
        this.methods.put(Kode.INDEX_NAME, new KodeBuiltinFunction(Kode.INDEX_NAME, null, interpreter) {

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
                        Double toNumber = ValueNumber.toNumber((KodeInstance) index);
                        if (toNumber.intValue() != toNumber) {
                            throw new RuntimeError("String Indices must be Integer in Nature found " + Kode.stringify(toNumber), null);
                        }
                        try {
                            return interpreter.toKodeValue("" + ((KodeInstance) This).str.charAt(toNumber.intValue()));
                        } catch (IndexOutOfBoundsException e) {
                            throw new RuntimeError("String Index Out Of Bound : " + Kode.stringify(toNumber), null);
                        }
                    }
                }
                throw new NotImplemented();
            }
        });

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
                    if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueString.toStr(left).concat(ValueString.toStr(right)));
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
                    if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueString.toStr(left).concat(ValueString.toStr(right)));
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="MUL">
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
                    if (ValueString.isString((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        String str = "";
                        for (int i = 0; i < ValueNumber.toNumber(right); i++) {
                            str = str.concat(ValueString.toStr(left));
                        }
                        return interpreter.toKodeValue(str);
                    }
                    if (ValueString.isString((KodeInstance) right) && ValueNumber.isNumber((KodeInstance) left)) {
                        String str = "";
                        for (int i = 0; i < ValueNumber.toNumber(left); i++) {
                            str = str.concat(ValueString.toStr(right));
                        }
                        return interpreter.toKodeValue(str);
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
                    if (ValueString.isString((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        String str = "";
                        for (int i = 0; i < ValueNumber.toNumber(right); i++) {
                            str = str.concat(ValueString.toStr(left));
                        }
                        return interpreter.toKodeValue(str);
                    }
                    if (ValueString.isString((KodeInstance) right) && ValueNumber.isNumber((KodeInstance) left)) {
                        String str = "";
                        for (int i = 0; i < ValueNumber.toNumber(left); i++) {
                            str = str.concat(ValueString.toStr(right));
                        }
                        return interpreter.toKodeValue(str);
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>

        this.methods.put("concat", new KodeBuiltinFunction("concat", null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                Object obj = arguments.get("obj");
                if (This instanceof KodeInstance && obj instanceof KodeInstance) {
                    if (ValueString.isString((KodeInstance) This) && ValueString.isString((KodeInstance) obj)) {
                        return interpreter.toKodeValue(ValueString.toStr(This).concat(ValueString.toStr(obj)));
                    }
                }
                throw new NotImplemented();
            }
        });

    }

    static String toStr(Object x_) {
        return ValueString.toStr(x_, x_);
    }

    private static String toStr(Object x_, Object a) {
        if (x_ instanceof String) {
            return (String) x_;
        } else if (x_ instanceof KodeInstance) {
            if (((KodeInstance) x_).klass instanceof ValueString) {
                return ((KodeInstance) x_).str;
            } else {
                try {
                    if (((KodeInstance) x_).fields.containsKey(Kode.STR_NAME)) {
                        Object get = ((KodeInstance) x_).fields.get(Kode.STR_NAME);
                        if (get instanceof KodeFunction) {
                            return toStr(((KodeFunction) get).bind((KodeInstance) x_).call(new HashMap()), a);
                        }
                    }
                    return toStr(((KodeInstance) x_).klass.findMethod(Kode.STR_NAME).bind((KodeInstance) x_).call(new HashMap()), a);
                } catch (NotImplemented e) {
                    throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Printable in Nature", null);
                }
            }
        } else {
            throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Printable in Nature", null);
        }
    }

    final static boolean isString(KodeInstance i) {
        return instanceOf(i.klass, ValueString.class);
    }

}
