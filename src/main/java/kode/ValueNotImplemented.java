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
        KodeInstance ins = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(ins).call();
        return ins;
    }

    private ValueNotImplemented(Interpreter interpreter) {
        super("NotImplemented", ValueError.val, interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, null, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object at = closure.getAt(0, "this");
                if (at instanceof KodeInstance) {
                    ((KodeInstance) at).klass.superclass.findMethod(Kode.INIT).bind((KodeInstance) at)
                            .call(this.interpreter.toKodeValue("This method is not implemented yet."));
                }
                return at;
            }
        });
//</editor-fold>
    }

    final static boolean isNotImplemented(KodeInstance i) {
        return instanceOf(i.klass, ValueNotImplemented.class);
    }

}
