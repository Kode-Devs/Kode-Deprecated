package org.edumate.kode.Engine.api.scripting;

import javax.script.*;
import java.io.Reader;

/**
 * JSR-223 compliant script engine for Kode. Instances are not created directly, but rather returned through
 * {@link KodeScriptEngineFactory#getScriptEngine()}. Note that this engine implements the {@link Compilable} and
 * {@link Invocable} interfaces, allowing for efficient pre-compilation and repeated execution of scripts.
 *
 * @see KodeScriptEngineFactory
 * @since 1.2.5
 */
public class KodeScriptEngine extends AbstractScriptEngine implements Compilable, Invocable {

    // the factory that created this engine
    private final KodeScriptEngineFactory factory;

    KodeScriptEngine(final KodeScriptEngineFactory factory, final String[] args, final ClassLoader appLoader, final ClassFilter classFilter) {
        assert args != null : "null argument array";
        this.factory = factory;
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return factory;
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    // NotImplemented

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        return null;
    }

    @Override
    public CompiledScript compile(Reader script) throws ScriptException {
        return null;
    }

    @Override
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        return null;
    }

    @Override
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        return null;
    }

    @Override
    public <T> T getInterface(Class<T> clasz) {
        return null;
    }

    @Override
    public <T> T getInterface(Object thiz, Class<T> clasz) {
        return null;
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return null;
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        return null;
    }
}
