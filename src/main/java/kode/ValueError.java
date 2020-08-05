/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.List;

/**
 *
 * @author dell
 */
class ValueError extends Value {

    static Value val = new ValueError(new Interpreter());

    static KodeInstance create(String msg) {
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call(val.interpreter.toKodeValue(msg));
        return instance;
    }

    private ValueError(Interpreter interpreter) {
        super("Error", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, null, interpreter) {

            @Override
            public int arity() {
                return -1;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    ((KodeInstance) This).set("args", this.interpreter.toKodeValue(arguments));
                }
                return This;
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, null, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    Object get = ((KodeInstance) This).get("args");
                    if (get == null) {
                        get = "<Missing Error Details>";
                    }
                    if (get instanceof KodeInstance) {
                        if (ValueList.isList((KodeInstance) get)) {
                            List<?> toList = ValueList.toList(get);
                            switch (toList.size()) {
                                case 0:
                                    get = "<Missing Error Details>";
                                    break;
                                case 1:
                                    get = toList.get(0);
                                    break;
                                default:
                                    get = toList;
                            }
                        }
                    }
                    return interpreter.toKodeValue(get.toString());
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="skip">
        this.methods.put("skip", new KodeBuiltinFunction("skip", null, interpreter) {

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                Object level = arguments[0];
                if (This instanceof KodeInstance && level instanceof KodeInstance) {
                    if (ValueNumber.isNumber((KodeInstance) level)) {
                        try {
                            ((KodeInstance) This).data = ValueNumber.toNumber(level).getInteger();
                            return This;
                        } catch (ArithmeticException e) {
                            throw new RuntimeError("Argument has non-zero fractional part.");
                        }
                    }
                    throw new RuntimeError("SArgument is not Numeric in nature.");
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
    }

    final static boolean isError(KodeInstance i) {
        return instanceOf(i.klass, ValueError.class);
    }

}
