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
 *
 * @author dell
 */
class Token {

    final TokenType type;
    final String lexeme;
    final Object literal;
    final String line_text;
    final int line;
    final String fn;

    Token(TokenType type, String lexeme, Object literal, int line,String line_text,String fn) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.line_text = line_text;
        this.fn = fn;
    }

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
