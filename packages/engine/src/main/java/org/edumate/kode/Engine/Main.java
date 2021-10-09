package org.edumate.kode.Engine;

import org.edumate.kode.Engine.internal.Debugger;

public class Main {
    public static void main(String[] args) {
        Debugger.debugTokenStream("123 + 456;\n \"hi\";");
    }
}
