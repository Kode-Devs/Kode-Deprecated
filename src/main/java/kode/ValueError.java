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

import java.util.Arrays;
import java.util.List;
import kni.KodeObject;

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
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, -2, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                ((KodeInstance) This).set("args", this.interpreter.toKodeValue(Arrays.copyOfRange(args, 1, args.length)));
            }
            return This;
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                Object get = ((KodeInstance) This).get("args");
                if (get == null) {
                    get = "<Missing Error Details>";
                }
                if (get instanceof KodeInstance) {
                    if (ValueList.isList((KodeInstance) get)) {
                        List<?> toList = ValueList.toList((KodeInstance) get);
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
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="skip">
        this.methods.put("skip", new KodeBuiltinFunction("skip", interpreter, null, 2, args -> {
            KodeObject This = args[0];
            KodeObject level = args[1];
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
        }));
//</editor-fold>
    }

    final static boolean isError(KodeInstance i) {
        return instanceOf(i.klass, ValueError.class);
    }

}
