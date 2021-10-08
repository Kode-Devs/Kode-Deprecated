package org.edumate.kode.Engine.internal.runtime;

import javax.script.SimpleScriptContext;

/**
 * This class manages the global state of execution. Context is immutable.
 *
 * @since 1.2.5
 */
public final class Context {
    /**
     * Is Context global debug mode enabled ?
     */
    public static final boolean DEBUG = Boolean.getBoolean("kode.debug");
}
