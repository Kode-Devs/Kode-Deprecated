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

/**
 *
 * @author dell
 */
class ValueBool extends Value {

    static Value val = new ValueBool(new Interpreter());

    static KodeInstance create(Boolean x) {
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call(Arrays.asList(x));
        return instance;
    }

    private ValueBool(Interpreter interpreter) {
        super("Bool", interpreter);
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
                    ((KodeInstance) This).data = ValueBool.toBoolean(arguments.get("x"));
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
                    return interpreter.toKodeValue(Kode.stringify(((KodeInstance) This).data));
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
                return (Boolean) ((KodeInstance) x_).data;
            } else {
                try {
                    if (((KodeInstance) x_).fields.containsKey(Kode.BOOLEAN)) {
                        Object get = ((KodeInstance) x_).fields.get(Kode.BOOLEAN);
                        if (get instanceof KodeFunction) {
                            return toBoolean(((KodeFunction) get).bind((KodeInstance) x_).call(new HashMap()), a);
                        }
                    }
                    return toBoolean(((KodeInstance) x_).klass.findMethod(Kode.BOOLEAN).bind((KodeInstance) x_).call(new HashMap()), a);
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
