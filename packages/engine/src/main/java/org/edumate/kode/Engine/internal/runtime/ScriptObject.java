package org.edumate.kode.Engine.internal.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScriptObject {

    private final PropertyMap _propertyMap;

    protected ScriptObject() {
        _propertyMap = new PropertyMap();
    }

    @NotNull
    public PropertyMap selfMap() {
        return _propertyMap;
    }

    @NotNull
    public ScriptObject runtimeType() {
        return this;
    }

    @Nullable
    public final FindProperty findProperty(final Object key, final Boolean deep) {
        final PropertyMap selfMap = this.selfMap();
        final Property currentProperty = selfMap.find(key);
        if (currentProperty != null) {
            return new FindProperty(this, this.runtimeType(), currentProperty);
        }

        if (deep) {
            final ScriptObject runtimeType = this.runtimeType();

            return runtimeType.findProperty(key, true);
        }

        return null;
    }

    // ------------------------------------------------------------------------------------------ temp func

    private Object value = null;

    @Deprecated
    public ScriptObject(Object value) {
        this();
        this.value = value;
    }

    @Deprecated
    protected <T> T value() {
        return (T) value;
    }

    @Deprecated
    @Override
    public String toString() {
        return value.toString();
    }
}
