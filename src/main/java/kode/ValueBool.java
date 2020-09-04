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
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 2, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                ((KodeInstance) This).data = ValueBool.toBoolean(args[1]);
            }
            return This;
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) This)) {
                    return interpreter.toKodeValue(Kode.stringify(ValueBool.toBoolean(This)));
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="bool">
        this.methods.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) This)) {
                    return This;
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="num">
        this.methods.put(Kode.NUMBER, new KodeBuiltinFunction(Kode.NUMBER, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) This)) {
                    return interpreter.toKodeValue(Interpreter.isTruthy(ValueBool.toBoolean(This)) ? 1 : 0);
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="toBoolean">
    static Boolean toBoolean(Object x) {
        for (;;) {
            if (x instanceof Boolean) {
                return (Boolean) x;
            } else if (x instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) x)) {
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
