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

import java.util.Map;

import kni.KNI;
import kni.KodeObject;
import kni.MethodDef;

/**
 * Builtin class for math.kde library.
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
@KNI(doc = "Math Built-in Lib")
public class _math {

    public static int i = 6;

    public static MethodDef log = new MethodDef(null, "a") {
        @Override
        public KodeObject call(Map<String, KodeObject> env) {
            return null;
        }

    };
}
