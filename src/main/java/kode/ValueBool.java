/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

/**
 *
 * @author dell
 */
class ValueBool extends Value {

    static Value val = new ValueBool(new Interpreter());

    static KodeInstance create(Boolean x) {
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call(x);
        return instance;
    }

    private ValueBool(Interpreter interpreter) {
        super("Bool", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, null, interpreter) {

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    ((KodeInstance) This).data = ValueBool.toBoolean(arguments[0]);
                }
                return This;
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, null, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueBool.isBool((KodeInstance) This)) {
                        return interpreter.toKodeValue(Kode.stringify(ValueBool.toBoolean(This)));
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="bool">
        this.methods.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, null, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueBool.isBool((KodeInstance) This)) {
                        return This;
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="num">
        this.methods.put(Kode.NUMBER, new KodeBuiltinFunction(Kode.NUMBER, null, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueBool.isBool((KodeInstance) This)) {
                        return interpreter.toKodeValue(Interpreter.isTruthy(ValueBool.toBoolean(This)) ? 1 : 0);
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="toBoolean">
    static Boolean toBoolean(Object x) {
        for (;;) {
            if (x instanceof Boolean) {
                return (Boolean) x;
            } else if (x instanceof KodeInstance) {
                if (((KodeInstance) x).klass instanceof ValueBool) {
                    return (Boolean) ((KodeInstance) x).data;
                } else {
                    try {
                        if (((KodeInstance) x).fields.containsKey(Kode.BOOLEAN)) {
                            Object get = ((KodeInstance) x).fields.get(Kode.BOOLEAN);
                            if (get instanceof KodeFunction) {
                                x = ((KodeFunction) get).bind((KodeInstance) x).call();
                                continue;
                            }
                        }
                        x = ((KodeInstance) x).klass.findMethod(Kode.BOOLEAN).bind((KodeInstance) x).call();
                        continue;
                    } catch (NotImplemented e) {
                    }
                }
            }
            return Interpreter.isTruthy(x);
        }
    }
//</editor-fold>

    final static boolean isBool(KodeInstance i) {
        return instanceOf(i.klass, ValueBool.class);
    }

}
