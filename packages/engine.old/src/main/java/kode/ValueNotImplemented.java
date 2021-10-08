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
 * NotImplemented Error DataType
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
class ValueNotImplemented extends Value {

    static Value val = new ValueNotImplemented(new Interpreter());

    static KodeInstance create() {
        KodeInstance instance = new KodeInstance(val);
        val.findMethod(Kode.INIT).bind(instance).call();
        return instance;
    }

    private ValueNotImplemented(Interpreter interpreter) {
        super("NotImplemented", ValueError.val, interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                ((KodeInstance) This).klass.superclass.findMethod(Kode.INIT).bind((KodeInstance) This)
                        .call(Interpreter.toKodeValue("This method is not implemented yet."));
            }
            return This;
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="init subclass">
        this.methods.put(Kode.INIT_SUBCLASS, new KodeBuiltinFunction(Kode.INIT_SUBCLASS, interpreter, null, -3, args -> {
            throw new RuntimeError("Class " + ValueNotImplemented.val.class_name + " can not be used as superclass.");
        }));
//</editor-fold>
    }

    static boolean isNotImplemented(KodeInstance i) {
        return instanceOf(i.klass, ValueNotImplemented.class);
    }

}
