/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edumate.kode.tools;

import ch.obermuhlner.scriptengine.jshell.JShellScriptEngineFactory;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 *
 * @author dell
 */
public class NewClass {

    @Deprecated
    public static void main(String[] args) {
        try {
            ScriptEngine engine = new JShellScriptEngineFactory().getScriptEngine();
            Object eval = engine.eval("System.out.println(\"Hello World\");");
            if (eval != null) {
                System.out.println(eval);
            }
        } catch (ScriptException e) {
            System.err.println(e.getMessage());
        }
    }

}
