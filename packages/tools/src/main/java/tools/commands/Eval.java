package tools.commands;


import picocli.CommandLine.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Command(name = "eval", description = "Evaluate an Expression")
public class Eval implements Runnable {

    @Parameters(paramLabel = "expr", description = "Given Expression")
    String source;

    @Override
    public void run() {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("javascript");
            Object result = engine.eval(source + ";");
            System.out.println("Result: " + result);
        } catch (Throwable e) {
            System.err.println(e.getMessage());
        }
    }
}
