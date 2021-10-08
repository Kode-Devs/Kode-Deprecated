package org.edumate.kode.Engine.api.scripting;

/**
 * Class filter (optional) to be used by script engine.
 *
 * @since 1.2.5
 */
public interface ClassFilter {
    /**
     * Should the Java class of the specified name be exposed to script?
     *
     * @param className is the fully qualified name of the java class being
     *                  checked. This will not be null. Only non-array class names will be
     *                  passed.
     * @return true if the java class can be exposed to script false otherwise
     */
    boolean exposeToScripts(String className);
}
