package org.edumate.kode.Engine.internal.runtime;

import java.util.Iterator;

public class PropertyMap implements Iterable<Object> {

    private final transient PropertyHashMap map = new PropertyHashMap();

    @Override
    public Iterator<Object> iterator() {
        return map.keySet().iterator();
    }
}
