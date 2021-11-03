package org.edumate.kode.Engine.internal.runtime;

import java.util.HashMap;
import java.util.Map;

public class PropertyMap {

    private final transient Map<String, Property> properties = new HashMap<>();

    public void define(final Object key, final ScriptObject value) {
        final String propName = key.toString();

        final Property newProp = new Property(propName, value);

        properties.put(propName, newProp);
    }

    public Property find(final Object key) {
        return properties.get(key.toString());
    }
}
