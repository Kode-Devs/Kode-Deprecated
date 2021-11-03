package org.edumate.kode.Engine;

import org.edumate.kode.Engine.internal.Debugger;

public class Main {
    public static void main(String[] args) {
        final String source = "print 1 + (2 + 3);";

        System.out.println("Source: " + source);

        System.out.println("\nTokens\n<---------------------------------------->");
        Debugger.debugLexer(source);

        System.out.println("\nInstructions\n<---------------------------------------->");
        Debugger.debugCompiler(source);

        System.out.println("\nExecution\n<---------------------------------------->");
        Debugger.debugVM(source);
    }
}
