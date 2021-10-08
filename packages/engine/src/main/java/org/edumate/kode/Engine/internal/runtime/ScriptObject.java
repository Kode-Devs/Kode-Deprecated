package org.edumate.kode.Engine.internal.runtime;

import java.util.concurrent.atomic.LongAdder;

public abstract class ScriptObject implements Cloneable {

    protected ScriptObject() {
        if (Context.DEBUG) {
            count.increment();
        }
    }

    @Override
    public ScriptObject clone() {
        try {
            ScriptObject clone = (ScriptObject) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * This is updated only in debug mode - counts number of {@code ScriptObject} instances created
     */
    private static LongAdder count;

    static {
        if (Context.DEBUG) {
            count = new LongAdder();
        }
    }

    /**
     * Get number of {@code ScriptObject} instances created. If not running in debug
     * mode this is always 0
     *
     * @return number of ScriptObjects created
     */
    public static long getCount() {
        return count != null ? count.longValue() : 0;
    }
}
