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
class ValueFile extends Value {

    static Value val = new ValueFile(new Interpreter());

    static KodeInstance create(String filename, String mode, String encoding) {
        KodeInstance instance = new KodeInstance(val);
        return instance;
    }

    private ValueFile(Interpreter interpreter) {
        super("File", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                throw new RuntimeError("Unsupported Operation. Use built-in function open() instead.");
            }
        });
//</editor-fold>
    }

    final static boolean isFile(KodeInstance i) {
        return instanceOf(i.klass, ValueFile.class);
    }

}
