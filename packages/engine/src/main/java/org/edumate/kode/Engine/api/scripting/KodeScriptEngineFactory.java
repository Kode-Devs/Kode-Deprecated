package org.edumate.kode.Engine.api.scripting;

import org.edumate.kode.Engine.internal.runtime.Context;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.List;
import java.util.Objects;

/**
 * JSR-223 compliant script engine factory for Kode.
 *
 * @since 1.2.5
 */
public final class KodeScriptEngineFactory implements ScriptEngineFactory {

    @Override
    public String getEngineName() {
        return (String) getParameter(ScriptEngine.ENGINE);
    }

    @Override
    public String getEngineVersion() {
        return (String) getParameter(ScriptEngine.ENGINE_VERSION);
    }

    @Override
    public List<String> getExtensions() {
        return extensions;
    }

    @Override
    public String getLanguageName() {
        return (String) getParameter(ScriptEngine.LANGUAGE);
    }

    @Override
    public String getLanguageVersion() {
        return (String) getParameter(ScriptEngine.LANGUAGE_VERSION);
    }

    @Override
    public String getMethodCallSyntax(final String obj, final String method, final String... args) {
        final StringBuilder sb = new StringBuilder()
                .append(Objects.requireNonNull(obj)).append('.')
                .append(Objects.requireNonNull(method)).append('(');

        if (args.length > 0)
            sb.append(Objects.requireNonNull(args[0]));
        for (int i = 1; i < args.length; i++) sb.append(", ").append(Objects.requireNonNull(args[i]));
        sb.append(')');

        return sb.toString();
    }

    @Override
    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    @Override
    public List<String> getNames() {
        return names;
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return "print(" + toDisplay + ")";
    }

    @Override
    public Object getParameter(final String key) {
        switch (key) {
            case ScriptEngine.NAME:
                return "kode";
            case ScriptEngine.ENGINE:
            case ScriptEngine.LANGUAGE:
                return "Kode";
            case ScriptEngine.ENGINE_VERSION:
            case ScriptEngine.LANGUAGE_VERSION:
                return "ECMA - 262 Edition 5.1";
            case "THREADING":
                // The engine implementation is not thread-safe. Can't be
                // used to execute scripts concurrently on multiple threads.
                // INFO: Kept for future use
                return null;
            default:
                return null;
        }
    }

    @Override
    public String getProgram(String... statements) {
        Objects.requireNonNull(statements);
        final StringBuilder sb = new StringBuilder();

        for (final String statement : statements) {
            sb.append(Objects.requireNonNull(statement)).append(';');
        }

        return sb.toString();
    }

    // default options passed to Nashorn script engine
    private static final String[] DEFAULT_OPTIONS = new String[]{};

    @Override
    public ScriptEngine getScriptEngine() {
        return newEngine(DEFAULT_OPTIONS, getAppClassLoader(), null);
    }

    /**
     * Create a new Script engine initialized with the given class loader.
     *
     * @param appLoader class loader to be used as script "app" class loader.
     * @return newly created script engine.
     * @throws SecurityException if the security manager's {@code checkPermission}
     *                           denies {@code RuntimePermission("kode.setConfig")}
     */
    public ScriptEngine getScriptEngine(final ClassLoader appLoader) {
        return newEngine(DEFAULT_OPTIONS, appLoader, null);
    }

    /**
     * Create a new Script engine initialized with the given class filter.
     *
     * @param classFilter class filter to use.
     * @return newly created script engine.
     * @throws NullPointerException if {@code classFilter} is {@code null}
     * @throws SecurityException    if the security manager's {@code checkPermission}
     *                              denies {@code RuntimePermission("kode.setConfig")}
     */
    public ScriptEngine getScriptEngine(final ClassFilter classFilter) {
        return newEngine(DEFAULT_OPTIONS, getAppClassLoader(), Objects.requireNonNull(classFilter));
    }

    /**
     * Create a new Script engine initialized with the given arguments.
     *
     * @param args arguments array passed to script engine.
     * @return newly created script engine.
     * @throws NullPointerException if {@code args} is {@code null}
     * @throws SecurityException    if the security manager's {@code checkPermission}
     *                              denies {@code RuntimePermission("kode.setConfig")}
     */
    public ScriptEngine getScriptEngine(final String... args) {
        return newEngine(Objects.requireNonNull(args), getAppClassLoader(), null);
    }

    /**
     * Create a new Script engine initialized with the given arguments and the given class loader.
     *
     * @param args      arguments array passed to script engine.
     * @param appLoader class loader to be used as script "app" class loader.
     * @return newly created script engine.
     * @throws NullPointerException if {@code args} is {@code null}
     * @throws SecurityException    if the security manager's {@code checkPermission}
     *                              denies {@code RuntimePermission("kode.setConfig")}
     */
    public ScriptEngine getScriptEngine(final String[] args, final ClassLoader appLoader) {
        return newEngine(Objects.requireNonNull(args), appLoader, null);
    }

    /**
     * Create a new Script engine initialized with the given arguments, class loader and class filter.
     *
     * @param args        arguments array passed to script engine.
     * @param appLoader   class loader to be used as script "app" class loader.
     * @param classFilter class filter to use.
     * @return newly created script engine.
     * @throws NullPointerException if {@code args} or {@code classFilter} is {@code null}
     * @throws SecurityException    if the security manager's {@code checkPermission}
     *                              denies {@code RuntimePermission("kode.setConfig")}
     */
    public ScriptEngine getScriptEngine(final String[] args, final ClassLoader appLoader, final ClassFilter classFilter) {
        return newEngine(Objects.requireNonNull(args), appLoader, Objects.requireNonNull(classFilter));
    }

    private ScriptEngine newEngine(final String[] args, final ClassLoader appLoader, final ClassFilter classFilter) {
        try {
            return new KodeScriptEngine(this, args, appLoader, classFilter);
        } catch (final RuntimeException e) {
            if (Context.DEBUG)
                e.printStackTrace();
            throw e;
        }
    }

    // -- Internals only below this point

    private static final List<String> names;
    private static final List<String> mimeTypes;
    private static final List<String> extensions;

    static {
        names = immutableList(
                "Kode", "kode",
                "kde"
        );

        mimeTypes = immutableList(
                "text/x-kode"
        );

        extensions = immutableList(
                "kde"
        );
    }

    private static List<String> immutableList(final String... elements) {
        return List.of(elements);
    }

    private static ClassLoader getAppClassLoader() {
        // Revisit: script engine implementation needs the capability to
        // find the class loader of the context in which the script engine
        // is running so that classes will be found and loaded properly
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return (contextClassLoader == null) ? KodeScriptEngineFactory.class.getClassLoader() : contextClassLoader;
    }
}
