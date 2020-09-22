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

import java.util.HashMap;

/**
 *
 * @author dell
 */
abstract class Value extends KodeClass {

    Value(String name, Interpreter interpreter) {
        this(name, null, interpreter);
    }

    Value(String name, KodeClass superclass, Interpreter interpreter) {
        super(name, superclass, new HashMap<>(), interpreter);
        this.__doc__ = doc();
    }
    
    String doc(){
        return null; 
    }

    final static boolean instanceOf(KodeClass i, Class<?> c) {
        if (i == null) {
            return false;
        }
        if (c.isInstance(i)) {
            return true;
        }
        if (i.superclass != null) {
            return instanceOf(i.superclass, c);
        }
        return false;
    }

}
