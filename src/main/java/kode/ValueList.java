/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.List;

/**
 *
 * @author dell
 */
class ValueList extends Value {

    static Value val = new ValueList(new Interpreter());

    static KodeInstance create(List<Object> x) {
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call(x);
        return instance;
    }

    private ValueList(Interpreter interpreter) {
        super("List", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter) {

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    ((KodeInstance) This).data = ValueList.toList(arguments[0]);
                }
                return This;
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueList.isList((KodeInstance) This)) {
                        try {
                            Object i;
                            if (!((KodeInstance) This).reccured) {
                                ((KodeInstance) This).reccured = true;
                                i = interpreter.toKodeValue(Kode.stringify(ValueList.toList(This)));
                            } else {
                                i = interpreter.toKodeValue(Kode.stringify("[...]"));
                            }
                            ((KodeInstance) This).reccured = false;
                            return i;
                        } catch (Throwable e) {
                            ((KodeInstance) This).reccured = false;
                            throw e;
                        }
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="bool">
        this.methods.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueList.isList((KodeInstance) This)) {
                        return interpreter.toKodeValue(Interpreter.isTruthy(ValueList.toList(This)));
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="list">
        this.methods.put(Kode.LIST, new KodeBuiltinFunction(Kode.LIST, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueList.isList((KodeInstance) This)) {
                        return This;
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="append">
        this.methods.put("append", new KodeBuiltinFunction("append", interpreter) {

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                Object obj = arguments[0];
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

    //<editor-fold defaultstate="collapsed" desc="toList">
    @SuppressWarnings({ "unchecked", "rawtypes" })
	static List<Object> toList(Object x) {
        Object a = x;
        for (;;) {
            if (x instanceof List) {
                return (List) x;
            } else if (x instanceof KodeInstance) {
                if (((KodeInstance) x).klass instanceof ValueList) {
                    return (List) ((KodeInstance) x).data;
                } else {
                    try {
                        if (((KodeInstance) x).fields.containsKey(Kode.LIST)) {
                            Object get = ((KodeInstance) x).fields.get(Kode.LIST);
                            if (get instanceof KodeFunction) {
                                x = ((KodeFunction) get).bind((KodeInstance) x).call();
                                continue;
                            }
                        }
                        x = ((KodeInstance) x).klass.findMethod(Kode.LIST).bind((KodeInstance) x).call();
                        continue;
                    } catch (NotImplemented e) {
                        throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Iterable in Nature", null);
                    }
                }
            }
            throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Iterable in Nature", null);
        }
    }
//</editor-fold>

    final static boolean isList(KodeInstance i) {
        return instanceOf(i.klass, ValueList.class);
    }

}
