/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.Objects;

/**
 *
 * @author dell
 */
class ValueType extends Value {

    static Value val = new ValueType(new Interpreter());

    private ValueType(Interpreter interpreter) {
        super("type", interpreter);
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
                    ((KodeInstance) This).data = arguments[0];
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
                    if (((KodeInstance) This).data != null) {
                        return interpreter.toKodeValue("<type '" + Kode.type(((KodeInstance) This).data) + "'>");
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="eq">
        this.methods.put(Kode.EQ, new KodeBuiltinFunction(Kode.EQ, interpreter) {

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (arguments[0] instanceof KodeClass && ((KodeInstance) This).data instanceof KodeInstance) {
                        return interpreter.toKodeValue(
                                Objects.equals(((KodeInstance) ((KodeInstance) This).data).klass, (KodeClass) arguments[0]));
                    }
                }
                return interpreter.toKodeValue(eq(This, arguments[0]));
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="ne">
        this.methods.put(Kode.NE, new KodeBuiltinFunction(Kode.NE, interpreter) {

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (arguments[0] instanceof KodeClass && ((KodeInstance) This).data instanceof KodeInstance) {
                        return interpreter.toKodeValue(
                                !Objects.equals(((KodeInstance) ((KodeInstance) This).data).klass, (KodeClass) arguments[0]));
                    }
                }
                return interpreter.toKodeValue(eq(This, arguments[0]));
            }
        });
        //</editor-fold>
    }

}
