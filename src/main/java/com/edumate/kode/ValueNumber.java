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
class ValueNumber extends Value {
    
    static KodeInstance create(Double x, Interpreter interpreter) {
        Value val = new ValueNumber(interpreter);
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT_NAME);
        initializer.bind(instance).call(Arrays.asList(x));
        return instance;
    }
    
    ValueNumber(Interpreter interpreter) {
        super("Number", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT_NAME, new KodeBuiltinFunction(Kode.INIT_NAME, null, interpreter) {
            
            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("x", false));
            }
            
            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    ((KodeInstance) This).num = ValueNumber.toNumber(arguments.get("x"));
                }
                return This;
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STR_NAME, new KodeBuiltinFunction(Kode.STR_NAME, null, interpreter) {
            
            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }
            
            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    return ValueString.create(Kode.stringify(((KodeInstance) This).num), interpreter);
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="neg">
        this.methods.put(Kode.NEG, new KodeBuiltinFunction(Kode.NEG, null, interpreter) {
            
            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }
            
            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        return interpreter.toKodeValue(-((KodeInstance) This).num);
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
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(left) + ValueNumber.toNumber(right));
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
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(left) + ValueNumber.toNumber(right));
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
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(left) * ValueNumber.toNumber(right));
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
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(left) * ValueNumber.toNumber(right));
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Sub">
        this.methods.put(Kode.SUB, new KodeBuiltinFunction(Kode.SUB, null, interpreter) {
            
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
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(left) - ValueNumber.toNumber(right));
                    }
                }
                throw new NotImplemented();
            }
        });
        this.methods.put(Kode.RSUB, new KodeBuiltinFunction(Kode.RSUB, null, interpreter) {
            
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
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(left) - ValueNumber.toNumber(right));
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Div">
        this.methods.put(Kode.DIV, new KodeBuiltinFunction(Kode.DIV, null, interpreter) {
            
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
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(left) / ValueNumber.toNumber(right));
                    }
                }
                throw new NotImplemented();
            }
        });
        this.methods.put(Kode.RDIV, new KodeBuiltinFunction(Kode.RDIV, null, interpreter) {
            
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
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(left) / ValueNumber.toNumber(right));
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Mod">
        this.methods.put(Kode.MOD, new KodeBuiltinFunction(Kode.MOD, null, interpreter) {
            
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
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(left) % ValueNumber.toNumber(right));
                    }
                }
                throw new NotImplemented();
            }
        });
        this.methods.put(Kode.RMOD, new KodeBuiltinFunction(Kode.RMOD, null, interpreter) {
            
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
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(left) % ValueNumber.toNumber(right));
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="floor_div">
        this.methods.put(Kode.FLOOR_DIV, new KodeBuiltinFunction(Kode.FLOOR_DIV, null, interpreter) {
            
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
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(Math.floor(ValueNumber.toNumber(left) / ValueNumber.toNumber(right)));
                    }
                }
                throw new NotImplemented();
            }
        });
        this.methods.put(Kode.RFLOOR_DIV, new KodeBuiltinFunction(Kode.RFLOOR_DIV, null, interpreter) {
            
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
                        left = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0));
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0));
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return interpreter.toKodeValue(Math.floor(ValueNumber.toNumber(left) / ValueNumber.toNumber(right)));
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
    }
    
    static Double toNumber(Object x_) {
        return ValueNumber.toNumber(x_, x_);
    }
    
    //<editor-fold defaultstate="collapsed" desc="toNumber">
    private static Double toNumber(Object x_, Object a) {
        if (x_ instanceof Double) {
            return (Double) x_;
        } else if (x_ instanceof KodeInstance) {
            if (((KodeInstance) x_).klass instanceof ValueNumber) {
                return ((KodeInstance) x_).num;
            } else {
                try {
                    if (((KodeInstance) x_).fields.containsKey(Kode.NUMBER_NAME)) {
                        Object get = ((KodeInstance) x_).fields.get(Kode.NUMBER_NAME);
                        if (get instanceof KodeFunction) {
                            return toNumber(((KodeFunction) get).bind((KodeInstance) x_).call(new HashMap()), a);
                        }
                    }
                    return toNumber(((KodeInstance) x_).klass.findMethod(Kode.NUMBER_NAME).bind((KodeInstance) x_).call(new HashMap()), a);
                } catch (NotImplemented e) {
                    throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Numeric in Nature", null);
                }
            }
        } else {
            throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Numeric in Nature", null);
        }
    }
//</editor-fold>
    
    final static boolean isNumber(KodeInstance i) {
        return instanceOf(i.klass, ValueNumber.class);
    }
    
}
