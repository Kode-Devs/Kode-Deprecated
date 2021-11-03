package org.edumate.kode.Engine.internal.parser;

import org.edumate.kode.Engine.internal.enums.TokenType;

import java.util.*;

public final class Lexer {
    private final String source;
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("true", TokenType.TOKEN_TRUE);
        keywords.put("false", TokenType.TOKEN_FALSE);
        keywords.put("print", TokenType.TOKEN_PRINT);
        keywords.put("none", TokenType.TOKEN_NONE);
    }

    /**
     * Creates an instance of the Lexical Analyzer for a specific snippet of
     * source code.
     *
     * @param source The snippet of the source code to be converted into Tokens.
     */
    public Lexer(final String source) {
        this.source = source;
    }

    /**
     * Scans the next most token available in the source code.
     */
    public Token scanTokenOnDemand() {
        skipWhitespace();
        start = current;

        if (isAtEnd()) return makeToken(TokenType.TOKEN_EOF);

        final char c = advance();
        if (isAlpha(c)) return identifier();
        if (isDigit(c)) return number();

        switch (c) {
            case '(':
                return makeToken(TokenType.TOKEN_LEFT_PAREN);
            case ')':
                return makeToken(TokenType.TOKEN_RIGHT_PAREN);
            case '{':
                return makeToken(TokenType.TOKEN_LEFT_BRACE);
            case '}':
                return makeToken(TokenType.TOKEN_RIGHT_BRACE);
            case ';':
                return makeToken(TokenType.TOKEN_SEMICOLON);
            case ',':
                return makeToken(TokenType.TOKEN_COMMA);
            case '.':
                return makeToken(TokenType.TOKEN_DOT);
            case '-':
                return makeToken(TokenType.TOKEN_MINUS);
            case '+':
                return makeToken(TokenType.TOKEN_PLUS);
            case '/':
                return makeToken(TokenType.TOKEN_SLASH);
            case '*':
                return makeToken(TokenType.TOKEN_STAR);
            case '!':
                return makeToken(match('=') ? TokenType.TOKEN_BANG_EQUAL : TokenType.TOKEN_BANG);
            case '=':
                return makeToken(match('=') ? TokenType.TOKEN_EQUAL_EQUAL : TokenType.TOKEN_EQUAL);
            case '<':
                return makeToken(match('=') ? TokenType.TOKEN_LESS_EQUAL : TokenType.TOKEN_LESS);
            case '>':
                return makeToken(match('=') ? TokenType.TOKEN_GREATER_EQUAL : TokenType.TOKEN_GREATER);
            case '"':
                return string();
        }

        return errorToken("Unexpected character.");
    }

    private void skipWhitespace() {
        while (true) {
            char c = peek();
            switch (c) {
                case ' ':
                case '\r':
                case '\t':
                    advance();
                    break;
                case '\n':
                    line++;
                    advance();
                    break;
                case '/':
                    if (peekNext() == '/') {
                        // A comment goes until the end of the line.
                        while (peek() != '\n' && !isAtEnd()) advance();
                    } else {
                        return;
                    }
                    break;
                default:
                    return;
            }
        }
    }

    private Token string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) return errorToken("Unterminated string.");

        // The closing quote
        advance();
        return makeToken(TokenType.TOKEN_STRING);
    }

    private Token number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the ".".
            advance();

            while (isDigit(peek())) advance();
        }

        return makeToken(TokenType.TOKEN_NUMBER);
    }

    private Token identifier() {
        while (isAlphaNumeric(peek())) advance();
        return makeToken(identifierType());
    }

    private TokenType identifierType() {
        final var identifierName = source.substring(this.start, this.current);
        if (keywords.containsKey(identifierName)) {
            return keywords.get(identifierName);
        }
        return TokenType.TOKEN_IDENTIFIER;
    }

    // ---------------------------------------------------------------------------- build fns

    /**
     * Generate a new Token.
     *
     * @param type TokenType associated with the new Token.
     * @return Returns the new Token Object
     */
    private Token makeToken(final TokenType type) {
        return new Token(type, this.start, this.current - this.start, this.line);
    }

    /**
     * Generate a new Error Token.
     *
     * @param message Error Message
     * @return Returns the new Error Token Object
     */
    private Token errorToken(final String message) {
        return makeToken(TokenType.TOKEN_ERROR);
    }

    // ---------------------------------------------------------------------------- utility fns

    /**
     * Increases the current index pointer value by 1.
     *
     * @return The character at the current index position before update.
     */
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    /**
     * Checks weather the whole source code has been scanned i.e., the current
     * index pointer is at end or not.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Matches the current character with the expected character and finally
     * consumes it if they form a match.
     *
     * @return Returns {@code true} if they form a match else {@code false}
     */
    private boolean match(final char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }

        current++;
        return true;
    }

    /**
     * Returns the current character from the source code without consuming it.
     * If the current index pointer is at end then it returns null character (
     * ASCII value 0 ).
     */
    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    /**
     * Returns the next character from the source code without consuming it. If
     * the current index pointer is at last character, or it has no next
     * character, then it returns null character ( ASCII value 0 ).
     */
    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    // ------------------------------------------------------------------

    /**
     * Checks weather the character resembles a single digit or not.
     */
    private boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Checks weather the character resembles either an alphabet or an
     * underscore, or not.
     */
    private boolean isAlpha(final char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || c == '_';
    }

    /**
     * Checks weather the character resembles either an alphabet or a single
     * digit or an underscore, or not.
     */
    private boolean isAlphaNumeric(final char c) {
        return isAlpha(c) || isDigit(c);
    }
}
