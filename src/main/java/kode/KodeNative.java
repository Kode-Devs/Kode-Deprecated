/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import kni.KNI;
import kni.KodeObject;

/**
 *
 * @author dell
 */
class KodeNative implements KodeCallable {

    final String className;
    final String pkg;
    final Interpreter inter;

    KodeNative(String className, String pkg, Interpreter inter) {
        this.className = className;
        this.pkg = pkg;
        this.inter = inter;
    }

    @Override
    public int arity() {
        return -1;
    }

    @Override
    public Object call(Object... arguments) {
        try {
            Object newInstance = new URLClassLoader(
                    addToList(this.pkg == null ? Paths.get("shared-lib") : Paths.get(Kode.LIBPATH, this.pkg, "shared-lib")).toArray(new URL[]{}),
                    Kode.class.getClassLoader()).loadClass(this.className).newInstance();
            KodeObject[] args = new KodeObject[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                Object get = arguments[i];
                if (get instanceof KodeInstance) {
                    if (ValueNative.isNative((KodeInstance) get)) {
                        args[i] = new KodeObject(((KodeInstance) get).data).asNative();
                        continue;
                    }
                }
                args[i] = new KodeObject(Interpreter.toJava(get));
            }
            if (newInstance instanceof KNI) {
                KodeObject call = ((KNI) newInstance).call(args);
                if (call == null) {
                    return null;
                }
                return call.isNative() ? ValueNative.create(call.get()) : this.inter.toKodeValue(call.get());
            }
            throw new Exception("The Class loaded does not implement Kode Native Interface (KNI).");
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeError(e.getMessage());
        }
    }

    private List<URL> addToList(Path path) {
        List<URL> urls = new ArrayList();
        try {
            urls.add(path.toUri().toURL());
        } catch (Throwable e) {
        }
        File[] listFiles = path.toFile().listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                try {
                    if (file.getName().endsWith(".jar")) {
                        urls.add(file.toURI().toURL());
                    }
                } catch (Throwable e) {
                }
            }
        }
        return urls;
    }

    @Override
    public String toString() {
        return "<native '" + this.className + "'>";
    }

}
