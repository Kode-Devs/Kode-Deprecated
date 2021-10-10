package org.edumate.kode.Engine.internal.enums;


import static org.edumate.kode.Engine.internal.enums.TokenKind.*;

/**
 * Description of all the tokens.
 */
public enum TokenType {

    // Single-character tokens.
    TOKEN_LEFT_PAREN(BRACKET, "("),
    TOKEN_RIGHT_PAREN(BRACKET, ")"),
    TOKEN_LEFT_BRACE(BRACKET, "{"),
    TOKEN_RIGHT_BRACE(BRACKET, "}"),
    TOKEN_LEFT_SQUARE(BRACKET, "["),
    TOKEN_RIGHT_SQUARE(BRACKET, "]"),

    TOKEN_COMMA(SPECIAL, ","),
    TOKEN_DOT(SPECIAL, "."),
    TOKEN_SEMICOLON(SPECIAL, ";"),

    TOKEN_MINUS(BINARY, "-"),
    TOKEN_PLUS(BINARY, "+"),
    TOKEN_SLASH(BINARY, "/"),
    TOKEN_STAR(BINARY, "*"),
    TOKEN_PERCENT(BINARY, "%"),
    TOKEN_BACKSLASH(BINARY, "\\"),
    TOKEN_POWER(BINARY, "^"),

    // One or two character tokens.
    TOKEN_BANG(UNARY, "!"),
    TOKEN_BANG_EQUAL(BINARY, "!="),
    TOKEN_EQUAL(BINARY, "="),
    TOKEN_EQUAL_EQUAL(BINARY, "=="),
    TOKEN_GREATER(BINARY, ">"),
    TOKEN_GREATER_EQUAL(BINARY, ">="),
    TOKEN_LESS(BINARY, "<"),
    TOKEN_LESS_EQUAL(BINARY, "<="),
    TOKEN_LEFT_SHIFT(BINARY, "<<"),
    TOKEN_RIGHT_SHIFT(BINARY, ">>"),

    // Literals.
    TOKEN_IDENTIFIER(LITERAL, null),
    TOKEN_STRING(LITERAL, null),
    TOKEN_NUMBER(LITERAL, null),
    TOKEN_ML_STRING(LITERAL, "multi-line string"),
    TOKEN_TRUE(LITERAL, "True"),
    TOKEN_FALSE(LITERAL, "False"),
    TOKEN_NONE(LITERAL, "None"),
    TOKEN_INFINITY(LITERAL, "Infinity"),
    TOKEN_NAN(LITERAL, "NaN"),

    // Keywords.
    TOKEN_AND(KEYWORD, null),
    TOKEN_BREAK(KEYWORD, null),
    TOKEN_CLASS(KEYWORD, null),
    TOKEN_CONTINUE(KEYWORD, null),
    TOKEN_ELSE(KEYWORD, null),
    TOKEN_FROM(KEYWORD, null),
    TOKEN_FUN(KEYWORD, "function"),
    TOKEN_FOR(KEYWORD, null),
    TOKEN_IF(KEYWORD, null),
    TOKEN_OR(KEYWORD, null),
    TOKEN_IMPORT(KEYWORD, null),
    TOKEN_AS(KEYWORD, null),
    TOKEN_RETURN(KEYWORD, null),
    TOKEN_SUPER(KEYWORD, null),
    TOKEN_THIS(KEYWORD, null),
    TOKEN_VAR(KEYWORD, null),
    TOKEN_WHILE(KEYWORD, null),
    TOKEN_NATIVE(KEYWORD, null),
    TOKEN_TRY(KEYWORD, null),
    TOKEN_EXCEPT(KEYWORD, null),
    TOKEN_RAISE(KEYWORD, null),
    TOKEN_PRINT(KEYWORD, null),

    // Specials
    TOKEN_ERROR(SPECIAL, null),
    TOKEN_EOF(SPECIAL, null),
    ;
    /**
     * Classification of token.
     */
    private final TokenKind kind;

    /**
     * Printable name of token, if any.
     */
    private final String name;

    /**
     * Operator precedence.
     */
    private final int precedence;

    /**
     * Left associativity
     */
    private final boolean isLeftAssociative;

    TokenType(final TokenKind kind, final String name) {
        this(kind, name, 0, false);
    }

    TokenType(final TokenKind kind, final String name, final int precedence, final boolean isLeftAssociative) {
        this.kind = kind;
        this.name = name;
        this.precedence = precedence;
        this.isLeftAssociative = isLeftAssociative;
    }

    public String getName() {
        return name;
    }

    public String getNameOrType() {
        return name == null ? super.name().toLowerCase() : name;
    }

    public TokenKind getKind() {
        return kind;
    }

    public int getPrecedence() {
        return precedence;
    }

    public boolean isLeftAssociative() {
        return isLeftAssociative;
    }

    @Override
    public String toString() {
        return getNameOrType();
    }
}
