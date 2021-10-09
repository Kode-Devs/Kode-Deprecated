package org.edumate.kode.Engine.internal;

import org.edumate.kode.Engine.internal.enums.TokenType;
import org.edumate.kode.Engine.internal.parser.Lexer;
import org.edumate.kode.Engine.internal.parser.Token;

public class Debugger {
    public static void debugTokenStream(final String source){
        final Lexer lexer = new Lexer(source);
        int line = -1;
        for (;;) {
            final Token token = lexer.scanTokenOnDemand();
            if (token.line != line) {
                System.out.printf("%4d ", token.line);
                line = token.line;
            } else {
                System.out.printf("   | ");
            }
            System.out.printf("%2d '%s'\n", token.type.ordinal(), source.substring(token.start, token.start + token.length));

            if (token.type == TokenType.TOKEN_EOF) break;
        }
    }
}
