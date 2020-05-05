package com.edumate.kode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
class ValueNumber extends Value {

    static KodeInstance create(Double x, Interpreter interpreter) {
        Value val = new ValueNumber(interpreter);
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT_NAME);
        initializer.bind(instance).call(Arrays.asList(x));
        return instance;
    }

    ValueNumber(Interpreter interpreter) {
        super("Number", interpreter);
        this.methods.put(Kode.INIT_NAME, new KodeBuiltinFunction(Kode.INIT_NAME, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return Arrays.asList(new Pair("x", false));
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    ((KodeInstance) This).num = ValueNumber.toNumber(arguments.get("x"));
                }
                return This;
            }
        });
        this.methods.put(Kode.STR_NAME, new KodeBuiltinFunction(Kode.STR_NAME, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    return ValueString.create(Kode.stringify(((KodeInstance) This).num), interpreter);
                }
                throw new NotImplemented();
            }
        });
    }

    static Double toNumber(Object x_) {
        return ValueNumber.toNumber(x_, x_);
    }

    private static Double toNumber(Object x_, Object a) {
        if (x_ instanceof Double) {
            return (Double) x_;
        } else if (x_ instanceof KodeInstance) {
            if (((KodeInstance) x_).klass instanceof ValueNumber) {
                return ((KodeInstance) x_).num;
            } else {
                try {
                    if (((KodeInstance) x_).fields.containsKey(Kode.NUMBER_NAME)) {
                        Object get = ((KodeInstance) x_).fields.get(Kode.NUMBER_NAME);
                        if (get instanceof KodeFunction) {
                            return toNumber(((KodeFunction) get).bind((KodeInstance) x_).call(new HashMap()), a);
                        }
                    }
                    return toNumber(((KodeInstance) x_).klass.findMethod(Kode.NUMBER_NAME).bind((KodeInstance) x_).call(new HashMap()), a);
                } catch (NotImplemented e) {
                    throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Numeric in Nature", null);
                }
            }
        } else {
            throw new RuntimeError("Object of type '" + Kode.type(a) + "' is not Numeric in Nature", null);
        }
    }

    final static boolean isNumber(KodeInstance i) {
        return instanceOf(i.klass, ValueNumber.class);
    }

}
