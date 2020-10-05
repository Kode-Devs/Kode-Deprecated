/* 
 * Copyright (C) 2020 Kode Devs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kode;

import kni.KodeCallable;
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
    public boolean isBind() {
        return false;
    }

    @Override
    public int arity() {
        return -1;
    }

    @Override
    public Object call(Object... arguments) {
        try {
            URLClassLoader urlClassLoader = new URLClassLoader(
                    addToList(this.pkg == null ? Paths.get("shared-lib") : Paths.get(Kode.LIBPATH, this.pkg, "shared-lib")).toArray(new URL[]{}),
                    Kode.class.getClassLoader());
            @SuppressWarnings("deprecation")
            Object newInstance = urlClassLoader.loadClass(this.className).newInstance();
            urlClassLoader.close();
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
                KodeObject result = ((KNI) newInstance).call(args);
                if (result == null) {
                    return null;
                }
                return result.isNative() ? ValueNative.create(result.get()) : this.inter.toKodeValue(result.get());
            }
            throw new Exception("The Class loaded does not implement Kode Native Interface (KNI).");
        } catch (Throwable e) {
            throw new RuntimeError(e.getMessage());
        }
    }

    private List<URL> addToList(Path path) {
        List<URL> urls = new ArrayList<>();
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
