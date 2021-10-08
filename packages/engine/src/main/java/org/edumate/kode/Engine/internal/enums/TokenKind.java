package org.edumate.kode.Engine.internal.enums;

/**
 * Classification of token types.
 */
public enum TokenKind {
    SPECIAL,    // Error, EOF ...
    UNARY,      // Unary operators.
    BINARY,     // Binary operators.
    BRACKET,    // [] () {}
    KEYWORD,    // String recognized as a keyword.
    LITERAL,    // Literal constant.
    FUTURE,     // Token reserved for future usage.
}
