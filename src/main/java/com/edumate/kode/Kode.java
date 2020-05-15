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
import java.util.Map;

/**
 *
 * @author dell
 */
class Kode {

    static final String NAME = "Kode";
    static final String VERSION = "v1.0.1-alpha";
    static final String EXTENSION = "kde";
    static final String AUTHOR = "Edumate";
    static final String USAGE = "Usage: kode [script]";

//<editor-fold defaultstate="collapsed" desc="Actual Codes">
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

    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    static Map<String, KodeModule> ModuleRegistry = new HashMap();

    static final String INIT = "__init__";
    static final String STRING = "__str__";
    static final String NUMBER = "__num__";
    static final String BOOLEAN = "__bool__";
    static final String LIST = "__list__";
    
    static final String GET_AT_INDEX = "__getAtIndex__";
    static final String SET_AT_INDEX = "__setAtIndex__";
    static final String CLASS = "__class__";
    static final String HASH = "__hash__";

    static final String BUILTIN_NAME = "__builtin__";

    static final String NEG = "__neg__";

    static final String ADD = "__add__";
    static final String RADD = "__radd__";

    static final String SUB = "__sub__";
    static final String RSUB = "__rsub__";

    static final String MUL = "__mul__";
    static final String RMUL = "__rmul__";

    static final String DIV = "__div__";
    static final String RDIV = "__rdiv__";

    static final String MOD = "__mod__";
    static final String RMOD = "__rmod__";

    static final String FLOOR_DIV = "__floordiv__";
    static final String RFLOOR_DIV = "__rfloordiv__";

    static void start_console(String[] args) {
        try {
            if (args.length > 1) {
                KodeIO.printfln_err(Kode.USAGE);
                System.exit(64);
            } else if (args.length == 1) {
                switch (args[0]) {
                    case "--test":
                        runBenchMark();
                        break;
                    case "-v":
                    case "--version":
                        KodeIO.printfln(Kode.getVersion());
                        break;
                    default:
                        Path path = Paths.get(args[0]);
                        if (path.getFileName().toString().endsWith("." + Kode.EXTENSION)) {
                            runFile(path.toAbsolutePath().toString(), new Interpreter());
                        } else {
                            KodeIO.printfln_err("Not a " + Kode.NAME + " runnable file");
                            System.exit(64);
                        }
                        break;
                }
            } else {
                KodeIO.printfln(Kode.getIntro());
                KodeIO.printfln("Call exit() to quit shell.");
                Interpreter interpreter = new Interpreter();
                for (;;) {
                    hadError = false;
                    hadRuntimeError = false;
                    try {
                        Object run = run("<shell>", KodeIO.scanf(">>>"), interpreter);
                        if (run != null) {
                            KodeIO.printfln(run);
                        }
                    } catch (RuntimeError error) {
                        Kode.runtimeError(error);
                    } catch (Error | Exception e) {
                        KodeIO.printfln_err("Fatal Error : " + e);
                    }
                }
            }
        } catch (Error | Exception e) {
            KodeIO.printfln_err("Fatal Error : " + e);
        }
    }

    static void runFile(String path, Interpreter inter) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(Paths.get(path).toFile().getName(), new String(bytes, Charset.defaultCharset()), inter);

        // Indicate an error in the exit code.
        if (hadError) {
            System.exit(65);
        }
        if (hadRuntimeError) {
            System.exit(70);
        }
    }

    private static void runBenchMark() throws Exception {
        runLib("benchmark", false, new Interpreter());
    }

    static void runLib(String name, Interpreter inter) throws Exception {
        runLib(name, true, inter);
    }

    static void runLib(String name, boolean fromDir, Interpreter inter) throws Exception {

        if (fromDir) {
            FileSearch path = new FileSearch("./", name + "." + Kode.EXTENSION);
            //KodeIO.printfln(path.path);
            if (path.exists()) {
                byte[] bytes = Files.readAllBytes(path.path.toAbsolutePath());
                run(path.path.toAbsolutePath().toFile().getName(), new String(bytes, Charset.defaultCharset()), inter);
                return;
            }

            String p = Paths.get(Paths.get(Kode.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toFile().getAbsolutePath(), "libs").toString();
            //KodeIO.printfln(p);
            path = new FileSearch(p, name + "." + Kode.EXTENSION);
            if (path.exists()) {
                byte[] bytes = Files.readAllBytes(path.path.toAbsolutePath());
                run(path.path.toAbsolutePath().toFile().getName(), new String(bytes, Charset.defaultCharset()), inter);
                return;
            }
        }

        byte[] bytes;
        InputStream file = Kode.class.getResourceAsStream("/" + name + "." + Kode.EXTENSION);
        if (file != null) {
            bytes = file.readAllBytes();
            run(name + "." + Kode.EXTENSION, new String(bytes, Charset.defaultCharset()), inter);
            return;
        }

        throw new Exception("Failed to Import file " + name + "." + Kode.EXTENSION);
    }

    static Object run(String fn, String source, Interpreter inter) throws Exception {
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

        return inter.interpret(statements);
    }

    static void error(String fn, int line, String message) {
        report(line, " in file '" + fn + "'", message);
    }

    static void report(int line, String where, String message) {
        KodeIO.printfln_err(
                "[line " + line + "] Error " + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "' in file '" + token.fn + "'", message);
        }
    }

    static void warning(String message) {
        KodeIO.printfln_err("Warning: " + message);
    }

    static void runtimeError(RuntimeError error) {
        KodeIO.printfln_err("Runtime Error : " + error.getMessage());
        error.token.forEach((line) -> {
            if (line != null) {
                KodeIO.printfln_err("in file " + line.fn + " [ at line " + line.line + " ] near '" + line.lexeme + "'");
                if (line.line_text != null) {
                    KodeIO.printfln_err("->\t" + line.line_text.trim());
                }
            }
        });
        hadRuntimeError = true;
    }

    static String _stringify(Object object) {

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
        if (object instanceof Double) {
            Double num = (Double) object;
            if (num.isInfinite()) {
                return "Infinity";
            }
            if (num.isNaN()) {
                return "NaN";
            }
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        // List
        if (object instanceof List) {
            List list = (List) object;
            if (list.isEmpty()) {
                return "[]";
            } else if (list.size() == 1) {
                return "[" + _stringify(list.get(0)) + "]";
            } else {
                String text = "[" + _stringify(list.get(0));
                for (int i = 1; i < list.size(); i++) {
                    text += ", " + _stringify(list.get(i));
                }
                text += "]";
                return text;
            }
        }

        // Object
        return object.toString();
    }

    static String stringify(Object object) {

        if (object instanceof KodeInstance) {
            if (ValueList.isList((KodeInstance) object)) {

            }
        }

        return _stringify(object);
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
        if (object instanceof KodeInstance) {
            if (object instanceof KodeModule) {
                return "module." + ((KodeModule) object).name;
            }
            return ((KodeInstance) object).klass.class_name;
        }
        if (object instanceof JavaNative) {
            return "native.function." + ((JavaNative) object).className + "." + ((JavaNative) object).methodName;
        }
        if (object instanceof Hash) {
            return "hash";
        }
        return "unknown";
    }

    static boolean instanceOf(KodeInstance i, KodeClass c) {
        return instanceOf(i.klass, c);
    }

    private static boolean instanceOf(KodeClass i, KodeClass c) {
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

    static final Map<String, Object> DEF_GLOBALS = new HashMap();

    static {
        KodeModule module;
        try {
            if (Kode.ModuleRegistry.containsKey(Kode.BUILTIN_NAME)) {
                module = Kode.ModuleRegistry.get(Kode.BUILTIN_NAME);
            } else {
                module = new KodeModule(Kode.BUILTIN_NAME, Kode.BUILTIN_NAME);
                Kode.ModuleRegistry.put(Kode.BUILTIN_NAME, module);
                module.run();
            }
            if (module.hadError || module.hadRuntimeError) {
                throw new Exception();
            }
            final Interpreter inter = module.inter;
            DEF_GLOBALS.putAll(inter.globals.values);
            DEF_GLOBALS.put("NaN", inter.toKodeValue(Double.NaN));
            DEF_GLOBALS.put("Infinity", inter.toKodeValue(Double.POSITIVE_INFINITY));
            DEF_GLOBALS.put("type", new KodeBuiltinFunction("type", null, inter) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("obj", null));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    return interpreter.toKodeValue("<type '" + Kode.type(arguments.get("obj")) + "'>");
                }

            });
            DEF_GLOBALS.put("print", new KodeBuiltinFunction("print", null, inter) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("str", interpreter.toKodeValue(Arrays.asList(interpreter.toKodeValue(""))), true),
                            new Pair("sep", interpreter.toKodeValue(" ")),
                            new Pair("end", interpreter.toKodeValue("\n")));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    List str = ValueList.toList(arguments.get("str"));
                    String sep = ValueString.toStr(arguments.get("sep"));
                    String end = ValueString.toStr(arguments.get("end"));
                    if (str.size() > 0) {
                        KodeIO.printf(ValueString.toStr(str.get(0)));
                    }
                    for (int i = 1; i < str.size(); i++) {
                        KodeIO.printf(sep);
                        KodeIO.printf(ValueString.toStr(str.get(i)));
                    }
                    KodeIO.printf(end);
                    return null;
                }

            });

            ValueNumber valueNumber = new ValueNumber(inter);
            DEF_GLOBALS.put(valueNumber.class_name, valueNumber);

            ValueString valueString = new ValueString(inter);
            DEF_GLOBALS.put(valueString.class_name, valueString);

            ValueBool valueBool = new ValueBool(inter);
            DEF_GLOBALS.put(valueBool.class_name, valueBool);

            ValueList valueList = new ValueList(inter);
            DEF_GLOBALS.put(valueList.class_name, valueList);

            DEF_GLOBALS.put("ins", new KodeBuiltinFunction("ins", null, inter) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("ins", null), new Pair("klass", null));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    return this.interpreter.toKodeValue(Kode.instanceOf((KodeInstance) arguments.get("ins"),
                            (KodeClass) arguments.get("klass")));
                }

            });

            DEF_GLOBALS.put("exit", new KodeBuiltinFunction("exit", null, inter) {
                @Override
                public List<Pair<String, Object>> arity() {
                    return Arrays.asList(new Pair("status", interpreter.toKodeValue(Double.valueOf(0))));
                }

                @Override
                public Object call(Map<String, Object> arguments) {
                    KodeIO.exit(ValueNumber.toNumber(arguments.get("status")).intValue());
                    return null;
                }

            });
            
        } catch (Exception ex) {
            KodeIO.exit(1);
        }
    }

//</editor-fold>
}
