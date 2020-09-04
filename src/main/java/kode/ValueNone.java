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
class ValueNone extends Value {

    static Value val = new ValueNone(new Interpreter());

    static KodeInstance create() {
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call();
        return instance;
    }

    private ValueNone(Interpreter interpreter) {
        super("NoneType", interpreter);
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueNone.isNone((KodeInstance) This)) {
                    return interpreter.toKodeValue(Kode.stringify(null));
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="bool">
        this.methods.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueNone.isNone((KodeInstance) This)) {
                    return interpreter.toKodeValue(false);
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
    }

    final static boolean isNone(KodeInstance i) {
        return instanceOf(i.klass, ValueNone.class);
    }

}
