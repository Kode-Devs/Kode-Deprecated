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
package kni;

import java.util.Map;

/**
 * Kode Method
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
public abstract class MethodDef {

    public final String doc;
    public final String[] params_name;

    public MethodDef(String doc, String... params_name) {
        this.params_name = params_name;
        this.doc = doc;
    }

    public abstract KodeObject call(Map<String, KodeObject> env);
}
