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
    //    LSHIFT, RSHIFT, LTSHIFT, RTSHIFT,
    //
    // Literals.
    IDENTIFIER, STRING, NUMBER, MLSTRING,
    //
    // Keywords.
    AND, BREAK, CLASS, CONTINUE, ELSE, FALSE, FROM, FUN, FOR, IF, NONE, INFINITY, NAN, OR,
    IMPORT, AS, RETURN, SUPER, THIS, TRUE, VAR, WHILE, NATIVE, TRY, CATCH, RAISE,
    //
    EOF
}
