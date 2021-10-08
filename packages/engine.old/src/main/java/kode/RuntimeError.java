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

import java.util.Stack;

/**
 * Runtime Error
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
class RuntimeError extends Error {

    private static final long serialVersionUID = 1L;
    Stack<Token> token = new Stack<>();
    KodeInstance instance;

    RuntimeError(String message, Token token) {
        this(ValueError.create(message));
        this.token.add(token);
    }

    RuntimeError(String message) {
        this(message, null);
    }

    RuntimeError(KodeInstance instance) {
        this.instance = instance;
    }

    @Override
    public String getMessage() {
        return this.instance.klass.class_name + ": " + this.instance;
    }

    @Override
    public String getLocalizedMessage() {
        return this.instance.toString();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

}
