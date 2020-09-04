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
class ValueNative extends Value {

    static Value val = new ValueNative(new Interpreter());

    static KodeInstance create(Object x) {
        KodeInstance instance = new KodeInstance(val);
        instance.data = x;
        return instance;
    }

    private ValueNative(Interpreter interpreter) {
        super("Native", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 1, args -> {
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, args -> {
                Object This = args[0];
                if (This instanceof KodeInstance) {
                    return interpreter.toKodeValue("<native object '" + ((KodeInstance) This).data == null ? "null" : ((KodeInstance) This).data + "'>");
                }
                throw new NotImplemented();
            }));
//</editor-fold>
    }

    final static boolean isNative(KodeInstance i) {
        return instanceOf(i.klass, ValueNative.class);
    }

}
