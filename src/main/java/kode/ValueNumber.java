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
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter) {

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
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        return interpreter.toKodeValue(Kode.stringify(ValueNumber.toNumber(This)));
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="num">
        this.methods.put(Kode.NUMBER, new KodeBuiltinFunction(Kode.NUMBER, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        return This;
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="bool">
        this.methods.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        return interpreter.toKodeValue(Interpreter.isTruthy(ValueNumber.toNumber(This)));
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="isInteger">
        this.methods.put("isInt", new KodeBuiltinFunction("isInt", interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(This).isInteger());
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="asInt">
        this.methods.put("asInt", new KodeBuiltinFunction("asInt", interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        try {
                            return interpreter.toKodeValue(ValueNumber.toNumber(This).getInteger());
                        } catch (ArithmeticException e) {
                            throw new RuntimeError("Has fractional part.");
                        }
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="asReal">
        this.methods.put("asReal", new KodeBuiltinFunction("asReal", interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        return interpreter.toKodeValue(ValueNumber.toNumber(This).getFloat());
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="asIndex">
        this.methods.put("asIndex", new KodeBuiltinFunction("asIndex", interpreter) {

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Object... arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    if (ValueNumber.isNumber((KodeInstance) This)) {
                        try {
                            return interpreter.toKodeValue(ValueNumber.toNumber(This).getAsIndex());
                        } catch (ArithmeticException ex) {
                            throw new RuntimeError("Its value falls beyond range of Indexing.");
                        }
                    }
                }
                throw new NotImplemented();
            }
        });
//</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="toNumber">
    static KodeNumber toNumber(Object x) {
        Object a = x;
        for (;;) {
            if (x instanceof KodeNumber) {
                return (KodeNumber) x;
            } else if (x instanceof KodeInstance) {
                if (((KodeInstance) x).klass instanceof ValueNumber) {
                    return (KodeNumber) ((KodeInstance) x).data;
                } else {
                    try {
                        if (((KodeInstance) x).fields.containsKey(Kode.NUMBER)) {
                            Object get = ((KodeInstance) x).fields.get(Kode.NUMBER);
                            if (get instanceof KodeFunction) {
                                x = ((KodeFunction) get).bind((KodeInstance) x).call();
                                continue;
                            }
                        }
                        x = ((KodeInstance) x).klass.findMethod(Kode.NUMBER).bind((KodeInstance) x).call();
                        continue;
                    } catch (NotImplemented e) {
                        throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Numeric in Nature", null);
                    }
                }
            }
            throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Numeric in Nature", null);
        }
    }
//</editor-fold>

    final static boolean isNumber(KodeInstance i) {
        return instanceOf(i.klass, ValueNumber.class);
    }

}
