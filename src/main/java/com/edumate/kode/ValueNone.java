/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edumate.kode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
class ValueNone extends Value {

    static KodeInstance create(Interpreter interpreter) {
        Value val = new ValueNone(interpreter);
        KodeInstance instance = new KodeInstance(val);
        KodeFunction initializer = val.findMethod(Kode.INIT_NAME);
        initializer.bind(instance).call(new ArrayList());
        return instance;
    }

    ValueNone(Interpreter interpreter) {
        super("NoneType", interpreter);
        this.methods.put(Kode.INIT_NAME, new KodeBuiltinFunction(Kode.INIT_NAME, null, interpreter) {

            @Override
            public List<Pair<String, Object>> arity() {
                return new ArrayList();
            }

            @Override
            public Object call(Map<String, Object> arguments) {
                Object This = closure.getAt(0, "this");
                if (This instanceof KodeInstance) {
                    ((KodeInstance) This).fields.put(Kode.STR_NAME, new KodeBuiltinFunction(Kode.STR_NAME, null, interpreter) {

                        @Override
                        public List<Pair<String, Object>> arity() {
                            return new ArrayList();
                        }

                        @Override
                        public Object call(Map<String, Object> arguments) {
                            return ValueString.create(Kode.stringify(null), interpreter);
                        }
                    });
                }
                return This;
            }
        });
    }

    final static boolean isNone(KodeInstance i) {
        return instanceOf(i.klass, ValueNone.class);
    }

}
