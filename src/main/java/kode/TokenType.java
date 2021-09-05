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
 * Token Type
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
enum TokenType {
    //
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, LEFT_SQUARE, RIGHT_SQUARE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, PERCENT, BACKSLASH, POWER,
    //
    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,
    LSHIFT, RSHIFT,
    //
    // Literals.
    IDENTIFIER, STRING, NUMBER, MLSTRING,
    //
    // Keywords.
    AND, BREAK, CLASS, CONTINUE, ELSE, FALSE, FROM, FUN, FOR, IF, NONE, INFINITY, NAN, OR,
    IMPORT, AS, RETURN, SUPER, THIS, TRUE, VAR, WHILE, NATIVE, TRY, EXCEPT, RAISE,
    //
    EOF
}
