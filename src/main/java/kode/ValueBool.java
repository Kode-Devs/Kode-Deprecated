/* 
 * Copyright (C) 2020 Kode Devs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kode;

import kni.KodeObject;

/**
 *
 * @author dell
 */
class ValueBool extends Value {

    static Value val = new ValueBool(new Interpreter());

    static KodeInstance create(Boolean x) {
        KodeInstance instance = new KodeInstance(val);
        instance.data = x;
        KodeFunction initializer = val.findMethod(Kode.INIT);
        initializer.bind(instance).call(instance);
        return instance;
    }

    private ValueBool(Interpreter interpreter) {
        super("Bool", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 2, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                ((KodeInstance) This).data = ValueBool.toBoolean(args[1]);
            }
            return This;
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="init subclass">
        this.methods.put(Kode.INIT_SUBCLASS, new KodeBuiltinFunction(Kode.INIT_SUBCLASS, interpreter, null, -3, args -> {
            throw new RuntimeError("Class " + ValueBool.val.class_name + " can not be used as superclass.");
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) This)) {
                    return interpreter.toKodeValue(Kode.stringify(ValueBool.toBoolean(This)));
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="bool">
        this.methods.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) This)) {
                    return This;
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="num">
        this.methods.put(Kode.NUMBER, new KodeBuiltinFunction(Kode.NUMBER, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) This)) {
                    return interpreter.toKodeValue(Interpreter.isTruthy(ValueBool.toBoolean(This)) ? 1 : 0);
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="toBoolean">
    static Boolean toBoolean(KodeObject x) {
        for (;;) {
            if (x instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) x)) {
                    return (Boolean) ((KodeInstance) x).data;
                } else {
                    try {
                        if (((KodeInstance) x).fields.containsKey(Kode.BOOLEAN)) {
                            Object get = ((KodeInstance) x).fields.get(Kode.BOOLEAN);
                            if (get instanceof KodeFunction) {
                                x = ((KodeFunction) get).bind((KodeInstance) x).call();
                                continue;
                            }
                        }
                        x = ((KodeInstance) x).klass.findMethod(Kode.BOOLEAN).bind((KodeInstance) x).call();
                        continue;
                    } catch (NotImplemented e) {
                    }
                }
            }
            return Interpreter.isTruthy(x);
        }
    }
//</editor-fold>

    final static boolean isBool(KodeInstance i) {
        return instanceOf(i.klass, ValueBool.class);
    }

}
