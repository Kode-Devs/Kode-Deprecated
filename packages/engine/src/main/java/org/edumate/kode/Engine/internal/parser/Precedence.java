package org.edumate.kode.Engine.internal.parser;

public final class Precedence {
    public static final int PREC_NONE = 0;
    public static final int PREC_ASSIGNMENT = 1;  // =
    public static final int PREC_OR = 2;          // or
    public static final int PREC_AND = 3;         // and
    public static final int PREC_EQUALITY = 4;    // == !=
    public static final int PREC_COMPARISON = 5;  // < > <= >=
    public static final int PREC_TERM = 6;        // + -
    public static final int PREC_FACTOR = 7;      // * /
    public static final int PREC_UNARY = 8;       // ! -
    public static final int PREC_CALL = 9;        // . ()
    public static final int PREC_PRIMARY = 10;

    private Precedence() {
    }
}
