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
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 2, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                ((KodeInstance) This).data = args[1];
            }
            return This;
        }));
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                if (((KodeInstance) This).data != null) {
                    return interpreter.toKodeValue("<type '" + Kode.type(((KodeInstance) This).data) + "'>");
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="eq">
        this.methods.put(Kode.EQ, new KodeBuiltinFunction(Kode.EQ, interpreter, null, 2, args -> {
            Object This = args[0];
            Object klass = args[1];
            if (This instanceof KodeInstance) {
                if (klass instanceof KodeClass && ((KodeInstance) This).data instanceof KodeInstance) {
                    return interpreter.toKodeValue(
                            Objects.equals(((KodeInstance) ((KodeInstance) This).data).klass, (KodeClass) klass));
                }
            }
            return interpreter.toKodeValue(Comparator.eq(This, klass, interpreter));
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="ne">
        this.methods.put(Kode.NE, new KodeBuiltinFunction(Kode.NE, interpreter, null, 2, args -> {
            Object This = args[0];
            Object klass = args[1];
            if (This instanceof KodeInstance) {
                if (klass instanceof KodeClass && ((KodeInstance) This).data instanceof KodeInstance) {
                    return interpreter.toKodeValue(
                            !Objects.equals(((KodeInstance) ((KodeInstance) This).data).klass, (KodeClass) klass));
                }
            }
            return interpreter.toKodeValue(Comparator.ne(This, klass, interpreter));
        }));
        //</editor-fold>
    }

}
