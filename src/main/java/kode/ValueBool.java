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
                return 0;
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
                    return interpreter.toKodeValue(Kode.stringify(((KodeInstance) This).data));
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
                    return This;
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
                    return interpreter.toKodeValue(Interpreter.isTruthy(This) ? 1 : 0);
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
                            return toBoolean(((KodeFunction) get).bind((KodeInstance) x_).call(), a);
                        }
                    }
                    return toBoolean(((KodeInstance) x_).klass.findMethod(Kode.BOOLEAN).bind((KodeInstance) x_).call(), a);
                } catch (NotImplemented e) {
                }
            }
        }
        return Interpreter.isTruthy(x_);
    }
//</editor-fold>

    final static boolean isBool(KodeInstance i) {
        return instanceOf(i.klass, ValueBool.class);
    }

}
