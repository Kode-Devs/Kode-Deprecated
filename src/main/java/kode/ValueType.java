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
class ValueType extends Value {

    static Value val = new ValueType(new Interpreter());

    private ValueType(Interpreter interpreter) {
        super("type", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, null, interpreter) {
            
            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("obj", null));
            }
            
            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    ((KodeInstance) This).data = Kode.type(arguments.get("obj"));
                }
                return This;
            }
        });
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, null, interpreter) {
            
            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }
            
            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    return interpreter.toKodeValue("<type '"+((KodeInstance) This).data+"'>");
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
    }

}
