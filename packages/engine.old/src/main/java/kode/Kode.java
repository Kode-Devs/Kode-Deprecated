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

import kni.KodeObject;
import math.KodeNumber;
import utils.IO;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Abstract class containing the driver method for KODE interpreter.
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 * @apiNote This class contains only {@link static} fields and should not be
 * inherited to a new {@link class}.
 */
abstract class Kode {
    static String NAME = "";
    static String VERSION = "";
    static final String EXTENSION = "kde";
    static final String AUTHOR = "Kode Devs";
    static final String USAGE = "Usage: kode [script]";
    static final String HELP = USAGE;

    /**
     * Default file encoding for source files.
     */
    static final Charset ENCODING = StandardCharsets.UTF_8;

    /**
     * Used to store the currentTimeMillis value whenever tic() is called. Also,
     * the initial value of null denotes that tic() isn't called before.
     */
    static Long curr_time = null;

    static {
        Thread.currentThread().setUncaughtExceptionHandler((Thread t, Throwable e) -> {
            System.out.println("System Panicked: " + e + " [Thread " + t + "]");
            IO.exit(1); // Panic State
        });
        try {
            Properties prop = new Properties();
            prop.load(
                    new InputStreamReader(
                            Objects.requireNonNull(
                                    Kode.class.getClassLoader().getResourceAsStream("build.properties")
                            )
                    )
            );
            NAME = prop.getProperty("name");
            VERSION = prop.getProperty("version");
        } catch (Exception ignored) {
        }
    }

    /**
     * Driver function for KODE.
     *
     * @param args Array of arguments from the command shell. First arguments
     *             contains the KODE version number.
     */
    public static void main(String[] args) {
        switch (args.length) {
            case 0:
                // Executed in case of no-arg in cmd i.e., starts the shell.
                //<editor-fold defaultstate="collapsed" desc="Code for Shell">
                IO.printfln(Kode.getIntro());
                IO.printfln("Call exit() to quit the shell.");
                Interpreter interpreter = new Interpreter();
                interpreter.globals.define(Kode.__NAME__, Interpreter.toKodeValue(Kode.__MAIN__));
                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        IO.printf(">>> ");
                        Pair<String, KodeObject> run = run("<shell>", IO.scanf(), interpreter);
                        if (run.item2 != null) {
                            IO.printfln(Kode.repr(run.item2));
                        }
                    } catch (Throwable e) {
                        handleThrowable(e);
                    }
                }
                //</editor-fold>
            case 1:
                switch (args[0]) {
                    case "-v":
                    case "--version":
                        IO.printfln(Kode.getIntro()); // Prints version info.
                        break;
                    case "-h":
                    case "--help":
                        IO.printfln(Kode.HELP); // Prints help text.
                        break;
                    default:
                        // Executed in case of runModule as script file in cmd i.e., reads the script file and executes it.
                        //<editor-fold defaultstate="collapsed" desc="Code for Script">
                        Path path = Paths.get(args[0]);
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
                        //</editor-fold>
                }
                break;
            default:
                IO.printfln_err(USAGE); // Prints usage message.
        }
        IO.exit(0);
    }

    /**
     * To generate the introduction to be displayed before the shell.
     */
    static String getIntro() {
        String res = NAME + " " + VERSION;
        try {
            res += " ( " + AUTHOR + ", built-on "
                    + new Date(new File(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI()).lastModified()) + " ) ";
        } catch (URISyntaxException ignored) {
        }
        res += "\nRunning on " + System.getProperty("os.name", "Unknown Operating System") + " (" + System.getProperty("user.name", "default") + ")";
        return res;
    }

    /**
     * Whenever the interpreter detects an unhandled {@link Throwable}, it
     * passes the error instance to this function. This function prints the
     * stack-trace for KODE run-time errors else prints fatal error.
     *
     * @see Kode#runtimeError
     */
    static void handleThrowable(Throwable e) {
        if (e instanceof RuntimeError) {
            Kode.runtimeError((RuntimeError) e);
        } else {
            IO.printfln_err("Fatal Error : " + e);
            e.printStackTrace(System.out);
            IO.exit(1);
        }
    }

    /**
     * Base location for packages.
     */
    static String LIB_PATH;

    /**
     * The imported modules gets stored in this {@link HashMap} so that it can
     * be re-imported when necessary.
     */
    static Map<String, KodeModule> ModuleRegistry = new HashMap<>();  // Change it

    //
    // The names of commonly used important entities are soft coded here,
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
     * @param path  Path to the script file.
     * @param inter Associated interpreter.
     * @see Kode#run
     */
    static void runFile(String path, Interpreter inter) throws Throwable {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        inter.globals.define(Kode.__NAME__, Interpreter.toKodeValue(Kode.__MAIN__));
        run(Paths.get(path).toFile().getName(), new String(bytes, ENCODING), inter);
    }

    /**
     * This function performs the actual execution of the source code.
     *
     * @param fn     Source file name.
     * @param source Actual source code.
     * @param inter  Associated interpreter.
     * @return A Pair instance whose item1 is the help text and item2 is the
     * last output item2 generated.
     */
    static Pair<String, KodeObject> run(String fn, String source, Interpreter inter) {
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
     * Utility function to print stack-trace for any Java {@link RuntimeError}
     * instance.
     *
     * @param error Instance of Java {@link RuntimeError} class.
     * @see Kode#handleThrowable
     * @see RuntimeError
     */
    static void runtimeError(RuntimeError error) {
        error.token.removeIf(Objects::isNull);
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
     * @return Printable string format of the object.
     * @see Kode#toString
     * @see Kode#repr
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
                StringBuilder text = new StringBuilder("[" + Kode.repr(list.get(0)));
                for (int i = 1; i < list.size(); i++) {
                    text.append(", ").append(Kode.repr(list.get(i)));
                }
                text.append("]");
                return text.toString();
            }
        }

        // Object
        return object.toString();
    }

    /**
     * Very useful utility function similar to {@link Kode#stringify} but is used for
     * representation of objects. The only difference is that it acts
     * differently for certain cases, and is used for displaying text as auto
     * output of the last result in the Shell.
     *
     * @return Representable string format of the object.
     * @see Kode#toString
     * @see Kode#stringify
     */
    private static String repr(Object value) {
        if (value instanceof KodeInstance) {
            if (ValueString.isString((KodeInstance) value)) {
                return '\'' + Kode.stringify(value) + '\'';
            }
        }
        return Kode.stringify(value);
    }

    /**
     * Utility function to detect the predefined type label for any object.
     */
    static String type(KodeObject object) {
        if (object instanceof KodeClass) {
            return "class." + ((KodeClass) object).class_name;
        }
        if (object instanceof KodeFunction) {
            if (object instanceof KodeBuiltinFunction) {
                return "builtin.function." + ((KodeBuiltinFunction) object).fun_name;
            }
            return "function." + ((KodeFunction) object).declaration.name.lexeme;
        }
        if (object instanceof KodeInstance) {
            return ((KodeInstance) object).klass.class_name;
        }
        if (object instanceof KodeModule) {
            return "module." + ((KodeModule) object).name;
        }
        return "unknown";
    }

    /**
     * Returns weather an object/instance belongs to a class or not.
     */
    static boolean instanceOf(KodeInstance i, KodeClass c) {
        return equal_or_sub_class_of(i.klass, c);
    }

    /**
     * Returns weather a class is equal to or subclass of another class or not.
     */
    static boolean equal_or_sub_class_of(KodeClass i, KodeClass c) {
        if (i == null) {
            return false;
        } else if (i.equals(c)) {
            return true;
        }
        return equal_or_sub_class_of(i.superclass, c);
    }

    /**
     * Instance of the module containing the built-in methods and variables.
     */
    static final KodeModule BUILTIN_MODULE = new KodeModule(Kode.BUILTIN_NAME, Kode.BUILTIN_NAME);

    /*
     * This part contains code snippets needed to initialize the interpreted
     * including definitions of built-in elements. If it fails during this part,
     * the interpreter shows an Error dialog and finally exits its execution.
     */
    static {
        try {
            // LIB_PATH variable is initialized with the actual path equivalent to <installation_location>/libs/
            LIB_PATH = Paths.get(Paths.get(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParent().getParent().toFile().getAbsolutePath(), ".packages").toAbsolutePath().toString(); // Get Parent added.

            Interpreter INTER = BUILTIN_MODULE.inter;
            Map<String, KodeObject> DEF_GLOBALS = INTER.globals.values;

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
                            return Interpreter.toKodeValue(String.format(ValueString.toStr((KodeInstance) temp), ll.toArray()));
                        } catch (IllegalFormatException e) {
                            throw new RuntimeError("Format error -> " + e);
                        }
                    }
                }
                throw new NotImplemented();
            }
            ));
            DEF_GLOBALS.put("printf", new KodeBuiltinFunction("printf", INTER, null, -2, args -> {
                IO.printf(DEF_GLOBALS.get("sprintf").call(args));
                return null;
            }));
            DEF_GLOBALS.put("input", new KodeBuiltinFunction("input", INTER, null, -1, args -> {
                if (args.length > 0) {
                    DEF_GLOBALS.get("printf").call(args);
                }
                try {
                    return Interpreter.toKodeValue(IO.scanf());
                } catch (IOException ex) {
                    throw new RuntimeError(ex.getMessage());
                }
            }));
            DEF_GLOBALS.put("inputPwd", new KodeBuiltinFunction("inputPwd", INTER, null, -1, args -> {
                if (args.length > 0) {
                    DEF_GLOBALS.get("printf").call(args);
                }
                try {
                    return Interpreter.toKodeValue(IO.scanf_pwd());
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
                KodeObject obj = args[0];
                try {
                    if (obj instanceof KodeInstance) {
                        Object fun = obj.get(Kode.LEN);
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
                        for (; ; ) {
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
                return Interpreter.toKodeValue(Arrays.asList(dir.toArray()));
            }));
            DEF_GLOBALS.put("id", new KodeBuiltinFunction("id", INTER, null, 1, args -> Interpreter.toKodeValue(args[0].hashCode())));
            DEF_GLOBALS.put("now", new KodeBuiltinFunction("now", INTER, null, 0, args -> Interpreter.toKodeValue(System.currentTimeMillis())));
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
                return Interpreter.toKodeValue(Kode.instanceOf((KodeInstance) object, (KodeClass) type));
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
                return Interpreter.toKodeValue(Kode.equal_or_sub_class_of((KodeClass) object, (KodeClass) type));
            }));
            DEF_GLOBALS.put("exit", new KodeBuiltinFunction("exit", INTER, null, 0, args -> {
                IO.exit(0);
                return null;
            }));
            DEF_GLOBALS.put("help", new KodeBuiltinFunction("help", INTER, null, 1, args -> {
                KodeObject get = args[0];
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
                    doc = "No Documentation Available for element of type '" + Kode.type(get) + "'.";
                }
                IO.printfln(doc);
                return null;
            }));
            DEF_GLOBALS.put("hasattr", new KodeBuiltinFunction("hasattr", INTER, null, 2, args -> {
                Object object = args[0];
                if (object instanceof KodeInstance) {
                    try {
                        ((KodeInstance) object).get(ValueString.toStr(args[1]));
                        return Interpreter.toKodeValue(true);
                    } catch (RuntimeError e) {
                        return Interpreter.toKodeValue(false);
                    }
                }
                throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
            }));
            DEF_GLOBALS.put("setattr", new KodeBuiltinFunction("setattr", INTER, null, 3, args -> {
                Object object = args[0];
                if (object instanceof KodeInstance) {
                    ((KodeInstance) object).set(ValueString.toStr(args[1]), args[2]);
                    return null;
                }
                throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
            }));
            DEF_GLOBALS.put("getattr", new KodeBuiltinFunction("getattr", INTER, null, 2, args -> {
                Object object = args[0];
                if (object instanceof KodeInstance) {
                    return Interpreter.toKodeValue(((KodeInstance) object).get(ValueString.toStr(args[1])));
                }
                throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
            }));
            DEF_GLOBALS.put("callable", new KodeBuiltinFunction("callable", INTER, null, 1, args -> Interpreter.toKodeValue(args[0] instanceof KodeCallable)));
            DEF_GLOBALS.put("eval", new KodeBuiltinFunction("eval", INTER, null, 1, args -> {
                Object src = args[0];
                if (src instanceof KodeInstance) {
                    if (ValueString.isString((KodeInstance) src)) {
                        try {
                            return Kode.run("<eval>", ValueString.toStr((KodeInstance) src), new Interpreter()).item2;
                        } catch (RuntimeError ex) {
                            throw ex;
                        } catch (Throwable ex) {
                            throw new RuntimeError("Fatal Error Occurred in eval(): " + ex);
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
                KodeObject temp = args[0];
                if (temp instanceof KodeInstance) {
                    if (ValueString.isString((KodeInstance) temp)) {
                        String ch = ValueString.toStr(temp);
                        if (ch.length() != 1) {
                            throw new RuntimeError("ord() expected a Character, but " + Kode.type(temp) + " of length " + ch.length() + " found");
                        }
                        return Interpreter.toKodeValue((long) ch.charAt(0));
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
                KodeObject temp = args[0];
                if (temp instanceof KodeInstance) {
                    if (ValueNumber.isNumber((KodeInstance) temp)) {
                        KodeNumber asIndex = ValueNumber.toNumber(temp);
                        if (asIndex.isInteger()) {
                            try {
                                return Interpreter.toKodeValue((char) asIndex.getInteger().longValueExact());
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
            BUILTIN_MODULE.runModule();
        } catch (Throwable ex) {
            handleThrowable(ex);
            JOptionPane.showMessageDialog(null, "Failed to Initialize Interpreter\nReason : " + ex);
            IO.exit(1);
        }
    }
}
