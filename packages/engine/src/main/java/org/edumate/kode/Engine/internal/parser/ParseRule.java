package org.edumate.kode.Engine.internal.parser;

public class ParseRule {
    public final Runnable prefix;
    public final Runnable infix;
    public final int precedence;

    public ParseRule(Runnable prefix, Runnable infix, int precedence) {
        this.prefix = prefix;
        this.infix = infix;
        this.precedence = precedence;
    }
}
