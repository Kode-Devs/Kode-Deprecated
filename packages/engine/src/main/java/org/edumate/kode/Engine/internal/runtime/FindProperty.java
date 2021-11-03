package org.edumate.kode.Engine.internal.runtime;

public class FindProperty {

    private final ScriptObject thiz;
    private final ScriptObject runtimeType;
    private final Property property;

    public FindProperty(ScriptObject thiz, ScriptObject runtimeType, final Property property){
        this.thiz = thiz;
        this.runtimeType = runtimeType;
        this.property = property;
    }
}
