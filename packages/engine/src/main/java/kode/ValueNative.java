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

/**
 * Native DataType
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
class ValueNative extends Value {

    static Value val = new ValueNative(new Interpreter());

    static KodeInstance create(Object x) {
        KodeInstance instance = new KodeInstance(val);
        instance.data = x;
        return instance;
    }

    private ValueNative(Interpreter interpreter) {
        super("Native", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 1, args -> {
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="init subclass">
        this.methods.put(Kode.INIT_SUBCLASS, new KodeBuiltinFunction(Kode.INIT_SUBCLASS, interpreter, null, -3, args -> {
            throw new RuntimeError("Class " + ValueNative.val.class_name + " can not be used as superclass.");
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, args -> {
            Object This = args[0];
            if (This instanceof KodeInstance) {
                return Interpreter.toKodeValue("<native object '" + (((KodeInstance) This).data == null ? "null" : ((KodeInstance) This).data) + "'>");
            }
            throw new NotImplemented();
        }));
//</editor-fold>
    }

    static boolean isNative(KodeInstance i) {
        return instanceOf(i.klass, ValueNative.class);
    }

}
