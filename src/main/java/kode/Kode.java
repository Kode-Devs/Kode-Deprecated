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

import utils.IO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import math.KodeNumber;
import utils.Pip4kode;
import utils.Pip4kode.PipError;

/**
 * Abstract class containing the driver method for KODE interpreter.
 */
abstract class Kode {

    static final String NAME = "Kode";
    static String VERSION;
    static final String EXTENSION = "kde";
    static final String AUTHOR = "Kode Devs";
    static final String USAGE = "Usage: kode [script]";
    static final String HELP = USAGE;

    /**
     * Default file encoding for source files.
     */
    private final static Charset ENCODING = StandardCharsets.UTF_8;

    /**
     * Use to store the currentTimeMillis value whenever tic() is called. Also
     * the initial value of null denotes that tic() isn't called before.
     */
    static Long curr_time = null;

    /**
     * To generate the introduction to be displayed before the shell.
     */
    static String getIntro() {
        String res = NAME + " " + VERSION;
        try {
            res += " ( " + AUTHOR + ", built-on "
                    + new Date(new File(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI()).lastModified()) + " ) ";
        } catch (URISyntaxException e) {
        }
        res += "\nRunning on " + System.getProperty("os.name", "Unknown Operating System") + " (" + System.getProperty("user.name", "default") + ")";
        return res;
    }

    /**
     * Driver function for KODE.
     *
     * @param args Array of arguments from the command shell. First arguments
     * contains the KODE version number.
     */
    public static void main(String... args) {
        Thread.currentThread().setUncaughtExceptionHandler((Thread t, Throwable e) -> {
            IO.exit(1); // Panic State
        });
        switch (args.length) {
            case 1:
                VERSION = args[0];
                //<editor-fold defaultstate="collapsed" desc="Code for Shell">
                IO.printfln(Kode.getIntro());
                IO.printfln("Call exit() to quit the shell.");
                Interpreter interpreter = new Interpreter();
                interpreter.globals.define(Kode.__NAME__, interpreter.toKodeValue(Kode.__MAIN__));
                for (;;) {
                    try {
                        IO.printf(">>> ");
                        Pair<String, Object> run = run("<shell>", IO.scanf(), interpreter);
                        if (run.value != null) {
                            IO.printfln(Kode.repr(run.value));
                        }
                    } catch (Throwable e) {
                        handleThrowable(e);
                    }
                }
            //</editor-fold> // Executed in case of no-arg in cmd i.e., starts the shell.
            case 2:
                VERSION = args[0];
                switch (args[1]) {
                    case "-v":
                    case "--version":
                        IO.printfln(Kode.getIntro()); // Prints version info.
                        break;
                    case "-h":
                    case "--help":
                        IO.printfln(Kode.HELP); // Prints help text.
                        break;
                    default:
                        //<editor-fold defaultstate="collapsed" desc="Code for Script">
                        Path path = Paths.get(args[1]);
                        if (path.getFileName().toString().endsWith("." + Kode.EXTENSION)) {
                            try {
                                runFile(path.toAbsolutePath().toString(), new Interpreter());
                            } catch (Throwable e) {
                                handleThrowable(e);
                            }
                        } else {
                            IO.printfln_err("Not a " + Kode.NAME + " runnable file");
                            IO.exit(64);
                        }
                    //</editor-fold> // Executed in case of run as script file in cmd i.e., reads the script file and executes it.
                }
                break;
            default:
                IO.printfln_err(USAGE); // Prints usage message.
        }
        IO.exit(0);
    }

    /**
     * Whenever the interpreter detects an unhandled {@link Throwable}, it
     * passes the error instance to this function. This function prints the
     * stack-trace for KODE run-time errors else prints fatal error.
     *
     * @see runtimeError
     */
    static void handleThrowable(Throwable e) {
        if (e instanceof RuntimeError) {
            Kode.runtimeError((RuntimeError) e);
        } else {
            IO.printfln_err("Fatal Error : " + e);
        }
    }

    /**
     * Base location for packages.
     */
    static String LIBPATH;

    /**
     * The imported modules gets stored in this {@link Hashmap} so that it can
     * be re-imported when necessary.
     */
    static Map<String, KodeModule> ModuleRegistry = new HashMap<>();  // Change it

    //
    // The names of commoly used importent entities are soft coded here,
    // so that they can be easily modified.
    //
    static final String INIT = "__init__";
    static final String INIT_SUBCLASS = "__init_subclass__";
    static final String INFINITY = "Infinity";
    static final String NAN = "NaN";
    static final String VARARGIN = "varargin";
    static final String __NAME__ = "__name__";
    static final String __MAIN__ = "__main__";

    static final String GET_ITEM = "__getItem__";
    static final String SET_ITEM = "__setItem__";
    static final String CLASS = "__class__";

    static final String BUILTIN_NAME = "__builtin__";

    static final String NEG = "__neg__";
    static final String POS = "__pos__";

    static final String LEN = "__len__";
    static final String STRING = "__str__";
    static final String NUMBER = "__num__";
    static final String BOOLEAN = "__bool__";
    static final String LIST = "__list__";

    static final String ADD = "__add__";
    static final String RADD = "__radd__";

    static final String SUB = "__sub__";
    static final String RSUB = "__rsub__";

    static final String MUL = "__mul__";
    static final String RMUL = "__rmul__";

    static final String TRUE_DIV = "__truediv__";
    static final String RTRUE_DIV = "__rtruediv__";

    static final String MOD = "__mod__";
    static final String RMOD = "__rmod__";

    static final String FLOOR_DIV = "__floordiv__";
    static final String RFLOOR_DIV = "__rfloordiv__";

    static final String POWER = "__pow__";
    static final String RPOWER = "__rpow__";

    static final String EQ = "__eq__";
    static final String NE = "__ne__";
    static final String LT = "__lt__";
    static final String LE = "__le__";
    static final String GT = "__gt__";
    static final String GE = "__ge__";

    static final String LSHIFT = "__lshift__";
    static final String RLSHIFT = "__rlshift__";

    static final String RSHIFT = "__rshift__";
    static final String RRSHIFT = "__rrshift__";

    /**
     * Utility function to read the script file from the path provided and hence
     * execute it.
     *
     * @param path Path to the script file.
     * @param inter Associated interpreter.
     * @see run
     */
    static void runFile(String path, Interpreter inter) throws Throwable {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        inter.globals.define(Kode.__NAME__, inter.toKodeValue(Kode.__MAIN__));
        run(Paths.get(path).toFile().getName(), new String(bytes, ENCODING), inter);
    }

    /**
     * Utility function to import any file from the package library. It first
     * scans for the file relative to the working directory, then to the package
     * directory and finally in the builtin directory. It also checks for the
     * availability of the package and its update, and hence downloads using
     * {@link Pip4kode} if necessary.
     *
     * @implSpec The interpreter must have permissions to write or modify files
     * in its installation location for its proper working. Else the
     * download/update feature will not work.
     *
     * @param name Relative file path as per the import statement.
     * @param inter Associated interpreter.
     * @return The help text associated with the script file which is being
     * imported.
     * @throws Throwable
     * @see run
     */
    static String runLib(String name, Interpreter inter) throws Throwable {
        String pkgname = Paths.get(name).getName(0).toString();
        String initial_path = Paths.get(Kode.LIBPATH, pkgname).toAbsolutePath().toString();
        try {
            // Check w.r.t working directory.
            Path path = Paths.get("./", name + "." + Kode.EXTENSION).toAbsolutePath();
            if (path.toFile().exists()) {
                byte[] bytes = Files.readAllBytes(path);
                return run(path.toFile().getName(), new String(bytes, ENCODING), inter).key;
            }

            // Check for avialibility of update for the package.
            if (Pip4kode.checkUpdate(pkgname, initial_path)) {
                IO.printf_err("[Info]: Package '" + pkgname + "' needs an update.\n"
                        + "Do you want to update the package '" + pkgname + "' ? [y/n] ");
                if (IO.scanf().equalsIgnoreCase("y")) {
                    throw new Exception();
                }
            }

            // Check w.r.t package directory.
            path = Paths.get(initial_path, name + "." + Kode.EXTENSION).toAbsolutePath();
            if (path.toFile().exists()) {
                byte[] bytes = Files.readAllBytes(path);
                return run(path.toFile().getName(), new String(bytes, ENCODING), inter).key;
            }

            // Check w.r.t built-in directory.
            byte[] bytes;
            InputStream file = Kode.class.getResourceAsStream("/" + name + "." + Kode.EXTENSION);
            if (file != null) {
                bytes = file.readAllBytes();
                return run(name + "." + Kode.EXTENSION, new String(bytes, ENCODING), inter).key;
            }

            // Not Found.
            IO.printfln_err("[Info]: Library file " + name + "." + Kode.EXTENSION + " not found in your device.");
            throw new Exception();
        } catch (Exception e) {
            // Download from online reposerity to local system.
            try {
                Pip4kode pip = new Pip4kode(pkgname);
                IO.printfln("Reading package metadata from repository ...");
                pip.init(initial_path);
                IO.printf("Do you want to download the package '" + pip.pkg + "' (" + pip.sizeInWords + ") ? [y/n] ");
                if (!IO.scanf().equalsIgnoreCase("y")) {
                    throw new Exception();
                }
                if (pip.download()) {
                    IO.printfln("Download Finished");
                    return runLib(name, inter);
                } else {
                    IO.printfln_err("Download Failed");
                }
            } catch (PipError ex) {
                throw new RuntimeError(ex.getMessage());
            } catch (RuntimeError ex) {
                throw ex;
            } catch (Throwable ex) {
            }
            throw new RuntimeError("Requirement '" + name + "' not satisfied.");
        }
    }

    /**
     * This function performs the actual execution of the source code.
     *
     * @param fn Source file name.
     * @param source Actual source code.
     * @param inter Associated interpreter.
     * @return A Pair instance whose key is the help text and value is the last
     * output value generated.
     * @throws Throwable
     */
    static Pair<String, Object> run(String fn, String source, Interpreter inter) throws Throwable {
        List<Token> tokens = new Lexer(fn, source).scanTokens(); // Lexical Analysis.
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse(); // Syntax Analysis.
        new Resolver(inter).resolve(statements); // Sentimental Analysis.
        return new Pair<>(parser.doc, inter.interpret(statements)); // Interpretation.
    }

    /**
     * Utility function to print warning message.
     *
     * @param message Message to be printed.
     */
    static void warning(String message) {
        IO.printfln_err("[Warning]: " + message);
    }

    /**
     * Utility function to print stack-trace for any error instance.
     *
     * @see handleThrowable
     */
    static void runtimeError(RuntimeError error) {
        error.token.removeIf(a -> a == null);
        Collections.reverse(error.token);
        for (BigInteger i = BigInteger.ZERO; i.compareTo(
                error.instance.data instanceof BigInteger ? (BigInteger) error.instance.data : BigInteger.ZERO
        ) < 0 && !error.token.empty(); i = i.add(BigInteger.ONE)) {
            error.token.pop();
        }
        if (!error.token.empty()) {
            IO.printfln_err("Traceback (most recent call last):");
        }
        error.token.forEach((line) -> {
            IO.printfln_err("  File '" + line.fn + "', line " + line.line + ", near '" + line.lexeme + "'");
            IO.printfln_err("    " + line.line_text.trim());
        });
        IO.printfln_err(error.getMessage());
    }

    /**
     * Very useful utility function to convert any {@link Object} to
     * {@link String}.
     *
     * @param object The object which needs to be converted.
     * @return Printable string format of the object.
     * @see toString
     * @see repr
     */
    static String stringify(Object object) {

        // None
        if (object == null) {
            return "None";
        }

        // Bool
        if (object instanceof Boolean) {
            if ((Boolean) object) {
                return "True";
            }
            return "False";
        }

        // Number
        if (object instanceof KodeNumber) {
            if (((KodeNumber) object).isInteger()) {
                return ((KodeNumber) object).getInteger().toString();
            } else {
                Double num = ((KodeNumber) object).getFloat();
                if (num == Double.POSITIVE_INFINITY) {
                    return Kode.INFINITY;
                }
                if (num == Double.NEGATIVE_INFINITY) {
                    return "-" + Kode.INFINITY;
                }
                if (num.isNaN()) {
                    return Kode.NAN;
                }
                return num.toString();

//            -- Not required now --
//            String format = String.format(Locale.US, "%.10G", num);
//            format = format
//                    .replaceFirst("\\.0+(e|$)", "$1")
//                    .replaceFirst("(\\.[0-9]*[1-9])(0+)(e|$)", "$1$3");
//            return format;
            }
        }

        // List
        if (object instanceof List) {
            List<?> list = (List<?>) object;
            if (list.isEmpty()) {
                return "[]";
            } else if (list.size() == 1) {
                return "[" + Kode.repr(list.get(0)) + "]";
            } else {
                String text = "[" + Kode.repr(list.get(0));
                for (int i = 1; i < list.size(); i++) {
                    text += ", " + Kode.repr(list.get(i));
                }
                text += "]";
                return text;
            }
        }

        // Object
        return object.toString();
    }

    /**
     * Very useful utility function similar to {@link stringify}.
     * The only difference is that it acts differently for certain cases,
     * and is used for displaying text as auto output of the last result in the Shell.
     *
     * @param object The object which needs to be converted.
     * @return Printable string format of the object.
     * @see toString
     * @see stringify
     */
    private static String repr(Object value) {
        if (value instanceof KodeInstance) {
            if (ValueString.isString((KodeInstance) value)) {
                return '\'' + Kode.stringify(value) + '\'';
            }
        }
        return Kode.stringify(value);
    }

    static String type(Object object) {

        // Function
        if (object instanceof KodeClass) {
            return "class." + ((KodeClass) object).class_name;
        }
        if (object instanceof KodeFunction) {
            if (object instanceof KodeBuiltinFunction) {
                return "builtin.function." + ((KodeBuiltinFunction) object).fun_name;
            }
            return "function." + ((KodeFunction) object).declaration.name.lexeme;
        }
        if (object instanceof KodeNative) {
            return "native.function." + ((KodeNative) object).className;
        }
        if (object instanceof KodeInstance) {
            return ((KodeInstance) object).klass.class_name;
        }
        if (object instanceof KodeModule) {
            return "module." + ((KodeModule) object).name;
        }
        return "unknown";
    }

    static boolean instanceOf(KodeInstance i, KodeClass c) {
        return instanceOf(i.klass, c);
    }

    static boolean instanceOf(KodeClass i, KodeClass c) {
        if (i == null) {
            return false;
        }
        if (i.equals(c)) {
            return true;
        }
        if (i.superclass != null) {
            return instanceOf(i.superclass, c);
        }
        return false;
    }

    static final KodeModule BUILTIN_MODULE = new KodeModule(Kode.BUILTIN_NAME, Kode.BUILTIN_NAME);

    static {
        try {
            LIBPATH = Paths.get(Paths.get(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParent().getParent().toFile().getAbsolutePath(), "libs").toAbsolutePath().toString(); // Get Parent added.
            Interpreter INTER = BUILTIN_MODULE.inter;
            Map<String, Object> DEF_GLOBALS = INTER.globals.values;

            DEF_GLOBALS.put("disp", new KodeBuiltinFunction("disp", INTER, null, 1, args -> {
                IO.printf(args[0] + "\n");
                return null;
            }));
            DEF_GLOBALS.put("sprintf", new KodeBuiltinFunction("sprintf", INTER, null, -2, args -> {
                List<Object> ll = new ArrayList<>();
                for (int i = 1; i < args.length; i++) {
                    Object toJava = Interpreter.toJava(args[i]);
                    if (toJava instanceof KodeNumber) {
                        ll.add(((KodeNumber) toJava).isInteger() ? ((KodeNumber) toJava).getInteger() : ((KodeNumber) toJava).getFloat());
                    } else {
                        ll.add(toJava);
                    }
                }
                Object temp = args[0];
                if (temp instanceof KodeInstance) {
                    if (ValueString.isString((KodeInstance) temp)) {
                        try {
                            return INTER.toKodeValue(String.format(ValueString.toStr(temp), ll.toArray()));
                        } catch (IllegalFormatException e) {
                            throw new RuntimeError("Format error -> " + e);
                        }
                    }
                }
                throw new NotImplemented();
            }
            ));
            DEF_GLOBALS.put("printf", new KodeBuiltinFunction("printf", INTER, null, -2, args -> {
                IO.printf(((KodeCallable) DEF_GLOBALS.get("sprintf")).call(args));
                return null;
            }));
            DEF_GLOBALS.put("input", new KodeBuiltinFunction("input", INTER, null, -1, args -> {
                if (args.length > 0) {
                    ((KodeCallable) DEF_GLOBALS.get("printf")).call(args);
                }
                try {
                    return INTER.toKodeValue(IO.scanf());
                } catch (IOException ex) {
                    throw new RuntimeError(ex.getMessage());
                }
            }));
            DEF_GLOBALS.put("inputPwd", new KodeBuiltinFunction("inputPwd", INTER, null, -1, args -> {
                if (args.length > 0) {
                    ((KodeCallable) DEF_GLOBALS.get("printf")).call(args);
                }
                try {
                    return INTER.toKodeValue(IO.scanf_pwd());
                } catch (IOException ex) {
                    throw new RuntimeError(ex.getMessage());
                }
            }));
            DEF_GLOBALS.put("tic", new KodeBuiltinFunction("tic", INTER, null, 0, args -> {
                Kode.curr_time = System.currentTimeMillis();
                return null;
            }));
            DEF_GLOBALS.put("toc", new KodeBuiltinFunction("toc", INTER, null, 0, args -> {
                if (Kode.curr_time == null) {
                    throw new RuntimeError("toc() before tic() is not allowed.");
                }
                IO.printfln(String.format("Elapsed time is %g seconds.", (System.currentTimeMillis() - Kode.curr_time) / 1000.0));
                return null;
            }));
            DEF_GLOBALS.put("len", new KodeBuiltinFunction("len", INTER, null, 1, args -> {
                Object obj = args[0];
                try {
                    if (obj instanceof KodeInstance) {
                        Object fun = ((KodeInstance) obj).get(Kode.LEN);
                        if (fun instanceof KodeFunction) {
                            return ((KodeFunction) fun).call();
                        }
                    }
                    throw new NotImplemented();
                } catch (NotImplemented e) {
                    throw new RuntimeError("Element of type '" + Kode.type(obj) + "' has no length.");
                }
            }));
            DEF_GLOBALS.put("dir", new KodeBuiltinFunction("dir", INTER, null, 1, args -> {
                Object obj = args[0];
                Set<String> dir = new TreeSet<>();
                if (obj instanceof KodeInstance) {
                    dir.addAll(((KodeInstance) obj).fields.keySet());
                    if (((KodeInstance) obj).klass != null) {
                        KodeClass kls = ((KodeInstance) obj).klass;
                        for (;;) {
                            if (kls != null) {
                                dir.addAll(kls.methods.keySet());
                                dir.addAll(kls.specialMethods.keySet());
                            } else {
                                break;
                            }
                            kls = kls.superclass;
                        }
                    }
                }
                if (obj instanceof KodeModule) {
                    dir.addAll(((KodeModule) obj).inter.globals.values.keySet());
                }
                return INTER.toKodeValue(Arrays.asList(dir.toArray()));
            }));
            DEF_GLOBALS.put("id", new KodeBuiltinFunction("id", INTER, null, 1, args -> {
                return INTER.toKodeValue(args[0].hashCode());
            }));
            DEF_GLOBALS.put("now", new KodeBuiltinFunction("now", INTER, null, 0, args -> {
                return INTER.toKodeValue(System.currentTimeMillis());
            }));
            DEF_GLOBALS.put("free", new KodeBuiltinFunction("free", INTER, null, 0, args -> {
                System.gc();
                return null;
            }));
            DEF_GLOBALS.put("resetLine", new KodeBuiltinFunction("resetLine", INTER, null, 0, args -> {
                IO.resetLine();
                return null;
            }));
            DEF_GLOBALS.put("clear", new KodeBuiltinFunction("clear", INTER, null, 0, args -> {
                IO.clc();
                return null;
            }));
            DEF_GLOBALS.put("isinstance", new KodeBuiltinFunction("isinstance", INTER, null, 2, args -> {
                Object object = args[0];
                Object type = args[1];
                if (!(object instanceof KodeInstance)) {
                    throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
                }
                if (!(type instanceof KodeClass)) {
                    throw new RuntimeError("Value passed for argument 'type' is not a class.");
                }
                return INTER.toKodeValue(Kode.instanceOf((KodeInstance) object, (KodeClass) type));
            }));
            DEF_GLOBALS.put("issubclass", new KodeBuiltinFunction("issubclass", INTER, null, 2, args -> {
                Object object = args[0];
                Object type = args[1];
                if (!(object instanceof KodeClass)) {
                    throw new RuntimeError("Value passed for argument 'object' is not a class.");
                }
                if (!(type instanceof KodeClass)) {
                    throw new RuntimeError("Value passed for argument 'type' is not a class.");
                }
                return INTER.toKodeValue(Kode.instanceOf((KodeClass) object, (KodeClass) type));
            }));
            DEF_GLOBALS.put("exit", new KodeBuiltinFunction("exit", INTER, null, 0, args -> {
                IO.exit(0);
                return null;
            }));
            DEF_GLOBALS.put("help", new KodeBuiltinFunction("help", INTER, null, 1, args -> {
                Object get = args[0];
                String doc = null;
                if (get instanceof KodeFunction) {
                    doc = ((KodeFunction) get).__doc__;
                }
                if (get instanceof KodeClass) {
                    doc = ((KodeClass) get).__doc__;
                }
                if (get instanceof KodeInstance) {
                    doc = ((KodeInstance) get).__doc__;
                }
                if (get instanceof KodeModule) {
                    doc = ((KodeModule) get).__doc__;
                }
                if (doc == null) {
                    doc = "No Documentation Avialable for element of type '" + Kode.type(get) + "'.";
                }
                IO.printfln(doc);
                return null;
            }));
            DEF_GLOBALS.put("hasattr", new KodeBuiltinFunction("hasattr", INTER, null, 2, args -> {
                Object object = args[0];
                if (object instanceof KodeInstance) {
                    try {
                        ((KodeInstance) object).get(args[1].toString());  //TODO change to String
                        return INTER.toKodeValue(true);
                    } catch (RuntimeError e) {
                        return INTER.toKodeValue(false);
                    }
                }
                throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
            }));
            DEF_GLOBALS.put("setattr", new KodeBuiltinFunction("setattr", INTER, null, 3, args -> {
                Object object = args[0];
                if (object instanceof KodeInstance) {
                    ((KodeInstance) object).set(args[1].toString(), args[2]);  //TODO change to String
                    return null;
                }
                throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
            }));
            DEF_GLOBALS.put("getattr", new KodeBuiltinFunction("getattr", INTER, null, 2, args -> {
                Object object = args[0];
                if (object instanceof KodeInstance) {
                    try {
                        return INTER.toKodeValue(((KodeInstance) object).get(args[1].toString()));  //TODO change to String
                    } catch (RuntimeError e) {
                        throw e;
                    }
                }
                throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
            }));
            DEF_GLOBALS.put("callable", new KodeBuiltinFunction("callable", INTER, null, 1, args -> {
                return INTER.toKodeValue(args[0] instanceof KodeCallable);
            }));
            DEF_GLOBALS.put("eval", new KodeBuiltinFunction("eval", INTER, null, 1, args -> {
                Object src = args[0];
                if (src instanceof KodeInstance) {
                    if (ValueString.isString((KodeInstance) src)) {
                        try {
                            return Kode.run("<eval>", ValueString.toStr(src), new Interpreter()).value;
                        } catch (RuntimeError ex) {
                            throw ex;
                        } catch (Throwable ex) {
                            throw new RuntimeError("Fatal Error Occured in eval(): " + ex);
                        }
                    }
                }
                throw new NotImplemented();
            }));
            DEF_GLOBALS.put("ord", new KodeBuiltinFunction("ord", INTER,
                    "\nBuiltin Function ord(ch)"
                    + "\n------------------------------"
                    + "\nIt returns the Integer Number value representation of any character."
                    + "\n"
                    + "\nparameters"
                    + "\n----------"
                    + "\nch : Character whose Integer Number value is needed."
                    + "\n"
                    + "\nreturns"
                    + "\n-------"
                    + "\nReturns the corresponding Integer Number value of the argument ch."
                    + "\n"
                    + "\nthrows"
                    + "\n------"
                    + "\nThrows Error if a String of length 1 is not passed as ch.\n",
                    1, args -> {
                        Object temp = args[0];
                        if (temp instanceof KodeInstance) {
                            if (ValueString.isString((KodeInstance) temp)) {
                                String ch = ValueString.toStr(temp);
                                if (ch.length() != 1) {
                                    throw new RuntimeError("ord() expected a Character, but " + Kode.type(temp) + " of length " + ch.length() + " found");
                                }
                                return INTER.toKodeValue((long) ch.charAt(0));
                            }
                        }
                        throw new RuntimeError("ord() expected String of length 1, but " + Kode.type(temp) + " found");
                    }));
            DEF_GLOBALS.put("chr", new KodeBuiltinFunction("chr", INTER,
                    "\nBuiltin Function chr(n)"
                    + "\n------------------------------"
                    + "\nIt returns the character representation associated with the Integer Number."
                    + "\n"
                    + "\nparameters"
                    + "\n----------"
                    + "\nn : Integer Number whose corresponding Character value is needed."
                    + "\n"
                    + "\nreturns"
                    + "\n-------"
                    + "\nReturns the corresponding Character value of the argument n."
                    + "\n"
                    + "\nthrows"
                    + "\n------"
                    + "\nThrows Error if a Integer Number within " + Long.MIN_VALUE + " to " + Long.MAX_VALUE + " is not passed.\n",
                    1, args -> {
                        Object temp = args[0];
                        if (temp instanceof KodeInstance) {
                            if (ValueNumber.isNumber((KodeInstance) temp)) {
                                KodeNumber asIndex = ValueNumber.toNumber(temp);
                                if (asIndex.isInteger()) {
                                    try {
                                        return INTER.toKodeValue((char) asIndex.getInteger().longValueExact());
                                    } catch (ArithmeticException e) {
                                        throw new RuntimeError("Integer Number Out Of Range.");
                                    }
                                }
                                throw new RuntimeError("Integer Number expected, got Decimal Number");
                            }
                        }
                        throw new RuntimeError("Integer Number expected, got " + Kode.type(temp));
                    }));
            DEF_GLOBALS.put(ValueNumber.val.class_name, ValueNumber.val);   //Number
            DEF_GLOBALS.put(ValueString.val.class_name, ValueString.val);   //String
            DEF_GLOBALS.put(ValueBool.val.class_name, ValueBool.val);   //Bool
            DEF_GLOBALS.put(ValueList.val.class_name, ValueList.val);   //List
            DEF_GLOBALS.put(ValueError.val.class_name, ValueError.val);    //Error
            DEF_GLOBALS.put(ValueType.val.class_name, ValueType.val);    //Type
            DEF_GLOBALS.put(ValueNotImplemented.val.class_name, ValueNotImplemented.val); //NotImplemented Error

            Kode.ModuleRegistry.put(Kode.BUILTIN_NAME, BUILTIN_MODULE);
            BUILTIN_MODULE.inter = INTER;
            BUILTIN_MODULE.run();
        } catch (Throwable ex) {
            handleThrowable(ex);
            JOptionPane.showMessageDialog(null, "Failed to Initialize Interpreter\nReason : " + ex.toString());
            IO.exit(1);
        }
    }
}
