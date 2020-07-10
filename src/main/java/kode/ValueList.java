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
class ValueList extends Value {

    static Value val = new ValueList(new Interpreter());

    static KodeInstance create(List x) {
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call(Arrays.asList(x));
        return instance;
    }

    private ValueList(Interpreter interpreter) {
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
                    ((KodeInstance) This).data = ValueList.toList(arguments.get("x"));
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
                            i = interpreter.toKodeValue(Kode.stringify(((KodeInstance) This).data));
                        } else {
                            i = interpreter.toKodeValue(Kode.stringify("[...]"));
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
                return (List) ((KodeInstance) x_).data;
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
