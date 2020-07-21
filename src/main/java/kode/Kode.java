/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryNotificationInfo;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.management.NotificationEmitter;
import javax.swing.JOptionPane;
import lib.warnings;
import math.KodeNumber;
import utils.Pip4kode;

/**
 *
 * @author dell
 */
class Kode {

    static final String NAME = "Kode";
    static String VERSION = "debug";
    static final String EXTENSION = "kde";
    static final String AUTHOR = "Kode-Devs";
    static final String USAGE = "Usage: kode [script]";

    static String getVersion() {
        return NAME + " " + VERSION;
    }

    static String getIntro() {
        String res = Kode.getVersion();
        try {
            res += " ( " + AUTHOR + ", "
                    + new Date(new File(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI()).lastModified()) + " ) ";
        } catch (URISyntaxException e) {
        }
        res += "\non " + System.getProperty("os.name", "Unknown Operating System") + " (" + System.getProperty("user.name", "default") + ")";
        return res;
    }

    public static void main(String... args) {
        switch (args.length) {
            case 0:
                break;
            case 1:
                VERSION = args[0];
                //<editor-fold defaultstate="collapsed" desc="Shell">
                IO.printfln(Kode.getIntro());
                IO.printfln("Call exit() to quit shell.");
                Interpreter interpreter = new Interpreter();
                interpreter.globals.define(Kode.__NAME__, interpreter.toKodeValue(Kode.__MAIN__));
                for (;;) {
                    try {
                        IO.printf(">>>");
                        Pair run = run("<shell>", IO.scanf(), interpreter);
                        if (run != null) {
                            if (run.value != null) {
                                Object value = run.value;
                                if (value instanceof KodeInstance) {
                                    if (ValueString.isString((KodeInstance) value)) {
                                        IO.printfln('\'' + value.toString() + '\'');
                                        continue;
                                    }
                                }
                                IO.printfln(value);
                            }
                        }
                    } catch (Throwable e) {
                        handleThrowable(e);
                    }
                }
            //</editor-fold>
            case 2:
                //<editor-fold defaultstate="collapsed" desc="File">
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
                //</editor-fold>
                break;
            default:
                IO.printfln_err(USAGE);
        }
        IO.exit(0);
    }

    static void handleThrowable(Throwable e) {
        if (e instanceof RuntimeError) {
            Kode.runtimeError((RuntimeError) e);
        } else {
            e.printStackTrace();
            IO.printfln_err("Fatal Error : " + e);
        }
    }

    static String LIBPATH;
    static Map<String, KodeModule> ModuleRegistry = new HashMap();

    static final String INIT = "__init__";
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

    static void runFile(String path, Interpreter inter) throws Throwable {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        inter.globals.define(Kode.__NAME__, inter.toKodeValue(Kode.__MAIN__));
        run(Paths.get(path).toFile().getName(), new String(bytes, Charset.defaultCharset()), inter);
    }

    static String runLib(String name, Interpreter inter) throws Throwable {
        return runLib(name, true, inter);
    }

    static String runLib(String name, boolean fromDir, Interpreter inter) throws Throwable {
        String pkgname = Paths.get(name).getName(0).toString();
        String p = Paths.get(Kode.LIBPATH, pkgname).toAbsolutePath().toString();
        try {
            if (fromDir) {
                FileSearch path = new FileSearch("./", name + "." + Kode.EXTENSION);
                if (path.exists()) {
                    byte[] bytes = Files.readAllBytes(path.path.toAbsolutePath());
                    return run(path.path.toAbsolutePath().toFile().getName(), new String(bytes, Charset.defaultCharset()), inter).key;
                }

                if (Pip4kode.checkUpdate(pkgname, p)) {
                    IO.printf_err("[Info]: Package '" + pkgname + "' needs an update.\n"
                            + "Do you want to update the package '" + pkgname + "' ? [y/n]");
                    if (IO.scanf().equalsIgnoreCase("y")) {
                        throw new Exception();
                    }
                }

                path = new FileSearch(p, name + "." + Kode.EXTENSION);
                if (path.exists()) {
                    byte[] bytes = Files.readAllBytes(path.path.toAbsolutePath());
                    return run(path.path.toAbsolutePath().toFile().getName(), new String(bytes, Charset.defaultCharset()), inter).key;
                }
            }

            byte[] bytes;
            InputStream file = Kode.class.getResourceAsStream("/" + name + "." + Kode.EXTENSION);
            if (file != null) {
                bytes = file.readAllBytes();
                return run(name + "." + Kode.EXTENSION, new String(bytes, Charset.defaultCharset()), inter).key;
            }

            IO.printfln_err("[Info]: Library file " + name + "." + Kode.EXTENSION + " not found in your device.");
            throw new Exception();
        } catch (Exception e) {
            Pip4kode pip;
            try {
                pip = new Pip4kode(pkgname);
                IO.printfln("Reading package metadata from repository ...");
                pip.init();
                IO.printf("Do you want to download the package '" + pip.pkg + "' (" + pip.sizeInWords + ") ? [y/n]");
                if (!IO.scanf().equalsIgnoreCase("y")) {
                    throw new Exception();
                }
                IO.printfln("Get: " + pip.repositoryRoot + " " + pip.pkg
                        + " rev " + pip.latestRevision + " [" + pip.sizeInWords + "]");
                pip.download(p);
                IO.printfln("Download Finished");
            } catch (Exception ex) {
                throw new RuntimeError("Requirement " + name + " not satisfied.");
            }
            return runLib(name, fromDir, inter);
        }
    }

    static Pair<String, Object> run(String fn, String source, Interpreter inter) throws Throwable {
        List<Token> tokens = new Lexer(fn, source).scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        new Resolver(inter).resolve(statements);
        return new Pair(parser.doc, inter.interpret(statements));
    }

    static void warning(String message) {
        warnings.print_warning("[Warning]: " + message);
    }

    static void runtimeError(RuntimeError error) {
        error.token.removeIf(a -> a == null);
        Collections.reverse(error.token);
        for (BigInteger i = BigInteger.ZERO; i.compareTo((BigInteger) error.instance.data) < 0 && !error.token.empty(); i = i.add(BigInteger.ONE)) {
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

    static String stringify(Object object) {

        // Nil
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
        // Hack. Work around Java adding ".0" to integer-valued doubles.
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
//            String format = String.format(Locale.US, "%.10G", num);
//            format = format
//                    .replaceFirst("\\.0+(e|$)", "$1")
//                    .replaceFirst("(\\.[0-9]*[1-9])(0+)(e|$)", "$1$3");
//            return format;
                return num.toString();
            }
        }

        // List
        if (object instanceof List) {
            List list = (List) object;
            if (list.isEmpty()) {
                return "[]";
            } else if (list.size() == 1) {
                return "[" + stringify(list.get(0)) + "]";
            } else {
                String text = "[" + stringify(list.get(0));
                for (int i = 1; i < list.size(); i++) {
                    text += ", " + stringify(list.get(i));
                }
                text += "]";
                return text;
            }
        }

        // Object
        return object.toString();
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
            if (object instanceof KodeModule) {
                return "module." + ((KodeModule) object).name;
            }
            return ((KodeInstance) object).klass.class_name;
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

    static final Interpreter INTER = new Interpreter();

    static {
        try {
            ((NotificationEmitter) ManagementFactory.getMemoryMXBean()).addNotificationListener((n, hb) -> {
                if (n.getType().equals(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED)) {
                    IO.printfln_err("[INFO]: Low on Memory.");
                }
            }, null, null);
            LIBPATH = Paths.get(Paths.get(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParent().getParent().toFile().getAbsolutePath(), "libs").toAbsolutePath().toString(); // Get Parent added.
            final Map<String, Object> DEF_GLOBALS = new HashMap();
            DEF_GLOBALS.put("disp", new KodeBuiltinFunction("print", null, INTER) {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Object... arguments) {
                    IO.printf(arguments[0] + "\n");
                    return null;
                }

            });
            DEF_GLOBALS.put("sprintf", new KodeBuiltinFunction("printf", null, INTER) {
                @Override
                public int arity() {
                    return -2;
                }

                @Override
                public Object call(Object... arguments) {
                    List ll = new ArrayList();
                    for (int i = 1; i < arguments.length; i++) {
                        Object toJava = Interpreter.toJava(arguments[i]);
                        if (toJava instanceof KodeNumber) {
                            ll.add(((KodeNumber) toJava).isInteger() ? ((KodeNumber) toJava).getInteger() : ((KodeNumber) toJava).getFloat());
                        } else {
                            ll.add(toJava);
                        }
                    }
                    try {
                        return this.interpreter.toKodeValue(String.format(arguments[0].toString(), ll.toArray()));
                    } catch (Exception e) {
                        throw new RuntimeError("Format error -> " + e);
                    }
                }

            });
            DEF_GLOBALS.put("printf", new KodeBuiltinFunction("printf", null, INTER) {
                @Override
                public int arity() {
                    return -2;
                }

                @Override
                public Object call(Object... arguments) {
                    IO.printf(((KodeCallable) DEF_GLOBALS.get("sprintf")).call(arguments));
                    return null;
                }

            });
            DEF_GLOBALS.put("input", new KodeBuiltinFunction("input", null, INTER) {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Object... arguments) {
                    IO.printf(arguments[0]);
                    return interpreter.toKodeValue(IO.scanf());
                }

            });

            DEF_GLOBALS.put("len", new KodeBuiltinFunction("len", null, INTER) {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Object... arguments) {
                    Object obj = arguments[0];
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
                }

            });
            DEF_GLOBALS.put("dir", new KodeBuiltinFunction("dir", null, INTER) {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Object... arguments) {
                    Object obj = arguments[0];
                    Set<String> dir = new TreeSet();
                    if (obj instanceof KodeInstance) {
                        dir.addAll(((KodeInstance) obj).fields.keySet());
                        if (obj instanceof KodeModule) {
                            dir.addAll(((KodeModule) obj).inter.globals.values.keySet());
                        } else if (((KodeInstance) obj).klass != null) {
                            KodeClass kls = ((KodeInstance) obj).klass;
                            for (;;) {
                                if (kls != null) {
                                    dir.addAll(kls.methods.keySet());
                                    dir.addAll(kls.specialMethods().keySet());
                                } else {
                                    break;
                                }
                                kls = kls.superclass;
                            }
                        }
                    }
                    return interpreter.toKodeValue(Arrays.asList(dir.toArray()));
                }

            });
            DEF_GLOBALS.put("id", new KodeBuiltinFunction("id", null, INTER) {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Object... arguments) {
                    return interpreter.toKodeValue(arguments[0].hashCode());
                }

            });
            DEF_GLOBALS.put("now", new KodeBuiltinFunction("now", null, INTER) {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Object... arguments) {
                    return interpreter.toKodeValue(System.currentTimeMillis());
                }

            });
            DEF_GLOBALS.put("free", new KodeBuiltinFunction("free", null, INTER) {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Object... arguments) {
                    System.gc();
                    return null;
                }

            });
            DEF_GLOBALS.put("resetLine", new KodeBuiltinFunction("resetLine", null, INTER) {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Object... arguments) {
                    IO.resetLine();
                    return null;
                }

            });
            DEF_GLOBALS.put("clear", new KodeBuiltinFunction("clear", null, INTER) {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Object... arguments) {
                    IO.clc();
                    return null;
                }

            });
            DEF_GLOBALS.put("isinstance", new KodeBuiltinFunction("isinstance", null, INTER) {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Object... arguments) {
                    Object object = arguments[0];
                    Object type = arguments[1];
                    if (!(object instanceof KodeInstance)) {
                        throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
                    }
                    if (!(type instanceof KodeClass)) {
                        throw new RuntimeError("Value passed for argument 'type' is not a class.");
                    }
                    return this.interpreter.toKodeValue(Kode.instanceOf((KodeInstance) object, (KodeClass) type));
                }

            });
            DEF_GLOBALS.put("issubclass", new KodeBuiltinFunction("issubclass", null, INTER) {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Object... arguments) {
                    Object object = arguments[0];
                    Object type = arguments[1];
                    if (!(object instanceof KodeClass)) {
                        throw new RuntimeError("Value passed for argument 'object' is not a class.");
                    }
                    if (!(type instanceof KodeClass)) {
                        throw new RuntimeError("Value passed for argument 'type' is not a class.");
                    }
                    return this.interpreter.toKodeValue(Kode.instanceOf((KodeClass) object, (KodeClass) type));
                }

            });
            DEF_GLOBALS.put("exit", new KodeBuiltinFunction("exit", null, INTER) {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Object... arguments) {
                    IO.exit(0);
                    return null;
                }

            });
            DEF_GLOBALS.put("help", new KodeBuiltinFunction("help", null, INTER) {

                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Object... arguments) {
                    Object get = arguments[0];
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
                    if (doc == null) {
                        doc = "No Documentation Avialable for element of type '" + Kode.type(get) + "'.";
                    }
                    IO.printfln(doc);
                    return null;
                }

            });
            DEF_GLOBALS.put("hasattr", new KodeBuiltinFunction("hasattr", null, INTER) {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Object... arguments) {
                    Object object = arguments[0];
                    if (object instanceof KodeInstance) {
                        try {
                            ((KodeInstance) object).get(arguments[1].toString());
                            return this.interpreter.toKodeValue(true);
                        } catch (RuntimeError e) {
                            return this.interpreter.toKodeValue(false);
                        }
                    }
                    throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
                }

            });
            DEF_GLOBALS.put("setattr", new KodeBuiltinFunction("setattr", null, INTER) {
                @Override
                public int arity() {
                    return 3;
                }

                @Override
                public Object call(Object... arguments) {
                    Object object = arguments[0];
                    if (object instanceof KodeInstance) {
                        ((KodeInstance) object).set(arguments[1].toString(), arguments[2]);
                        return null;
                    }
                    throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
                }

            });
            DEF_GLOBALS.put("getattr", new KodeBuiltinFunction("getattr", null, INTER) {
                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Object... arguments) {
                    Object object = arguments[0];
                    if (object instanceof KodeInstance) {
                        try {
                            return this.interpreter.toKodeValue(((KodeInstance) object).get(arguments[1].toString()));
                        } catch (RuntimeError e) {
                            throw e;
                        }
                    }
                    throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
                }

            });
            DEF_GLOBALS.put("callable", new KodeBuiltinFunction("callable", null, INTER) {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Object... arguments) {
                    return this.interpreter.toKodeValue(arguments[0] instanceof KodeCallable);
                }

            });
            DEF_GLOBALS.put("eval", new KodeBuiltinFunction("eval", null, INTER) {
                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Object... arguments) {
                    Object src = arguments[0];
                    if (src instanceof KodeInstance) {
                        if (ValueString.isString((KodeInstance) src)) {
                            try {
                                return eval(ValueString.toStr(src));
                            } catch (RuntimeError ex) {
                                throw ex;
                            } catch (Throwable ex) {
                                throw new RuntimeError("Fatal Error Occured in eval(): " + ex);
                            }
                        }
                    }
                    throw new NotImplemented();
                }

                private Object eval(String source) throws Throwable {
                    Interpreter inter = new Interpreter();
                    Lexer scanner = new Lexer("<eval>", source);
                    List<Token> tokens = scanner.scanTokens();
                    Parser parser = new Parser(tokens);
                    List<Stmt> statements = parser.parse();
                    Resolver resolver = new Resolver(inter);
                    resolver.resolve(statements);
                    return inter.interpret(statements);
                }

            });

            DEF_GLOBALS.put(ValueNumber.val.class_name, ValueNumber.val);   //Number
            DEF_GLOBALS.put(ValueString.val.class_name, ValueString.val);   //String
            DEF_GLOBALS.put(ValueBool.val.class_name, ValueBool.val);   //Bool
            DEF_GLOBALS.put(ValueList.val.class_name, ValueList.val);   //List
            DEF_GLOBALS.put(ValueError.val.class_name, ValueError.val);    //Error
            DEF_GLOBALS.put(ValueType.val.class_name, ValueType.val);    //Type
            DEF_GLOBALS.put(ValueNotImplemented.val.class_name, ValueNotImplemented.val); //NotImplemented Error
            INTER.globals.values.putAll(DEF_GLOBALS);
            KodeModule module = new KodeModule(Kode.BUILTIN_NAME, Kode.BUILTIN_NAME);
            Kode.ModuleRegistry.put(Kode.BUILTIN_NAME, module);
            module.inter = INTER;
            module.run();
        } catch (Throwable ex) {
            handleThrowable(ex);
            JOptionPane.showMessageDialog(null, "Failed to Initialize Interpreter\nReason : " + ex.toString());
            IO.exit(1);
        }
    }
}
