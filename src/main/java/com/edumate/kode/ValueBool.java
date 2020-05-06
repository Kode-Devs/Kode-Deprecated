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
class ValueBool extends Value {

    private Boolean x;

    static KodeInstance create(Boolean x, Interpreter interpreter) {
        Value val = new ValueBool(interpreter);
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT_NAME);
        initializer.bind(instance).call(Arrays.asList(x));
        return instance;
    }

    ValueBool(Interpreter interpreter) {
        super("Bool", interpreter);
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
                    ((KodeInstance) This).bool = ValueBool.toBoolean(arguments.get("x"));
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
                    return ValueString.create(Kode.stringify(((KodeInstance) This).bool), interpreter);
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
                    if(ValueBool.isBool((KodeInstance) This))
                        This = interpreter.toKodeValue(new Double(ValueBool.toBoolean((KodeInstance) This) ? 1 : 0));
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        return interpreter.toKodeValue(-((KodeInstance) This).num);
                    }
                }
                throw new NotImplemented();
            }
        });
        //</editor-fold>
    }

    static Boolean toBoolean(Object x_) {
        return ValueBool.toBoolean(x_, x_);
    }

    //<editor-fold defaultstate="collapsed" desc="toBoolean">
    private static Boolean toBoolean(Object x_, Object a) {
        if (x_ instanceof Boolean) {
            return (Boolean) x_;
        } else if (x_ instanceof KodeInstance) {
            if (((KodeInstance) x_).klass instanceof ValueBool) {
                return ((KodeInstance) x_).bool;
            } else {
                try {
                    if (((KodeInstance) x_).fields.containsKey(Kode.BOOL_NAME)) {
                        Object get = ((KodeInstance) x_).fields.get(Kode.BOOL_NAME);
                        if (get instanceof KodeFunction) {
                            return toBoolean(((KodeFunction) get).bind((KodeInstance) x_).call(new HashMap()), a);
                        }
                    }
                    return toBoolean(((KodeInstance) x_).klass.findMethod(Kode.BOOL_NAME).bind((KodeInstance) x_).call(new HashMap()), a);
                } catch (NotImplemented e) {
                    throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Boolean in Nature", null);
                }
            }
        } else {
            throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Boolean in Nature", null);
        }
    }
//</editor-fold>

    final static boolean isBool(KodeInstance i) {
        return instanceOf(i.klass, ValueBool.class);
    }

}
