/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
class JavaNative implements KodeCallable {

    final String className;
    final String methodName;
    final String path;
    final Interpreter inter;

    JavaNative(String className, String methodName, String path, Interpreter inter) {
        this.className = className;
        this.methodName = methodName;
        this.path = path != null ? path : File.separator;
        this.inter = inter;
    }

    @Override
    public List<Pair<String, Object>> arity() {
        return Arrays.asList(new Pair("params", ValueList.create(new ArrayList())).setType(TokenType.STAR));
    }

    @Override
    public Object call(Map<String, Object> arguments) {
        Object p = arguments.get("params");
        Object[] params;
        if (p instanceof KodeInstance) {
            if (ValueList.isList((KodeInstance) p)) {
                params = ValueList.toList(p).toArray();
            } else {
                throw new RuntimeError("Argument params must be of type List", null);
            }
        } else {
            throw new RuntimeError("Argument params must be of type List", null);
        }

        // Actual Code goes here
        try {
            Path temp = Paths.get(path);
            URLClassLoader urlClassLoader = new URLClassLoader(
                    new URL[]{
                        temp.toUri().toURL(),
                        Paths.get(File.separator).toUri().toURL(),
                        Paths.get(Paths.get(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toFile().getCanonicalPath(), "libs").toUri().toURL()},
                    Kode.class.getClassLoader());
            Class[] parameterTypes = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                parameterTypes[i] = Object.class;
                params[i] = inter.toJava(params[i]);
            }
            Object o = urlClassLoader.loadClass(className).getMethod(methodName, parameterTypes).invoke(null, params);
            return inter.toKodeValue(o);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | RuntimeError | MalformedURLException | URISyntaxException e) {
            throw new RuntimeError("native library file missing : " + e.getMessage(), null);
        } catch (InvocationTargetException e) {
            String message = e.getTargetException().getMessage();
            throw new RuntimeError(message != null ? message : e.getTargetException().toString(), null);
        } catch (IOException e) {
            throw new RuntimeError("native library file missing : " + e.getMessage(), null);
        }
    }

    @Override
    public String toString() {
        return "<native function '"+this.methodName+"'>";
    }

}
