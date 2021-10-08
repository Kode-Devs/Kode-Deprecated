package kode;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KodeEngineFactory implements ScriptEngineFactory {
    @Override
    public String getEngineName() {
        return getLanguageName() + " Script Engine";
    }

    @Override
    public String getEngineVersion() {
        return getLanguageVersion();
    }

    @Override
    public List<String> getExtensions() {
        return List.of(Kode.EXTENSION);
    }

    @Override
    public List<String> getMimeTypes() {
        return List.of("text/x-kode");
    }

    @Override
    public List<String> getNames() {
        final var names = new ArrayList<String>();
        names.add(getLanguageName());
        names.add(getLanguageName().toLowerCase());
        names.addAll(getExtensions());
        return Collections.unmodifiableList(names);
    }

    @Override
    public String getLanguageName() {
        return Kode.NAME;
    }

    @Override
    public String getLanguageVersion() {
        return Kode.VERSION;
    }

    @Override
    public Object getParameter(String key) {
        switch (key) {
            case ScriptEngine.ENGINE:
                return getEngineName();
            case ScriptEngine.ENGINE_VERSION:
                return getEngineVersion();
            case ScriptEngine.LANGUAGE:
                return getLanguageName();
            case ScriptEngine.LANGUAGE_VERSION:
                return getLanguageVersion();
            case ScriptEngine.NAME:
                return getNames().get(0);
            default:
                return null;
        }
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        StringBuilder s = new StringBuilder();
        s.append(obj);
        s.append(".");
        s.append(m);
        s.append("(");
        for (int i = 0; i < args.length; i++) {
            if(i > 0) {
                s.append(",");
            }
            s.append(args[i]);
        }
        s.append(")");
        return s.toString();
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return "print(" + toDisplay + ")";
    }

    @Override
    public String getProgram(String... statements) {
        StringBuilder s = new StringBuilder();
        for(String statement: statements){
            s.append(statement);
            s.append(";\n");
        }
        return s.toString();
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new KodeEngine();
    }
}
