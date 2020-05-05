/*
 * MIT License
 *
 * Copyright (c) 2020 Edumate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.edumate.kode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.edumate.kode.TokenType.*;

/**
 *
 * @author dell
 */
class Lexer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private final String fn;

    private static final Map<String, TokenType> KEYWORDS;
    private static final Map<String, String> ESCAPE;

    static {
        KEYWORDS = new HashMap<>();
        KEYWORDS.put("and", AND);
        KEYWORDS.put("break", BREAK);
        KEYWORDS.put("class", CLASS);
        KEYWORDS.put("continue", CONTINUE);
        KEYWORDS.put("else", ELSE);
        KEYWORDS.put("False", FALSE);
        KEYWORDS.put("for", FOR);
        KEYWORDS.put("from", FROM);
        KEYWORDS.put("fun", FUN);
        KEYWORDS.put("if", IF);
        KEYWORDS.put("None", NONE);
        KEYWORDS.put("native", NATIVE);
        KEYWORDS.put("or", OR);
        KEYWORDS.put("import", IMPORT);
        KEYWORDS.put("as", AS);
        KEYWORDS.put("return", RETURN);
        KEYWORDS.put("super", SUPER);
        KEYWORDS.put("this", THIS);
        KEYWORDS.put("True", TRUE);
        KEYWORDS.put("var", VAR);
        KEYWORDS.put("while", WHILE);
    }

    static {
        ESCAPE = new HashMap();
        ESCAPE.put("n", "\n");
        ESCAPE.put("t", "\t");
        ESCAPE.put("r", "\r");
        ESCAPE.put("b", "\b");
    }

    Lexer(String fn, String source) {
        this.fn = fn;
        this.source = source;
    }

    private String getLine(int line) {
        return source.split("\n")[line - 1];
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line, null, fn));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            // Single Char Lexemes
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case '[':
                addToken(LEFT_SQUARE);
                break;
            case ']':
                addToken(RIGHT_SQUARE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case '%':
                addToken(PERCENT);
                break;
            case '\\':
                addToken(BACKSLASH);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            // Double Character Lexemes
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            // Longer Lexemes
            case '/':
                if (match('/')) {
                    // A comment goes until the end of line.
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else if (match('*')) {
                    // A comment goes until the '*' followed by '/'
                    while (!isAtEnd()) {
                        if (peek() == '*' && peekNext() == '/') {
                            advance(); // For '*'
                            advance(); // For '/'
                            break;
                        }
                        if (advance() == '\n') {
                            line++;
                        }
                    }
                } else {
                    addToken(SLASH);
                }
                break;
            // White Space
            case ' ':
            case '\r':
            case '\t':
                break;
            // New Line
            case '\n':
                line++;
                break;
            // String
            case '\"':
                string('\"');
                break;
            case '\'':
                string('\'');
                break;
            // Error Handling
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Kode.error(fn, line, "Unexpected character '" + c + "'.");
                }
                break;
        }
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line, getLine(line), fn));
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private void string(char quote) {
        String text = "";

        while (!isAtEnd()) {
            if (peek() == quote) {
                break;
            }

            if (peek() == '\n') {
                line++;
            }

            if (peek() == '\\') {
                advance();
                String ch = "" + advance();
                text += ESCAPE.getOrDefault(ch, ch);
                continue;
            }

            text += advance();
        }

        // Unterminated string.
        if (isAtEnd()) {
            Kode.error(fn, line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        addToken(STRING, text);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) {
                advance();
            }
        }

        // Look for a exponential part.
        if ((peek() == 'e' || peek() == 'E') && (isDigit(peekNext()) || peekNext() == '+' || peekNext() == '-')) {
            // Consume the "e"
            advance();
            if (peek() == '+' || peek() == '-') {
                advance();
            }

            while (isDigit(peek())) {
                advance();
            }
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || c == '_';
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        // See if the identifier is a reserved word.
        String text = source.substring(start, current);

        TokenType type = KEYWORDS.get(text);
        if (type == null) {
            type = IDENTIFIER;
        }
        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

}
