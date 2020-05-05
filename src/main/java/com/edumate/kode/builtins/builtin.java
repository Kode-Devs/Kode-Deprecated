package com.edumate.kode.builtins;

import com.edumate.kode.KodeIO;

/**
 *
 * @author dell
 */
public class builtin {

    public static boolean resetLine() {
        return KodeIO.textIO.getTextTerminal().resetLine();
    }
}
