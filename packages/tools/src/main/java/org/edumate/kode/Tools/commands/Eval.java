package org.edumate.kode.Tools.commands;


import picocli.CommandLine.*;

import javax.script.*;

@Command(name = "eval", description = "Evaluate an Expression")
public class Eval implements Runnable {

//    @Parameters(paramLabel = "expr", description = "Given Expression")
//    String source;

    @Override
    public void run() {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("kode");
            Object result = engine.eval("5+1;");
            System.out.println("Result: " + result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
