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
 * File DataType
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
class ValueFile extends Value {

    static Value val = new ValueFile(new Interpreter());

    static KodeInstance create(String filename, String mode, String encoding) {
        return new KodeInstance(val);
    }

    private ValueFile(Interpreter interpreter) {
        super("File", interpreter);
        //<editor-fold defaultstate="collapsed" desc="init">
        this.methods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 1, args -> {
            throw new RuntimeError("Unsupported Operation. Use built-in function open() instead.");
        }));
//</editor-fold>
    }

    static boolean isFile(KodeInstance i) {
        return instanceOf(i.klass, ValueFile.class);
    }

}
