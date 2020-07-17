/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
class ValueNative extends Value {

    static Value val = new ValueNative(new Interpreter());

    static KodeInstance create(Object x) {
        KodeInstance instance = new KodeInstance(val);
        instance.data = x;
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call(new Object[0]);
        return instance;
    }

    private ValueNative(Interpreter interpreter) {
        super("Native", interpreter);
//        //<editor-fold defaultstate="collapsed" desc="str">
//        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, null, interpreter) {
//
//            @Override
//            public List<Pair<String, Object>> arity() {
//                return new ArrayList();
//            }
//
//            @Override
//            public Object call(Map<String, Object> arguments) {
//                Object This = closure.getAt(0, "this");
//                if (This instanceof KodeInstance) {
//                    return interpreter.toKodeValue("<native object '" + ((KodeInstance) This).data + "'>");
//                }
//                throw new NotImplemented();
//            }
//        });
////</editor-fold>
    }
    
    final static boolean isNative(KodeInstance i) {
        return instanceOf(i.klass, ValueNative.class);
    }

}
