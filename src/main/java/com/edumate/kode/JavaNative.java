/*
 * MIT License
 *
 * Copyright (c) 2020 Edumate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.edumate.kode;

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
        return Arrays.asList(new Pair("params", ValueList.create(new ArrayList(), inter), true));
    }

    @Override
    public Object call(Map<String, Object> arguments) {
        Object p = arguments.get("params");
        Object[] params;
        if (p instanceof List) {
            params = ((List) p).toArray();
        } else {
            throw new RuntimeError("Argument params must be of type List", null);
        }

        // Actual Code goes here
        try {
            Path temp = Paths.get(path);
            URLClassLoader urlClassLoader = new URLClassLoader(
                    //
                    //Delared paths
                    //                    new URL[]{},
                    new URL[]{
                        temp.toUri().toURL(),
                        Paths.get(File.separator).toUri().toURL(),
                        Paths.get(Paths.get(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toFile().getCanonicalPath(), "libs").toUri().toURL()},
                    //
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
        return "<built-in function 'native'>";
    }

}
