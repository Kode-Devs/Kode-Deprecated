package org.edumate.kode.Engine.internal.enums;


import static org.edumate.kode.Engine.internal.enums.TokenKind.*;

/**
 * Description of all the tokens.
 */
public enum TokenType {

    // Single-character tokens.
    LEFT_PAREN(BRACKET, "("),
    RIGHT_PAREN(BRACKET, ")"),
    LEFT_BRACE(BRACKET, "{"),
    RIGHT_BRACE(BRACKET, "}"),
    LEFT_SQUARE(BRACKET, "["),
    RIGHT_SQUARE(BRACKET, "]"),

    COMMA(SPECIAL, ","),
    DOT(SPECIAL, "."),
    SEMICOLON(SPECIAL, ";"),

    MINUS(BINARY, "-"),
    PLUS(BINARY, "+"),
    SLASH(BINARY, "/"),
    STAR(BINARY, "*"),
    PERCENT(BINARY, "%"),
    BACKSLASH(BINARY, "\\"),
    POWER(BINARY, "^"),

    // One or two character tokens.
    BANG(UNARY, "!"),
    BANG_EQUAL(BINARY, "!="),
    EQUAL(BINARY, "="),
    EQUAL_EQUAL(BINARY, "=="),
    GREATER(BINARY, ">"),
    GREATER_EQUAL(BINARY, ">="),
    LESS(BINARY, "<"),
    LESS_EQUAL(BINARY, "<="),
    LSHIFT(BINARY, "<<"),
    RSHIFT(BINARY, ">>"),

    // Literals.
    IDENTIFIER(LITERAL, null),
    STRING(LITERAL, null),
    NUMBER(LITERAL, null),
    MLSTRING(LITERAL, "multi-line string"),
    TRUE(LITERAL, "True"),
    FALSE(LITERAL, "False"),
    NONE(LITERAL, "None"),
    INFINITY(LITERAL, "Infinity"),
    NAN(LITERAL, "NaN"),

    // Keywords.
    AND(KEYWORD, null),
    BREAK(KEYWORD, null),
    CLASS(KEYWORD, null),
    CONTINUE(KEYWORD, null),
    ELSE(KEYWORD, null),
    FROM(KEYWORD, null),
    FUN(KEYWORD, "function"),
    FOR(KEYWORD, null),
    IF(KEYWORD, null),
    OR(KEYWORD, null),
    IMPORT(KEYWORD, null),
    AS(KEYWORD, null),
    RETURN(KEYWORD, null),
    SUPER(KEYWORD, null),
    THIS(KEYWORD, null),
    VAR(KEYWORD, null),
    WHILE(KEYWORD, null),
    NATIVE(KEYWORD, null),
    TRY(KEYWORD, null),
    EXCEPT(KEYWORD, null),
    RAISE(KEYWORD, null),

    EOF(SPECIAL, null),
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
