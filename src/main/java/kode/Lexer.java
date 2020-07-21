/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kode.TokenType.*;
import math.KodeNumber;
import utils.TextUtils;

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
        KEYWORDS.put(Kode.INFINITY, INFINITY);
        KEYWORDS.put(Kode.NAN, NAN);
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
        KEYWORDS.put("try", TRY);
        KEYWORDS.put("except", CATCH);
        KEYWORDS.put("raise", RAISE);
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
        addToken(EOF);
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
            // Double Character Lexemes
            case '*':
                addToken(match('*') ? POWER : STAR);
                break;
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
            case '`':
                multilineString();
                break;
            // Error Handling
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    error(fn, line, "Unexpected character '" + c + "'.");
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
        tokens.add(new Token(type, source.substring(start, current), literal, line, getLine(line), fn));
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
                break;
            }

            text += advance();
        }

        // Unterminated string.
        if (!match(quote)) {
            error(fn, line, "Unterminated string.");
            return;
        }

        //Processing
        try {
            text = TextUtils.translateEscapes(text);
        } catch (IllegalArgumentException e) {
            error(fn, line, e.getMessage());
            return;
        }

        addToken(STRING, text);
    }

    private void multilineString() {
        String text = "";

        while (!isAtEnd()) {
            if (peek() == '`') {
                break;
            }

            if (peek() == '\n') {
                line++;
            }

            text += advance();
        }

        // Unterminated string.
        if (!match('`')) {
            error(fn, line, "Unterminated multi-line string.");
            return;
        }

        //Processing
        text = text.stripIndent();
        if (text.startsWith("\n")) {
            text = text.substring(1);
        }
        if (text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }
        try {
            text = TextUtils.translateEscapes(text);
        } catch (IllegalArgumentException e) {
            error(fn, line, e.getMessage());
            return;
        }

        addToken(MLSTRING, text);
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
        addToken(NUMBER, KodeNumber.valueOf(source.substring(start, current)));
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

    void error(String fn, int line, String message) {
        throw new RuntimeError(message, new Token(EOF, source.substring(start, current), null, line, getLine(line), fn));
    }

}
