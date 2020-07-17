/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author dell
 */
class ValueError extends Value {

    static Value val = new ValueError(new Interpreter());

    static KodeInstance create(String msg) {
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        Map par = new HashMap();
        par.put("args", val.interpreter.toKodeValue(Arrays.asList(val.interpreter.toKodeValue(msg))));
//        initializer.bind(instance).call(par);
        return instance;
    }

    private ValueError(Interpreter interpreter) {
        super("Error", interpreter);
//        //<editor-fold defaultstate="collapsed" desc="init">
//        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, null, interpreter) {
//
//            @Override
//            public List<Pair<String, Object>> arity() {
//                return Arrays.asList(new Pair("args", interpreter.toKodeValue(Arrays.asList())).setType(TokenType.STAR));
//            }
//
//            @Override
//            public Object call(Map<String, Object> arguments) {
//                Object at = closure.getAt(0, "this");
//                if (at instanceof KodeInstance) {
//                    ((KodeInstance) at).set("args", arguments.get("args"));
//                }
//                return at;
//            }
//        });
////</editor-fold>
//
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
//                    Object get = ((KodeInstance) This).get("args");
//                    if (get == null) {
//                        get = "<Missing Error Details>";
//                    }
//                    if (get instanceof KodeInstance) {
//                        if (ValueList.isList((KodeInstance) get)) {
//                            get = ValueList.toList(get).stream()
//                                    .map(n -> n.toString())
//                                    .collect(Collectors.joining("\n"));
//                        }
//                    }
//                    return interpreter.toKodeValue(get.toString());
//                }
//                throw new NotImplemented();
//            }
//        });
////</editor-fold>
    }

    final static boolean isError(KodeInstance i) {
        return instanceOf(i.klass, ValueError.class);
    }

}
