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
class KodeModule implements ExtKodeObject {

    String name;
    Interpreter inter = new Interpreter();
    private final String path;
    String __doc__;

    KodeModule(String name, String path) {
        this.name = name;
        this.path = path;
    }

    void run() throws Throwable {
        this.__doc__ = Kode.runLib(path, inter);
    }

    @Override
    public String toString() {
        return "<module '" + this.name + "'>";
    }

    @Override
    public KodeObject call(KodeObject... args) {
        throw new RuntimeError("Not supported yet.");
    }

    @Override
    public KodeObject get(String name) {
        try {
            return inter.globals.get(name);
        } catch (RuntimeError ex) {
            throw new RuntimeError("Module '" + this.name + "' has no attribute '" + name + "'.");
        }
    }

    @Override
    public void set(String name, KodeObject value) {
        try {
            inter.globals.assign(name, value);
        } catch (RuntimeError ex) {
            throw new RuntimeError("Module '" + this.name + "' has no attribute '" + name + "'.");
        }
    }
}
