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
class ValueNotImplemented extends Value {

    static Value val = new ValueNotImplemented(new Interpreter());

    static KodeInstance create() {
        KodeInstance instance = new KodeInstance(val);
        val.findMethod(Kode.INIT).bind(instance).call();
        return instance;
    }

    private ValueNotImplemented(Interpreter interpreter) {
        super("NotImplemented", ValueError.val, interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 1, args -> {
                Object This = args[0];
                if (This instanceof KodeInstance) {
                    ((KodeInstance) This).klass.superclass.findMethod(Kode.INIT).bind((KodeInstance) This)
                            .call(this.interpreter.toKodeValue("This method is not implemented yet."));
                }
                return This;
            }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="init subclass">
        this.methods.put(Kode.INIT_SUBCLASS, new KodeBuiltinFunction(Kode.INIT_SUBCLASS, interpreter, null, -3, args -> {
            throw new RuntimeError("Class " + ValueNotImplemented.val.class_name + " can not be used as superclass.");
        }));
//</editor-fold>
    }

    final static boolean isNotImplemented(KodeInstance i) {
        return instanceOf(i.klass, ValueNotImplemented.class);
    }

}
