package org.edumate.kode.Engine.internal.parser;

import org.edumate.kode.Engine.internal.enums.TokenType;

public final class Token {

    public final TokenType type;
    public final int start, length, line;

    Token(TokenType type, int start, int length, int line) {
        this.type = type;
        this.start = start;
        this.length = length;
        this.line = line;
    }
}
