/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
