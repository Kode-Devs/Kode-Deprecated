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

import java.util.Objects;
import kni.KodeObject;

/**
 *
 * @author dell
 */
class ValueType extends Value {

    static Value val = new ValueType(new Interpreter());

    private ValueType(Interpreter interpreter) {
        super("type", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 2, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                ((KodeInstance) This).data = args[1];
            }
            return This;
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="init subclass">
        this.methods.put(Kode.INIT_SUBCLASS, new KodeBuiltinFunction(Kode.INIT_SUBCLASS, interpreter, null, -3, args -> {
            throw new RuntimeError("Class " + ValueType.val.class_name + " can not be used as superclass.");
        }));
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="str">
        this.methods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                Object data = ((KodeInstance) This).data;
                if (data != null) {
                    return interpreter.toKodeValue("<type '" + (data instanceof KodeObject
                            ? Kode.type((KodeObject) data) : data.getClass().getName()) + "'>");
                }
            }
            throw new NotImplemented();
        }));
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="eq">
        this.methods.put(Kode.EQ, new KodeBuiltinFunction(Kode.EQ, interpreter, null, 2, args -> {
            KodeObject This = args[0];
            KodeObject klass = args[1];
            if (This instanceof KodeInstance) {
                if (klass instanceof KodeClass && ((KodeInstance) This).data instanceof KodeInstance) {
                    return interpreter.toKodeValue(
                            Objects.equals(((KodeInstance) ((KodeInstance) This).data).klass, (KodeClass) klass));
                }
            }
            return interpreter.toKodeValue(Comparator.eq(This, klass, interpreter));
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="ne">
        this.methods.put(Kode.NE, new KodeBuiltinFunction(Kode.NE, interpreter, null, 2, args -> {
            KodeObject This = args[0];
            KodeObject klass = args[1];
            if (This instanceof KodeInstance) {
                if (klass instanceof KodeClass && ((KodeInstance) This).data instanceof KodeInstance) {
                    return interpreter.toKodeValue(
                            !Objects.equals(((KodeInstance) ((KodeInstance) This).data).klass, (KodeClass) klass));
                }
            }
            return interpreter.toKodeValue(Comparator.ne(This, klass, interpreter));
        }));
        //</editor-fold>
    }

}
