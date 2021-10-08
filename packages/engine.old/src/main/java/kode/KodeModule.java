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

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import kni.KNI;
import kni.MethodDef;
import kni.KodeObject;
import utils.IO;
import utils.Pip4kode;

/**
 * Module
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
class KodeModule implements ExtKodeObject {

    String name;
    Interpreter inter = new Interpreter();
    private final String path;
    String __doc__;

    KodeModule(String name, String path) {
        this.name = name;
        this.path = path;
    }

    /**
     * Utility function to search and execute any file from the system. It first
     * scans for the file relative to the working directory, then to the package
     * directory and finally in the builtin directory. It also checks for the
     * availability of the package and its update, and hence downloads using
     * {@link Pip4kode} if necessary.
     *
     * @implSpec The interpreter must have permissions to write or modify files
     * in its installation location for its proper working. Else the
     * download/update feature will not work.
     * @see Kode#run
     */
    void runModule() {
        String pkg_name = Paths.get(path).getName(0).toString();
        String initial_path = Paths.get(Kode.LIB_PATH, "package-" + pkg_name).toAbsolutePath().toString();
        try {
            // Check w.r.t working directory.
            Path temp_path = Paths.get("./", path + "." + Kode.EXTENSION).toAbsolutePath();
            if (temp_path.toFile().exists()) {
                byte[] bytes = Files.readAllBytes(temp_path);
                this.__doc__ = Kode.run(temp_path.toFile().getName(), new String(bytes, Kode.ENCODING), inter).item1;
                return;
            }

            // Check for availability of update for the package.
            if (Pip4kode.checkUpdate(pkg_name, initial_path)) {
                IO.printf_err("[Info]: Package '" + pkg_name + "' needs an update.\n"
                        + "Do you want to update the package '" + pkg_name + "' ? [y/n] ");
                if (IO.scanf().equalsIgnoreCase("y")) {
                    throw new Exception(); // Accepted for update
                }
            }

            // Check w.r.t package directory.
            temp_path = Paths.get(initial_path, path + "." + Kode.EXTENSION).toAbsolutePath();
            if (temp_path.toFile().exists()) {
                this.__doc__ = Kode.run(temp_path.toFile().getName(), Files.readString(temp_path, Kode.ENCODING), inter).item1;
                return;
            }

            // Check w.r.t built-in directory.
            InputStream file = Kode.class.getResourceAsStream("/" + path + "." + Kode.EXTENSION);
            if (file != null) {
                this.__doc__ = Kode.run(path + "." + Kode.EXTENSION, new String(file.readAllBytes(), Kode.ENCODING), inter).item1;
                return;
            }

            // Check w.r.t native library.
            if (searchNativeFile(initial_path)) {
                return;
            }

            // Not Found.
            IO.printfln_err("[Info]: Library file '" + name + "' not found in your device.");
            throw new Exception();
        } catch (Exception e) {
            // Download from online repository to local system.
            try {
                Pip4kode pip = new Pip4kode(pkg_name);
                IO.printfln("Reading package metadata from repository ...");
                pip.init(initial_path);
                IO.printf("Do you want to download the package '" + pip.pkg + "' (" + pip.sizeInWords + ") ? [y/n] ");
                if (!IO.scanf().equalsIgnoreCase("y")) {
                    throw new Exception();
                }
                if (pip.download()) {
                    IO.printfln("Download Finished");
                    runModule(); // Re-run Module Scan On Download Completion
                    return;
                } else {
                    IO.printfln_err("Download Failed");
                }
            } catch (Pip4kode.PipError ex) {
                throw new RuntimeError(ex.getMessage());
            } catch (RuntimeError ex) {
                throw ex;
            } catch (Throwable ignored) {
            }
            throw new RuntimeError("Requirement '" + name + "' not satisfied.");
        }
    }

    private boolean searchNativeFile(String initial_path) {
        try {
            ArrayList<URL> urls = new ArrayList<>();
            urls.addAll(KodeModule.listURLs(Paths.get("shared-lib")));
            urls.addAll(KodeModule.listURLs(Paths.get(initial_path, "shared-lib")));
            URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[]{}), Kode.class.getClassLoader());
            Class<?> klass = Class.forName(name, true, urlClassLoader);
            KNI annotation = klass.getAnnotation(KNI.class);
            if (annotation == null) {
                return false;
            } else {
                this.__doc__ = annotation.doc();
                for (Field f : klass.getFields()) {
                    try {
                        String f_name = f.getName();
                        if (!f_name.matches("^[A-Za-z_][A-Za-z0-9_]*$")) {
                            continue;
                        }
                        Object value = f.get(null);
                        if (value instanceof MethodDef) {
                            final MethodDef md = (MethodDef) value;
                            inter.globals.define(f_name, new KodeBuiltinFunction(f_name, inter, md.doc,
                                    md.params_name.length == 0 ? md.params_name.length
                                            : md.params_name.length * (md.params_name[md.params_name.length - 1].equals(Kode.VARARGIN) ? -1 : 1),
                                    arguments -> {
                                        Map<String, KodeObject> env = new HashMap<>();
                                        if (md.params_name.length != 0) {
                                            for (int i = 0; i < md.params_name.length - 1; i++) {
                                                env.put(md.params_name[i], arguments[i]);
                                            }
                                            if (md.params_name[md.params_name.length - 1].equals(Kode.VARARGIN)) {
                                                List<KodeObject> varargin = new ArrayList<>(Arrays.asList(arguments).subList(md.params_name.length - 1, arguments.length));
                                                env.put(Kode.VARARGIN, ValueList.create(varargin));
                                            } else {
                                                env.put(md.params_name[md.params_name.length - 1], arguments[md.params_name.length - 1]);
                                            }
                                        }
                                        return md.call(env);
                                    }));
                        } else {
                            inter.globals.define(f_name, Interpreter.toKodeValue(value));
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
            urlClassLoader.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Static utility method to list out URLs of all .jar files in the directory
     * denoted by the argument {@literal path}, including URL to the path itself
     * as the first URL.
     *
     * @param path Path to the directory.
     * @return List of associated URLs.
     */
    private static List<URL> listURLs(Path path) {
        List<URL> urls = new ArrayList<>();
        try {
            urls.add(path.toUri().toURL());
        } catch (Throwable ignored) {
        }
        File[] listFiles = path.toFile().listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                try {
                    if (file.getName().endsWith(".jar")) {
                        urls.add(file.toURI().toURL());
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return urls;
    }

    @Override
    public String toString() {
        return "<module '" + this.name + "'>";
    }

    @Override
    public KodeObject call(KodeObject... args) {
        throw new RuntimeError("Not supported yet.");
    }

    @Override
    public KodeObject get(String name) {
        try {
            return inter.globals.get(name);
        } catch (RuntimeError ex) {
            throw new RuntimeError("Module '" + this.name + "' has no attribute '" + name + "'.");
        }
    }

    @Override
    public void set(String name, KodeObject value) {
        try {
            inter.globals.assign(name, value);
        } catch (RuntimeError ex) {
            throw new RuntimeError("Module '" + this.name + "' has no attribute '" + name + "'.");
        }
    }
}
