/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
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
    static final String VERSION = "1.0.0";
    static final String EXTENSION = "kde";
    static final String AUTHOR = "Kode-Devs";
    static final String USAGE = "Usage: kode [script]";

    static String getVersion() {
        return Kode.NAME + " " + Kode.VERSION;
    }

    static String getIntro() {
        String res = Kode.getVersion();
        try {
            res += " ( " + Kode.AUTHOR + ", "
                    + new Date(new File(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI()).lastModified()) + " ) ";
        } catch (URISyntaxException e) {
        }
        res += "\non " + System.getProperty("os.name", "Unknown Operating System") + " (" + System.getProperty("user.name", "default") + ")";
        return res;
    }

    static void start_console(String... args) {
        try {
            switch (args.length) {
                case 0:
                    //<editor-fold defaultstate="collapsed" desc="Shell">
                    KodeHelper.printfln(Kode.getIntro());
                    KodeHelper.printfln("Call exit() to quit shell.");
                    Interpreter interpreter = new Interpreter();
                    interpreter.globals.define(Kode.__NAME__, interpreter.toKodeValue(Kode.__MAIN__));
                    for (;;) {
                        hadError = false;
                        hadRuntimeError = false;
                        try {
                            Pair run = run("<shell>", KodeHelper.scanf(">>>"), interpreter);
                            if (run != null) {
                                if (run.value != null) {
                                    Object value = run.value;
                                    if (value instanceof KodeInstance) {
                                        if (ValueString.isString((KodeInstance) value)) {
                                            KodeHelper.printfln('\'' + value.toString() + '\'');
                                            continue;
                                        }
                                    }
                                    KodeHelper.printfln(value);
                                }
                            }
                        } catch (RuntimeError error) {
                            Kode.runtimeError(error);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            KodeHelper.printfln_err("Fatal Error : " + e);
                        }
                    }
                //</editor-fold>
                case 1:
                    //<editor-fold defaultstate="collapsed" desc="File">
                    Path path = Paths.get(args[0]);
                    if (path.getFileName().toString().endsWith("." + Kode.EXTENSION)) {
                        runFile(path.toAbsolutePath().toString(), new Interpreter());
                    } else {
                        KodeHelper.printfln_err("Not a " + Kode.NAME + " runnable file");
                        System.exit(64);
                    }
                    //</editor-fold>
                    break;
                default:
                    KodeHelper.printfln_err(Kode.USAGE);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            KodeHelper.printfln_err("Fatal Error : " + e);
        }
        KodeHelper.exit(0);
    }
    
    static String LIBPATH;

    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    static Map<String, KodeModule> ModuleRegistry = new HashMap();

    static final String INIT = "__init__";
    static final String INFINITY = "Infinity";
    static final String NAN = "NaN";
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

    static void runFile(String path, Interpreter inter) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        inter.globals.define(Kode.__NAME__, inter.toKodeValue(Kode.__MAIN__));
        run(Paths.get(path).toFile().getName(), new String(bytes, Charset.defaultCharset()), inter);

        // Indicate an error in the exit code.
        if (hadError) {
            System.exit(65);
        }
        if (hadRuntimeError) {
            System.exit(70);
        }
    }

    static String runLib(String name, Interpreter inter) throws Exception {
        return runLib(name, true, inter);
    }

    static String runLib(String name, boolean fromDir, Interpreter inter) throws Exception {
        String pkgname = name.contains(File.separator) ? Arrays.asList(name.split(File.separator)).get(0) : name; // BUG name.split not working
        String p = Paths.get(Kode.LIBPATH, pkgname).toAbsolutePath().toString();
        try {
            if (fromDir) {
                FileSearch path = new FileSearch("./", name + "." + Kode.EXTENSION);
                if (path.exists()) {
                    byte[] bytes = Files.readAllBytes(path.path.toAbsolutePath());
                    return run(path.path.toAbsolutePath().toFile().getName(), new String(bytes, Charset.defaultCharset()), inter).key;
                }

                if (Pip4kode.checkUpdate(pkgname, p)) {
                    KodeHelper.printfln_err("[Info]: Package '" + pkgname + "' needs an update.");
                    if (KodeHelper.scanf("Do you want to update the package '" + pkgname + "' ? [y/n]")
                            .equalsIgnoreCase("y")) {
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

            KodeHelper.printfln_err("[Info]: Library file " + name + "." + Kode.EXTENSION + " not found in your device.");
            throw new Exception();
        } catch (Exception e) {
            Pip4kode pip;
            try {
                pip = new Pip4kode(pkgname);
                KodeHelper.printfln("Reading package metadata from repository ...");
                pip.init();
                if (!KodeHelper.scanf("Do you want to download the package '" + pip.pkg + "' (" + pip.sizeInWords + ") ? [y/n]")
                        .equalsIgnoreCase("y")) {
                    throw new Exception();
                }
                KodeHelper.printfln("Get: " + pip.repositoryRoot + " " + pip.pkg
                        + " rev " + pip.latestRevision + " [" + pip.sizeInWords + "]");
                pip.download(p);
                KodeHelper.printfln("Download Finished");
            } catch (Exception ex) {
                throw new RuntimeError("Requirement " + name + " not satisfied.");
            }
            return runLib(name, fromDir, inter);
        }
    }

    static Pair<String, Object> run(String fn, String source, Interpreter inter) throws Exception {
        Lexer scanner = new Lexer(fn, source);
        List<Token> tokens = scanner.scanTokens();

        // Stop if there was a unknown char error.
        if (hadError) {
            return null;
        }

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) {
            return null;
        }

        Resolver resolver = new Resolver(inter);
        resolver.resolve(statements);

        // Stop if there was a resolution error.
        if (hadError) {
            return null;
        }

        return new Pair(parser.doc, inter.interpret(statements));
    }

    static void error(String fn, int line, String message) {
        report(line, " in file '" + fn + "'", message);
    }

    static void report(int line, String where, String message) {
        KodeHelper.printfln_err(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " near end in file " + token.fn, message);
        } else {
            report(token.line, " near '" + token.lexeme + "' in file " + token.fn, message);
        }
        if (token.line_text != null) {
            KodeHelper.printfln_err("->\t" + token.line_text.trim());
        }
    }

    static void warning(String message) {
        warnings.print_warning("Warning: " + message);
    }

    static void runtimeError(RuntimeError error) {
        KodeHelper.printfln_err(error.getMessage());
        error.token.forEach((line) -> {
            if (line != null) {
                KodeHelper.printfln_err("in file " + line.fn + " [ at line " + line.line + " ] near '" + line.lexeme + "'");
                if (line.line_text != null) {
                    KodeHelper.printfln_err("->\t" + line.line_text.trim());
                }
            }
        });
        hadRuntimeError = true;
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
            String format = String.format(Locale.US, "%.10G", num);
            format = format
                    .replaceFirst("\\.0+(e|$)", "$1")
                    .replaceFirst("(\\.[0-9]*[1-9])(0+)(e|$)", "$1$3");
            return format;
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
            LIBPATH = Paths.get(Paths.get(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                .getParent().toFile().getAbsolutePath(), "libs").toAbsolutePath().toString();
            final Map<String, Object> DEF_GLOBALS = new HashMap();
            DEF_GLOBALS.put("print", new KodeBuiltinFunction("print", null, INTER) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(
                            new Pair("str", interpreter.toKodeValue(Arrays.asList(interpreter.toKodeValue("")))).setType(TokenType.STAR),
                            new Pair("sep", interpreter.toKodeValue(" ")),
                            new Pair("end", interpreter.toKodeValue("\n")));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    List str = ValueList.toList(arguments.get("str"));
                    String sep = ValueString.toStr(arguments.get("sep"));
                    String end = ValueString.toStr(arguments.get("end"));
                    KodeHelper.printf(str.stream().map(ValueString::toStr).collect(Collectors.joining(sep)) + end);
                    return null;
                }

            });
            DEF_GLOBALS.put("input", new KodeBuiltinFunction("input", null, INTER) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("str", interpreter.toKodeValue(Arrays.asList(interpreter.toKodeValue("")))).setType(TokenType.STAR),
                            new Pair("sep", interpreter.toKodeValue(" ")),
                            new Pair("end", interpreter.toKodeValue("")),
                            new Pair("mask", interpreter.toKodeValue(false)));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    List str = ValueList.toList(arguments.get("str"));
                    String sep = ValueString.toStr(arguments.get("sep"));
                    String end = ValueString.toStr(arguments.get("end"));
                    String msg = str.stream().map(ValueString::toStr).collect(Collectors.joining(sep)) + end;
                    if (ValueBool.toBoolean(arguments.get("mask"))) {
                        return interpreter.toKodeValue(KodeHelper.scanf_pwd(msg));
                    }
                    return interpreter.toKodeValue(KodeHelper.scanf(msg));
                }

            });
            DEF_GLOBALS.put("len", new KodeBuiltinFunction("len", null, INTER) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("obj", null));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    Object obj = arguments.get("obj");
                    try {
                        if (obj instanceof KodeInstance) {
                            Object fun = ((KodeInstance) obj).get(Kode.LEN);
                            if (fun instanceof KodeFunction) {
                                return ((KodeFunction) fun).call(Arrays.asList());
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
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("obj", null));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    Object obj = arguments.get("obj");
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
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("obj", null));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    return interpreter.toKodeValue(arguments.get("obj").hashCode());
                }

            });
            DEF_GLOBALS.put("now", new KodeBuiltinFunction("now", null, INTER) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList();
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    return interpreter.toKodeValue(System.currentTimeMillis());
                }

            });
            DEF_GLOBALS.put("free", new KodeBuiltinFunction("free", null, INTER) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList();
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    System.gc();
                    return null;
                }

            });
            DEF_GLOBALS.put("resetLine", new KodeBuiltinFunction("resetLine", null, INTER) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList();
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    KodeHelper.resetLine();
                    return null;
                }

            });
            DEF_GLOBALS.put("clear", new KodeBuiltinFunction("clear", null, INTER) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList();
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    KodeHelper.clc();
                    return null;
                }

            });
            DEF_GLOBALS.put("isinstance", new KodeBuiltinFunction("isinstance", null, INTER) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("object", null), new Pair("type", null));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    Object object = arguments.get("object");
                    Object type = arguments.get("type");
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
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("object", null), new Pair("type", null));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    Object object = arguments.get("object");
                    Object type = arguments.get("type");
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
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("status", interpreter.toKodeValue(0)));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    KodeHelper.exit(ValueNumber.toNumber(arguments.get("status")).getAsIndex());
                    return null;
                }

            });
            DEF_GLOBALS.put("help", new KodeBuiltinFunction("help", null, INTER) {

                @Override
                String doc() {
                    return "Call help(obj) to get the help document attached with the object 'obj'.";
                }

                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("obj", this));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    Object get = arguments.get("obj");
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
                    KodeHelper.printfln(doc);
                    return null;
                }

            });
            DEF_GLOBALS.put("hasattr", new KodeBuiltinFunction("hasattr", null, INTER) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("object", null), new Pair("attribute", null));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    Object object = arguments.get("object");
                    String attribute = arguments.get("attribute").toString();
                    if (object instanceof KodeInstance) {
                        try {
                            ((KodeInstance) object).get(attribute);
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
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("object", null), new Pair("attribute", null), new Pair("value", null));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    Object object = arguments.get("object");
                    String attribute = arguments.get("attribute").toString();
                    Object value = arguments.get("value");
                    if (object instanceof KodeInstance) {
                        ((KodeInstance) object).set(attribute, value);
                        return null;
                    }
                    throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
                }

            });
            DEF_GLOBALS.put("getattr", new KodeBuiltinFunction("getattr", null, INTER) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("object", null), new Pair("attribute", null));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    Object object = arguments.get("object");
                    String attribute = arguments.get("attribute").toString();
                    if (object instanceof KodeInstance) {
                        try {
                            return this.interpreter.toKodeValue(((KodeInstance) object).get(attribute));
                        } catch (RuntimeError e) {
                            throw e;
//                            return this.interpreter.toKodeValue(false);
                        }
                    }
                    throw new RuntimeError("Value passed for argument 'object' is not an instance of a class.");
                }

            });
            DEF_GLOBALS.put("callable", new KodeBuiltinFunction("callable", null, INTER) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("object", null));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    Object object = arguments.get("object");
                    return this.interpreter.toKodeValue(object instanceof KodeCallable);
                }

            });
            
            DEF_GLOBALS.put(ValueNumber.val.class_name, ValueNumber.val);   //Number
            DEF_GLOBALS.put(ValueString.val.class_name, ValueString.val);   //String
            DEF_GLOBALS.put(ValueBool.val.class_name, ValueBool.val);   //Bool
            DEF_GLOBALS.put(ValueList.val.class_name, ValueList.val);   //List
            DEF_GLOBALS.put(ValueError.val.class_name, ValueError.val);    //Error
            DEF_GLOBALS.put(ValueType.val.class_name, ValueType.val);    //Type
            DEF_GLOBALS.put(ValueNotImplemented.val.class_name, ValueNotImplemented.val);
            INTER.globals.values.putAll(DEF_GLOBALS);
            KodeModule module = new KodeModule(Kode.BUILTIN_NAME, Kode.BUILTIN_NAME);
            Kode.ModuleRegistry.put(Kode.BUILTIN_NAME, module);
            module.inter = INTER;
            module.run();
            if (module.hadError || module.hadRuntimeError) {
                throw new Exception();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Failed to Initialize Interpreter\nReason : " + ex);
            KodeHelper.exit(1);
        }
    }
}
