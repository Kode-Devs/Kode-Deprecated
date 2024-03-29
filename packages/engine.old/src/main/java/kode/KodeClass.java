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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kni.KodeObject;
import math.KodeMath;

/**
 * This class is used to represent any non-builtin class definition.
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
class KodeClass extends KodeCallable {

    /**
     * Doc-string or help text associated with the function, or {@code null} for
     * missing documentation.
     */
    String __doc__ = null;

    final String class_name;
    final KodeClass superclass;
    Map<String, KodeFunction> methods;
    final Interpreter interpreter;

    /**
     * Map containing names and declaration of all predefined methods for any
     * class.
     */
    Map<String, KodeFunction> specialMethods;

    /**
     * Generates a new non-builtin class definition.
     *
     * @param name        Name of the class.
     * @param superclass  Reference to the super-class definition if present, or
     *                    {@code null}.
     * @param methods     A map consisting all method names and its definitions,
     *                    declared within the class definition.
     * @param interpreter Associated interpreter reference.
     */
    KodeClass(String name, KodeClass superclass, Map<String, KodeFunction> methods, Interpreter interpreter) {
        this.superclass = superclass;
        this.class_name = name;
        this.methods = methods;
        this.interpreter = interpreter;

        // Definitions for predefined methods.
        this.specialMethods = new HashMap<>();

        //<editor-fold defaultstate="collapsed" desc="init">
        specialMethods.put(Kode.INIT, new KodeBuiltinFunction(Kode.INIT, interpreter, null, 1, arg -> arg[0]));
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="str">
        specialMethods.put(Kode.STRING, new KodeBuiltinFunction(Kode.STRING, interpreter, null, 1, arg -> Interpreter.toKodeValue("<object of '" + class_name + "'>")));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="number">
        specialMethods.put(Kode.NUMBER, new KodeBuiltinFunction(Kode.NUMBER, interpreter, null, 1, args -> {
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="list">
        specialMethods.put(Kode.LIST, new KodeBuiltinFunction(Kode.LIST, interpreter, null, 1, args -> {
            throw new NotImplemented();
        }));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="bool">
        specialMethods.put(Kode.BOOLEAN, new KodeBuiltinFunction(Kode.BOOLEAN, interpreter, null, 1, args -> Interpreter.toKodeValue(Interpreter.isTruthy(true))));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="neg">
        specialMethods.put(Kode.NEG, new KodeBuiltinFunction(Kode.NEG, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) This)) {
                    This = Interpreter.toKodeValue(ValueBool.toBoolean(This) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) This)) {
                    return Interpreter.toKodeValue(KodeMath.neg(ValueNumber.toNumber(This)));
                }
            }
            throw new NotImplemented();
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="pos">
        specialMethods.put(Kode.POS, new KodeBuiltinFunction(Kode.POS, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) This)) {
                    This = Interpreter.toKodeValue(ValueBool.toBoolean(This) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) This)) {
                    return Interpreter.toKodeValue(KodeMath.pos(ValueNumber.toNumber(This)));
                }
            }
            throw new NotImplemented();
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="getItem">
        specialMethods.put(Kode.GET_ITEM, new KodeBuiltinFunction(Kode.GET_ITEM, interpreter, null, 2, args -> {
            KodeObject This = args[0];
            KodeObject index = args[1];
            if (This instanceof KodeInstance && index instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) index)) {
                    int toNumber;
                    try {
                        toNumber = ValueNumber.toNumber(index).getAsIndex();
                    } catch (RuntimeError ex) {
                        throw ex;
                    } catch (Throwable ex) {
                        throw new RuntimeError("Index Out of Range or has fractional part.");
                    }
                    if (ValueList.isList((KodeInstance) This)) {
                        try {
                            return Interpreter.toKodeValue(ValueList.toList(This).get(toNumber));
                        } catch (IndexOutOfBoundsException e) {
                            throw new RuntimeError("List Index Out Of Bound : " + Kode.stringify(toNumber), null);
                        }
                    }
                    if (ValueString.isString((KodeInstance) This)) {
                        try {
                            return Interpreter.toKodeValue(ValueString.toStr(This).charAt(toNumber));
                        } catch (IndexOutOfBoundsException e) {
                            throw new RuntimeError("String Index Out Of Bound : " + Kode.stringify(toNumber), null);
                        }
                    }
                }
            }
            throw new NotImplemented();
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="setItem">
        specialMethods.put(Kode.SET_ITEM, new KodeBuiltinFunction(Kode.SET_ITEM, interpreter, null, 3, args -> {
            KodeObject This = args[0];
            KodeObject index = args[1];
            if (This instanceof KodeInstance && index instanceof KodeInstance) {
                if (ValueNumber.isNumber((KodeInstance) index)) {
                    int toNumber;
                    try {
                        toNumber = ValueNumber.toNumber(index).getAsIndex();
                    } catch (RuntimeError ex) {
                        throw ex;
                    } catch (Throwable ex) {
                        throw new RuntimeError("Index Out of Range or has fractional part.");
                    }
                    if (ValueList.isList((KodeInstance) This)) {
                        try {
                            ValueList.toList(This).set(toNumber, args[2]);
                            return null;
                        } catch (IndexOutOfBoundsException e) {
                            throw new RuntimeError("List Index Out Of Bound : " + Kode.stringify(toNumber));
                        }
                    }
                }
            }
            throw new NotImplemented();
        }));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="len">
        specialMethods.put(Kode.LEN, new KodeBuiltinFunction(Kode.LEN, interpreter, null, 1, args -> {
            KodeObject This = args[0];
            if (This instanceof KodeInstance) {
                if (ValueString.isString((KodeInstance) This)) {
                    return Interpreter.toKodeValue(ValueString.toStr(This).length());
                }
                if (ValueList.isList((KodeInstance) This)) {
                    return Interpreter.toKodeValue(ValueList.toList(This).size());
                }
            }
            throw new NotImplemented();
        }));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="add">
        specialMethods.put(Kode.ADD, new KodeBuiltinFunction(Kode.ADD, interpreter, null, 2, args -> {
            KodeObject left = args[0];
            KodeObject right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                    return Interpreter.toKodeValue(ValueString.toStr(left).concat(ValueString.toStr(right)));
                } else if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                    List<KodeObject> ll = new ArrayList<>();
                    ll.addAll(ValueList.toList(left));
                    ll.addAll(ValueList.toList(right));
                    return Interpreter.toKodeValue(ll);
                } else {
                    if (ValueBool.isBool((KodeInstance) left)) {
                        left = Interpreter.toKodeValue(ValueBool.toBoolean(left) ? 1 : 0);
                    }
                    if (ValueBool.isBool((KodeInstance) right)) {
                        right = Interpreter.toKodeValue(ValueBool.toBoolean(right) ? 1 : 0);
                    }
                    if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                        return Interpreter.toKodeValue(KodeMath.add(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    }
                }
            }
            throw new NotImplemented();
        }));
        specialMethods.put(Kode.RADD, new KodeBuiltinFunction(Kode.RADD, interpreter, null, 2, args -> findMethod(Kode.ADD).call(args[1], args[0])));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="sub">
        specialMethods.put(Kode.SUB, new KodeBuiltinFunction(Kode.SUB, interpreter, null, 2, args -> {
            KodeObject left = args[0];
            KodeObject right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = Interpreter.toKodeValue(ValueBool.toBoolean(left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = Interpreter.toKodeValue(ValueBool.toBoolean(right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    return Interpreter.toKodeValue(KodeMath.subtract(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                }
            }
            throw new NotImplemented();
        }));
        specialMethods.put(Kode.RSUB, new KodeBuiltinFunction(Kode.RSUB, interpreter, null, 2, args -> findMethod(Kode.SUB).call(args[1], args[0])));
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="mul">
        specialMethods.put(Kode.MUL, new KodeBuiltinFunction(Kode.MUL, interpreter, null, 2, args -> {
            KodeObject left = args[0];
            KodeObject right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = Interpreter.toKodeValue(ValueBool.toBoolean(left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = Interpreter.toKodeValue(ValueBool.toBoolean(right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    return Interpreter.toKodeValue(KodeMath.multiply(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                }
                if (ValueString.isString((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    String str = "";
                    try {
                        for (int i = 0; i < ValueNumber.toNumber(right).getAsIndex(); i++) {
                            str = str.concat(ValueString.toStr(left));
                        }
                    } catch (Exception ex) {
                        throw new NotImplemented();
                    }
                    return Interpreter.toKodeValue(str);
                }
                if (ValueString.isString((KodeInstance) right) && ValueNumber.isNumber((KodeInstance) left)) {
                    String str = "";
                    try {
                        for (int i = 0; i < ValueNumber.toNumber(left).getAsIndex(); i++) {
                            str = str.concat(ValueString.toStr(right));
                        }
                    } catch (Exception ex) {
                        throw new NotImplemented();
                    }
                    return Interpreter.toKodeValue(str);
                }
                if (ValueList.isList((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    List<Object> ll = new ArrayList<>();
                    try {
                        for (int i = 0; i < ValueNumber.toNumber(right).getAsIndex(); i++) {
                            ll.addAll(ValueList.toList(left));
                        }
                    } catch (Exception ex) {
                        throw new NotImplemented();
                    }
                    return Interpreter.toKodeValue(ll);
                }
                if (ValueList.isList((KodeInstance) right) && ValueNumber.isNumber((KodeInstance) left)) {
                    List<Object> ll = new ArrayList<>();
                    try {
                        for (int i = 0; i < ValueNumber.toNumber(left).getAsIndex(); i++) {
                            ll.addAll(ValueList.toList(right));
                        }
                    } catch (Exception ex) {
                        throw new NotImplemented();
                    }
                    return Interpreter.toKodeValue(ll);
                }
            }
            throw new NotImplemented();
        }));
        specialMethods.put(Kode.RMUL, new KodeBuiltinFunction(Kode.RMUL, interpreter, null, 2, args -> findMethod(Kode.MUL).call(args[1], args[0])));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="true_div">
        specialMethods.put(Kode.TRUE_DIV, new KodeBuiltinFunction(Kode.TRUE_DIV, interpreter, null, 2, args -> {
            KodeObject left = args[0];
            KodeObject right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = Interpreter.toKodeValue(ValueBool.toBoolean(left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = Interpreter.toKodeValue(ValueBool.toBoolean(right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    try {
                        return Interpreter.toKodeValue(KodeMath.divide(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    } catch (ArithmeticException ex) {
                        throw new RuntimeError(ex.getMessage());
                    }
                }
            }
            throw new NotImplemented();
        }));
        specialMethods.put(Kode.RTRUE_DIV, new KodeBuiltinFunction(Kode.RTRUE_DIV, interpreter, null, 2, args -> findMethod(Kode.TRUE_DIV).call(args[1], args[0])));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="floor_div">
        specialMethods.put(Kode.FLOOR_DIV, new KodeBuiltinFunction(Kode.FLOOR_DIV, interpreter, null, 2, args -> {
            KodeObject left = args[0];
            KodeObject right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = Interpreter.toKodeValue(ValueBool.toBoolean(left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = Interpreter.toKodeValue(ValueBool.toBoolean(right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    try {
                        return Interpreter.toKodeValue(KodeMath.floor_div(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    } catch (ArithmeticException ex) {
                        throw new RuntimeError(ex.getMessage());
                    }
                }
            }
            throw new NotImplemented();
        }));
        specialMethods.put(Kode.RFLOOR_DIV, new KodeBuiltinFunction(Kode.RFLOOR_DIV, interpreter, null, 2, args -> findMethod(Kode.FLOOR_DIV).call(args[1], args[0])));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="mod">
        specialMethods.put(Kode.MOD, new KodeBuiltinFunction(Kode.MOD, interpreter, null, 2, args -> {
            KodeObject left = args[0];
            KodeObject right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = Interpreter.toKodeValue(ValueBool.toBoolean(left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = Interpreter.toKodeValue(ValueBool.toBoolean(right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    try {
                        return Interpreter.toKodeValue(KodeMath.modulo(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    } catch (ArithmeticException ex) {
                        throw new RuntimeError(ex.getMessage());
                    }
                }
            }
            throw new NotImplemented();
        }));
        specialMethods.put(Kode.RMOD, new KodeBuiltinFunction(Kode.RMOD, interpreter, null, 2, args -> findMethod(Kode.MOD).call(args[1], args[0])));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="pow">
        specialMethods.put(Kode.POWER, new KodeBuiltinFunction(Kode.POWER, interpreter, null, 2, args -> {
            KodeObject left = args[0];
            KodeObject right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = Interpreter.toKodeValue(ValueBool.toBoolean(left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = Interpreter.toKodeValue(ValueBool.toBoolean(right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    try {
                        return Interpreter.toKodeValue(KodeMath.exponent(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    } catch (ArithmeticException ex) {
                        throw new NotImplemented();
                    }
                }
            }
            throw new NotImplemented();
        }));
        specialMethods.put(Kode.RPOWER, new KodeBuiltinFunction(Kode.RPOWER, interpreter, null, 2, args -> findMethod(Kode.POWER).call(args[1], args[0])));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="lshift">
        specialMethods.put(Kode.LSHIFT, new KodeBuiltinFunction(Kode.LSHIFT, interpreter, null, 2, args -> {
            KodeObject left = args[0];
            KodeObject right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = Interpreter.toKodeValue(ValueBool.toBoolean(left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = Interpreter.toKodeValue(ValueBool.toBoolean(right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    try {
                        return Interpreter.toKodeValue(KodeMath.lshift(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    } catch (ArithmeticException ex) {
                        throw new NotImplemented();
                    }
                }
            }
            throw new NotImplemented();
        }));
        specialMethods.put(Kode.RLSHIFT, new KodeBuiltinFunction(Kode.RLSHIFT, interpreter, null, 2, args -> findMethod(Kode.LSHIFT).call(args[1], args[0])));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="rshift">
        specialMethods.put(Kode.RSHIFT, new KodeBuiltinFunction(Kode.RSHIFT, interpreter, null, 2, args -> {
            KodeObject left = args[0];
            KodeObject right = args[1];
            if (left instanceof KodeInstance && right instanceof KodeInstance) {
                if (ValueBool.isBool((KodeInstance) left)) {
                    left = Interpreter.toKodeValue(ValueBool.toBoolean(left) ? 1 : 0);
                }
                if (ValueBool.isBool((KodeInstance) right)) {
                    right = Interpreter.toKodeValue(ValueBool.toBoolean(right) ? 1 : 0);
                }
                if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                    try {
                        return Interpreter.toKodeValue(KodeMath.rshift(ValueNumber.toNumber(left), ValueNumber.toNumber(right)));
                    } catch (ArithmeticException ex) {
                        throw new NotImplemented();
                    }
                }
            }
            throw new NotImplemented();
        }));
        specialMethods.put(Kode.RRSHIFT, new KodeBuiltinFunction(Kode.RRSHIFT, interpreter, null, 2, args -> findMethod(Kode.RSHIFT).call(args[1], args[0])));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="eq">
        specialMethods.put(Kode.EQ, new KodeBuiltinFunction(Kode.EQ, interpreter, null, 2, args -> Interpreter.toKodeValue(Comparator.eq(args[0], args[1], interpreter))));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="ne">
        specialMethods.put(Kode.NE, new KodeBuiltinFunction(Kode.NE, interpreter, null, 2, args -> Interpreter.toKodeValue(Comparator.ne(args[0], args[1], interpreter))));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="lt">
        specialMethods.put(Kode.LT, new KodeBuiltinFunction(Kode.LT, interpreter, null, 2, args -> Interpreter.toKodeValue(Comparator.lt(args[0], args[1], interpreter))));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="le">
        specialMethods.put(Kode.LE, new KodeBuiltinFunction(Kode.LE, interpreter, null, 2, args -> Interpreter.toKodeValue(Comparator.le(args[0], args[1], interpreter))));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="gt">
        specialMethods.put(Kode.GT, new KodeBuiltinFunction(Kode.GT, interpreter, null, 2, args -> Interpreter.toKodeValue(Comparator.gt(args[0], args[1], interpreter))));
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="ge">
        specialMethods.put(Kode.GE, new KodeBuiltinFunction(Kode.GE, interpreter, null, 2, args -> Interpreter.toKodeValue(Comparator.ge(args[0], args[1], interpreter))));
        //</editor-fold>
    }

    @Override
    public boolean isBind() {
        return true; // A class whenever called gets bound with a new object by default.
    }

    /**
     * Retrieves a method definition by using its name from a class or its object.
     *
     * @param name Name of the method.
     * @return Returns the associated method definition.
     */
    KodeFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
        }

        if (specialMethods.containsKey(name)) {
            return specialMethods.get(name);
        }

        return null;
    }

    @Override
    public String toString() {
        return "<class '" + class_name + "'>";
    }

    @Override
    public KodeObject __call__(KodeObject... arguments) {
        // Generation of new Instance
        KodeInstance instance = new KodeInstance(this);

        // Constructor Validation Call
        ArrayList<KodeObject> asList = new ArrayList<>(Arrays.asList(arguments));
        asList.add(0, this);
        asList.add(1, instance);
        for (KodeClass klass = this.superclass; klass != null; klass = klass.superclass) {
            if (klass.methods.containsKey(Kode.INIT_SUBCLASS)) {
                klass.methods.get(Kode.INIT_SUBCLASS).call(asList.toArray(arguments));
            }
        }

        // Constructor Call
        this.findMethod(Kode.INIT).bind(instance).call(arguments);

        // Return the new generated instance of the class
        return instance;
    }

    @Override
    public int arity() {
        // Same as of its constructor.
        KodeFunction initializer = findMethod(Kode.INIT);
        if (initializer == null) {
            return 0;
        }
        return initializer.arity();
    }

    @Override
    public KodeObject get(String name) {
        throw new RuntimeError("Not supported yet.");
    }

    @Override
    public void set(String name, KodeObject value) {
        throw new RuntimeError("Not supported yet.");
    }

}
