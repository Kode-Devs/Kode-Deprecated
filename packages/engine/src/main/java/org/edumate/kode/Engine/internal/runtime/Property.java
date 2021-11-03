package org.edumate.kode.Engine.internal.runtime;

public class Property {

    private final String name;

    public Property(String name, ScriptObject value) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
