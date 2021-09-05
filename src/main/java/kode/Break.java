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
 * Throwing instance of this class represents that {@code break} statement has
 * been called inside a loop.
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
class Break extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Generates a throwable instance of the {@link Break} class, representing
     * {@code break} statement call.
     *
     * @see Break
     * @see Stmt.Break
     */
    Break() {
        super("Cannot break from top-level code.", null, false, false);
    }
}
