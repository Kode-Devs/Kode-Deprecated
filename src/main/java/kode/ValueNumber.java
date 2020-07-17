/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import math.KodeNumber;

/**
 *
 * @author dell
 */
class ValueNumber extends Value {
    
    @Override
    String doc(){
        return "Number Class wrapping over Java Double data-type."; 
    }

    static Value val = new ValueNumber(new Interpreter());

    static KodeInstance create(KodeNumber x) {
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call(x);
        return instance;
    }

    private ValueNumber(Interpreter interpreter) {
        super("Number", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, null, interpreter) {

            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    ((KodeInstance) This).data = ValueNumber.toNumber(arguments[0]);
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
                    return interpreter.toKodeValue(Kode.stringify(((KodeInstance) This).data));
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
    }

    static KodeNumber toNumber(Object x_) {
        return ValueNumber.toNumber(x_, x_);
    }

    //<editor-fold defaultstate="collapsed" desc="toNumber">
    private static KodeNumber toNumber(Object x_, Object a) {
        if (x_ instanceof KodeNumber) {
            return (KodeNumber) x_;
        } else if (x_ instanceof KodeInstance) {
            if (((KodeInstance) x_).klass instanceof ValueNumber) {
                return (KodeNumber) ((KodeInstance) x_).data;
            } else {
                try {
                    if (((KodeInstance) x_).fields.containsKey(Kode.NUMBER)) {
                        Object get = ((KodeInstance) x_).fields.get(Kode.NUMBER);
                        if (get instanceof KodeFunction) {
                            return toNumber(((KodeFunction) get).bind((KodeInstance) x_).call(), a);
                        }
                    }
                    return toNumber(((KodeInstance) x_).klass.findMethod(Kode.NUMBER).bind((KodeInstance) x_).call(), a);
                } catch (NotImplemented e) {
                    throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Numeric in Nature", null);
                }
            }
        } else {
            throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Numeric in Nature", null);
        }
    }
//</editor-fold>

    final static boolean isNumber(KodeInstance i) {
        return instanceOf(i.klass, ValueNumber.class);
    }

}
